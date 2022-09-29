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

package com.ceos.merlot.das.drv.mb.command;

import com.ceos.merlot.api.DriverCallback;
import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.das.drv.mb.api.MbDevice;
import com.ceos.merlot.das.drv.mb.impl.MbDeviceImpl;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;

/**
 *
 * @author cgarcia
 */
@Command(scope = "mb", name = "write", description = "Write one field to the PLC.")
@Service
public class MbWriteCommand implements Action, DriverCallback    {
    
    @Reference
    BundleContext bc;        
    
    @Reference
    Session session;

    @Reference
    SessionFactory sessionFactory;

    @Argument(index = 0, name = "serial", description = "Device serial (like DEVICE_ID).", required = true, multiValued = false)
    String serial;  
    
    @Argument(index = 1, name = "scalartype", description = "Scalar type ( ScalarType).", required = true, multiValued = false)
    String scalarType;    

    @Argument(index = 2, name = "field", description = "Device field to read.", required = true, multiValued = false)
    String userField;  
    
    @Argument(index = 3, name = "value", description = "Value or values to write.", required = true, multiValued = true)
    List<String> values;  
    
    @Override
    public Object execute() throws Exception {
        String filter =  "(&(|(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + Device.class.getName() + ")" +
            "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + com.ceos.merlot.api.Device.class.getName() + "))" +
            "(&(" + org.osgi.service.device.Constants.DEVICE_CATEGORY + "=" + MbDeviceImpl.MB_DEVICE_CATEGORY + ")" +
            "(" + org.osgi.service.device.Constants.DEVICE_SERIAL + "=" + serial + ")))";
        
        ServiceReference[] refs = bc.getServiceReferences((String) null, filter);
        
        if (refs == null){
            System.out.println("Device not found. SERIAL: " + serial);
        } else {
            ServiceReference ref = refs[0];
            MbDevice mbdevice = (MbDevice) bc.getService(ref);
            mbdevice.WriteRequest(scalarType, userField, values, this);            
        }                
        return null;
    }
    
    @Override
    public void execute(DriverEvent cb) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");        
        try{
            PlcWriteResponse response = cb.getPlcWriteResponse();
            Collection<String> fields = response.getFieldNames();
            String fieldname = (String) fields.toArray()[0];
            System.out.println(fieldname + "/Response@["+LocalTime.now().format(dtf)+"]: " + response.getResponseCode(fieldname));
            System.out.println("\r\n");
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }    

    
}
