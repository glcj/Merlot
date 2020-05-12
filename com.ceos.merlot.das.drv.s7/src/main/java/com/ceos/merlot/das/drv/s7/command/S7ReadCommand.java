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

package com.ceos.merlot.das.drv.s7.command;

import com.ceos.merlot.api.DriverCallback;
import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.das.drv.s7.api.S7Device;
import com.ceos.merlot.das.drv.s7.impl.S7DeviceImpl;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.s7.model.S7Field;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;

/**
 *
 * @author cgarcia
 */
@Command(scope = "s7", name = "read", description = "Read a list of fields from PLC.")
@Service
public class S7ReadCommand implements Action, DriverCallback  {
    
    @Reference
    BundleContext bc;        
    
    @Reference
    Session session;

    @Reference
    SessionFactory sessionFactory;

    @Argument(index = 0, name = "serial", description = "S7 device serial.", required = true, multiValued = false)
    String serial;  
    
    @Argument(index = 1, name = "ref", description = "User reference value.", required = true, multiValued = false)
    String userRef;    

    @Argument(index = 2, name = "field", description = "S7 field to read.", required = true, multiValued = false)
    String userField;        
    
    
    @Override
    public Object execute() throws Exception {
        String filter =  "(&(|(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + Device.class.getName() + ")" +
            "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + com.ceos.merlot.api.Device.class.getName() + "))" +
            "(&(" + org.osgi.service.device.Constants.DEVICE_CATEGORY + "=" + S7DeviceImpl.S7_DEVICE_CATEGORY + ")" +
            "(" + org.osgi.service.device.Constants.DEVICE_SERIAL + "=" + serial + ")))";  

        ServiceReference[] refs = bc.getServiceReferences((String) null, filter);
        if (refs == null){
            System.out.println("Device not found. SERIAL: " + serial);
        } else {
            ServiceReference ref = refs[0];
            S7Device s7device = (S7Device) bc.getService(ref);
            s7device.ReadRequest(userRef, userField, this);
        }        
        return null;
        
    }

    @Override
    public void execute(DriverEvent cb) {
        try {
            PlcReadResponse response = cb.getPlcReadResponse();
            PlcReadRequest request = response.getRequest();
            S7Field requestField = (S7Field) request.getField(userRef);
            System.out.println(requestField.toString());

            switch (requestField.getDataType()){
            case BYTE:
                System.out.println("BYTE: " + response.getAllBytes(userRef) + "\r\n");
                break;
            case CHAR:
            case STRING:
                System.out.println("CHAR|STRING: " + response.getAllStrings(userRef) + "\r\n");
                break;
            case WORD:
            case USINT:
            case SINT:
            case UINT:
            case INT:
            case DINT:
                System.out.println("CHAR|STRING: " + response.getAllIntegers(userRef) + "\r\n");
                break;
            case UDINT:
            case ULINT:
            case LINT:
                System.out.println("CHAR|STRING: " + response.getAllLongs(userRef) + "\r\n");                
                break;
            case BOOL:
                System.out.println("DATE: " + response.getAllBooleans(userRef) + "\r\n");                 
                break;
            case REAL:
            case LREAL:
                System.out.println("REAL|LREAL: " + response.getAllFloats(userRef) + "\r\n");                
                break;
            case DT:
            case DATE_AND_TIME:
                System.out.println("DATE_AND_TIME: " + response.getAllDateTimes(userRef) + "\r\n");                  
                break;
            case DATE:
                System.out.println("DATE: " + response.getAllDates(userRef) + "\r\n");                 
                break;
            case S5TIME:
                System.out.println("S5TIME: " + response.getAllDuration(userRef) + "\r\n"); 
                break;
            case TIME:
                System.out.println("TIME: " + response.getAllDuration(userRef) + "\r\n");  
                break;
            case TOD:
            case TIME_OF_DAY:
                System.out.println("TIME_OF_DAY: " + response.getAllTimes(userRef) + "\r\n");
                break;               
            default:
                throw new NotImplementedException("The response type for datatype " + requestField.getDataType() + " is not yet implemented");                
            }
            
            
        } catch (Exception ex){
            System.out.println("S7ReadCommand: " + ex);
        }
    }
    
}
