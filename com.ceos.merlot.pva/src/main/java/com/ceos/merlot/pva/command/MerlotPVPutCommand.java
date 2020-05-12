/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.ceos.merlot.pva.command;

import com.ceos.merlot.pva.api.MerlotPvaClient;
import java.io.PrintStream;
import java.util.List;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.epics.pvaClient.PvaClient;
import org.epics.pvaClient.PvaClientChannel;
import org.epics.pvaClient.PvaClientPut;
import org.epics.pvaClient.PvaClientPutData;
import org.epics.pvdata.copy.CreateRequest;
import org.epics.pvdata.factory.ConvertFactory;
import org.epics.pvdata.pv.Convert;
import org.epics.pvdata.pv.PVScalar;
import org.epics.pvdata.pv.PVScalarArray;
import org.epics.pvdata.pv.PVString;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Type;

/**
 *
 * @author cgarcia
 */
@Command(scope = "pva", name = "pvput", description = "Retrieve information about servers.")
@Service
public class MerlotPVPutCommand implements Action {
            
    private final Convert convert = ConvertFactory.getConvert();
    
    private PrintStream out;
    
    @Reference
    Session session;
    
    @Reference
    MerlotPvaClient pvaClientService;     
    
    @Option(name = "-r", aliases = "--request", description = "Request, specifies what fields to return and options, default is 'field(value)'.", required = false, multiValued = false)
    String request="value,timeStamp"; 
    
    @Option(name = "-w", aliases = "--wait", description = "Wait time, specifies timeout, default is 3.0 second(s).", required = false, multiValued = false)
    boolean wait;
    
    @Option(name = "-t", aliases = "--terse", description = "Terse mode - print only successfully written value, without names.", required = false, multiValued = false)
    boolean terse;    

    @Option(name = "-v", aliases = "--view", description = "Show entire structure.", required = false, multiValued = false)
    boolean verbose;

    @Option(name = "-f", aliases = "--file", description = "Use <input file> as an input that provides a list PV name(s) to be read, use '-' for stdin.", required = false, multiValued = false)
    String file;

    @Option(name = "-n", aliases = "--enum", description = "Force enum interpretation of values as numbers (default is enum string).", required = false, multiValued = false)
    boolean blnenum;  

    @Option(name = "-s", aliases = "--string", description = "Force enum interpretation of values as string).", required = false, multiValued = false)
    boolean blnstring;

    @Argument(index = 0, name = "channel", description = "Channel <PV name>", required = true, multiValued = false)
    String strChannel = null; 

    @Argument(index = 1, name = "value", description = "Value(s) to channel.", required = true, multiValued = true)
    List<String> strValues = null;     

    @Override
    public Object execute() throws Exception {
        out = session.getConsole();
        Type type  = null;
        PvaClient pvaClient = pvaClientService.getPvaClient();
	CreateRequest createRequest = CreateRequest.create();
      
        if (pvaClient == null) {
            out.println("Global PvaClient is not deployed.");
            return null;
        }       

        PvaClientChannel chClient = pvaClient.createChannel(strChannel, "pva"); 
        
        PVStructure pvRequest = createRequest.createRequest(request);          
        PvaClientPut putClient = chClient.createPut(pvRequest);
        PvaClientPutData putData = putClient.getData();
        
        if (putData.isValueScalar()){
            putScalar(chClient, putData, strValues);
        } else if (putData.isValueScalarArray()) {
            putScalarArray(chClient, putData, pvRequest, strValues);
        } else {
            out.println("Type not supported.");
        }
                
        putClient.put();
        
        chClient.destroy();
        return null;
    }
    
    private void putScalar(PvaClientChannel chClient, PvaClientPutData putData, List<String> strValues){
        PVScalar pvScalar = putData.getScalarValue();
        ScalarType scalarType = pvScalar.getScalar().getScalarType();
        try {
            putData.putString(strValues.get(0));
        } catch (Exception ex) {
            out.println("Can't write scalar" + ex);
        }
        
    }
    
    private void putScalarArray(PvaClientChannel chClient, PvaClientPutData putData, PVStructure pvRequest, List<String> strValues) {
        int ini = 0;
        int end = 0;        
        PVScalarArray pvScalarArray = putData.getScalarArrayValue();

                        
        PVString options = pvRequest.getStringField("field.value._options.array");
        
        out.println("Old: " + pvScalarArray);
        
        try {
            String[] values = new String[strValues.size()];
            for (int i=0; i<strValues.size(); i++){
                values[i] = strValues.get(i);
            }  
            
            if (options == null) {
                ini = 0;
                end = values.length;
            } else {
                String[] strIniEnd = options.get().split(":");
                if (strIniEnd.length >= 2) {
                    ini = Integer.parseInt(strIniEnd[0]);
                    end = Integer.parseInt(strIniEnd[1]); 
                }
            }            
            
            if (pvScalarArray.getLength() >= (ini + end)){ 
                convert.fromStringArray(pvScalarArray, ini, end, values, 0);
            } else {
                out.println("Can't write scalar array, too many values.");
            }
            
            out.println("New: " + pvScalarArray);
            
        } catch (Exception ex) {
            out.println("Can't write scalar array " + ex);
        }       
    }
    
}
