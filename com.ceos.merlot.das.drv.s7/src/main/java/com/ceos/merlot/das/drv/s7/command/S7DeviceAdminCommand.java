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

import com.ceos.merlot.das.drv.s7.api.S7Device;
import com.ceos.merlot.das.drv.s7.impl.S7DeviceImpl;
import java.util.Dictionary;
import java.util.Hashtable;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
@Command(scope = "s7", name = "dev", description = "S7 Device admin.")
@Service
public class S7DeviceAdminCommand implements Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(S7DeviceAdminCommand.class);  
    
    @Reference
    BundleContext bc;       
    
    @Option(name = "-n", aliases = "--new", description = "New S7 device instance.", required = false, multiValued = false)
    boolean newdevice = false;
    
    @Option(name = "-s", aliases = "--start", description = "Stop and delete the S7 device.", required = false, multiValued = false)
    boolean start = false; 
    
    @Option(name = "-k", aliases = "--stop", description = "Stop the S7 device.", required = false, multiValued = false)
    boolean stop = false;       
    
    @Option(name = "-p", aliases = "--status", description = "S7 device connection status.", required = false, multiValued = false)
    boolean status = false;     
    
    @Option(name = "-d", aliases = "--del", description = "Stop and delete the S7 device.", required = false, multiValued = false)
    boolean delete = false;     
        
    @Argument(index = 0, name = "serial", description = "S7 device serial", required = true, multiValued = false)
    String serial;
    
    @Argument(index = 1, name = "url", description = "URL String to the S7 device.", required = false, multiValued = false)
    String url;      

    @Argument(index = 2, name = "description", description = "S7 device short description.", required = false, multiValued = false)
    String description;    
    
    @Argument(index = 3, name = "friendlydesc", description = "S7 device friendly description.", required = false, multiValued = false)
    String friendlydesc;    
    
                
    @Override
    public Object execute() throws Exception {
        Dictionary<String,String> properties = new Hashtable();
        System.out.println(newdevice);
        System.out.println(status);
        System.out.println(serial);
        System.out.println(url);
        System.out.println(description);
        System.out.println(friendlydesc);
        
  
        if (newdevice){
            
            properties.put(Constants.DEVICE_CATEGORY, S7DeviceImpl.S7_DEVICE_CATEGORY);             
            properties.put(Constants.DEVICE_DESCRIPTION, description);
            properties.put(Constants.DEVICE_SERIAL, serial);
            
            S7Device s7device = new S7DeviceImpl(bc);
            s7device.setUrl(url);
            bc.registerService(Device.class.getName(), s7device, properties);             
        } else if (start) {
            String filter =  "(&(|(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + Device.class.getName() + ")" +
                "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + com.ceos.merlot.api.Device.class.getName() + "))" +
                "(&(" + org.osgi.service.device.Constants.DEVICE_CATEGORY + "=" + S7DeviceImpl.S7_DEVICE_CATEGORY + ")" +
                "(" + org.osgi.service.device.Constants.DEVICE_SERIAL + "=" + serial + ")))";  

            ServiceReference[] refs = bc.getServiceReferences((String) null, filter);
            if (refs == null){
                System.out.println("S7 Device not found. SERIAL: " + serial);
            } else {
                ServiceReference ref = refs[0];
                S7Device s7device = (S7Device) bc.getService(ref);
                s7device.start();
            }            
        } else if (stop){
            String filter =  "(&(|(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + Device.class.getName() + ")" +
                "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + com.ceos.merlot.api.Device.class.getName() + "))" +
                "(&(" + org.osgi.service.device.Constants.DEVICE_CATEGORY + "=" + S7DeviceImpl.S7_DEVICE_CATEGORY + ")" +
                "(" + org.osgi.service.device.Constants.DEVICE_SERIAL + "=" + serial + ")))";            
            ServiceReference[] refs = bc.getServiceReferences((String) null, filter);
            if (refs == null){
                System.out.println("S7 Device not found. SERIAL: " + serial);
            } else {
                ServiceReference ref = refs[0];
                S7Device s7device = (S7Device) bc.getService(ref);
                s7device.stop();
            }                   
        } else if (status) {
            
        } else if (delete) {
            
        }
        
        return null;
    };
    
}
