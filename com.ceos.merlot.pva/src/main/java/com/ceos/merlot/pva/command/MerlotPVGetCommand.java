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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.epics.pvaClient.PvaClient;
import org.epics.pvaClient.PvaClientChannel;
import org.epics.pvaClient.PvaClientGet;
import org.epics.pvaClient.PvaClientMonitor;
import org.epics.pvaClient.PvaClientMonitorData;
import org.epics.pvaClient.PvaClientMonitorRequester;
import org.epics.pvdata.property.TimeStamp;
import org.epics.pvdata.pv.PVField;
import org.epics.pvdata.pv.PVStructure;

/**
 *
 * @author cgarcia
 */
@Command(scope = "pva", name = "pvget", description = "Get information about records.")
@Service
public class MerlotPVGetCommand implements Action, PvaClientMonitorRequester  {
    
    private PrintStream out;
    Map<PvaClientMonitor, String> pvMonitors = null;
        
    @Reference
    Session session;
    
    @Reference
    MerlotPvaClient pvaClientService;    
   
    @Option(name = "-v", aliases = "--view", description = "Show entire structure (implies Raw mode).", required = false, multiValued = false)
    boolean viewstruct;         
   
    @Option(name = "-r", aliases = "--request", description = "Request, specifies what fields to return and options, default is 'field(value)'.", required = false, multiValued = false)
    String request = "value,timeStamp";   
   
    @Option(name = "-w", aliases = "--wait", description = "Wait time, specifies timeout, default is 3.0 second(s).", required = false, multiValued = false)
    int wait = 3;   
   
    @Option(name = "-m", aliases = "--monitor", description = "Monitor .", required = false, multiValued = false)
    boolean monitor = false;     
   
    @Option(name = "-q", aliases = "--quiet", description = "Quiet mode, print only error messages.", required = false, multiValued = false)
    boolean quiet;   
   
    @Argument(index = 0, name = "channel", description = "Channel <PV name>", required = true, multiValued = true)
    List<String> strChannels = null;       

    @Override
    public Object execute() throws Exception {
        out = session.getConsole();
        PvaClientChannel  clientChannel = null;
        PvaClientGet getChannel = null;
        PVField pvField = null;
        TimeStamp ts = null;
        Date date = null;
        String strDate = null;
        
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        PvaClient pvaClient = pvaClientService.getPvaClient();
        if (pvaClient == null) {
            out.println("Global PvaClient is not deployed.");
            return null;
        }

        if (monitor) {
            if (strChannels != null) {
                MonitorChannels(strChannels);
            }            
        }  else {
            for (String strChannel:strChannels) {

                clientChannel =  pvaClient.createChannel(strChannel, "pva");
                clientChannel.connect(wait);

                getChannel = clientChannel.createGet(request);
                if (viewstruct) {
                    out.println(clientChannel.getChannel().getChannelName() 
                            + " " 
                            + getChannel.getData().getPVStructure());
                } else {
                    pvField = getChannel.getData().getValue();
                    ts = getChannel.getData().getTimeStamp();                    
                    date = new Date(ts.getMilliSeconds());
                    strDate = formatter.format(date);
                    out.println(strChannel + " " + pvField + " " + strDate);
                }

                clientChannel.destroy();
            }                
        }       
            
                    
        //pvaClient.clearRequester();
        //pva.destroy();
        
        return null;
    }
    
    private void MonitorChannels(List<String> strChannels ){
        PvaClient pvaClient = pvaClientService.getPvaClient();
        if (pvaClient == null) {
            out.println("Global PvaClient is not deployed.");
            return;
        }        
        PvaClientChannel[]  clientChannels = new PvaClientChannel[strChannels.size()];
        PvaClientGet[] getChannels = new PvaClientGet[strChannels.size()];
        PvaClientMonitor[] monitors  = new PvaClientMonitor[strChannels.size()];
        PVField pvField = null;
        this.pvMonitors = new HashMap();
        int i = 0;
        for (String strChannel:strChannels) {        
            monitors[i] =  pvaClient.createChannel(strChannel, "pva").monitor(request);
            monitors[i].setRequester(this);
            pvMonitors.put(monitors[i], strChannel);
            i++;
        }
        out.println("Ctrl-C to quit.");
        
        while(true){
            for (int j=0; j < i; j++){
                try {
                    monitors[j].waitEvent(0.0);
                } catch (Exception ex){
                    out.println("loop: " + ex);
                    for (int k=0; k < i; k++){
                        out.println("MonitorChannels: " + ex);
                        monitors[k].releaseEvent();
                        monitors[k].destroy();
                    }
                    break;
                } finally {
                    monitors[j].releaseEvent();
                };
            }
        }
        
    }

    @Override
    public void event(PvaClientMonitor monitor) {
        Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");        
        PvaClientMonitorData pvaData = monitor.getData();
        PVField pvField = null;
        TimeStamp ts = null;
        Date date = null;
        String strDate = null;
        
        if (pvaData.hasValue()) {
            String strFullName = pvaData.getScalarValue().getFullName();
            pvField = pvaData.getValue();
            PVStructure pvStructure = pvaData.getPVStructure();
            
            if (viewstruct) {
                out.println(pvMonitors.get(monitor) +  " " + pvStructure);
            } else {
                PVField pvTimeStampField = pvaData.getPVStructure().getSubField("timeStamp");
                strDate = "";
                if (pvTimeStampField != null){
                    ts = pvaData.getTimeStamp();
                    date = new Date(ts.getMilliSeconds());
                    strDate = formatter.format(date);                
                };
               out.println(pvMonitors.get(monitor) +  " " + pvField + " " + strDate);
            }
        } else {
            //System.out.println("Problem...");
        }     

    }
    

    
}
