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
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;

/**
 *
 * @author cgarcia
 */
@Command(scope = "s7", name = "status", description = "Read status of PLC.")
@Service
public class S7DeviceStatusCommand implements Action  {

    @Reference
    BundleContext bc;     

    @Argument(index = 0, name = "serial", description = "S7 device serial.", required = true, multiValued = false)
    String serial;      
    
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
        }        
        return null;
    }
    
}
