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

package com.ceos.merlot.das.drv.s7.impl;

import com.ceos.merlot.das.drv.s7.api.S7Device;
import com.ceos.merlot.das.drv.s7.api.S7Driver;
import com.ceos.merlot.scheduler.api.Job;
import java.util.Hashtable;
import org.apache.plc4x.java.api.PlcDriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class S7ReferringDriverImpl  implements Driver {
    private static final Logger LOGGER = LoggerFactory.getLogger(S7ReferringDriverImpl.class);
    private static final String COM_CEOS_S7_DEVICE_CATEGORY = "com.ceos.s7";
    private BundleContext bc;  
    private final EventAdmin eventAdmin;
    private PlcDriver plcdriver = null;

    public S7ReferringDriverImpl (BundleContext bc, EventAdmin eventAdmin) {
        this.bc = bc;
        this.eventAdmin = eventAdmin;
    }    
    
    
    @Override
    public int match(ServiceReference reference) throws Exception {
        //1. Detecta es un dispositivo s7       
        if ((reference != null) && (plcdriver != null)){
            if (reference.getProperty(Constants.DEVICE_CATEGORY) != null){
                if (COM_CEOS_S7_DEVICE_CATEGORY.matches(
                   (String) reference.getProperty(Constants.DEVICE_CATEGORY))){

                    BundleContext refbc = reference.getBundle().getBundleContext();
                    String filterdriver =  "(DRIVER_ID=" + (String) reference.getProperty(Constants.DEVICE_SERIAL) + ")"; 
                    ServiceReference[] refdrvs = refbc.getServiceReferences(Driver.class.getName(), filterdriver);
                    if (refdrvs == null) {
                        Hashtable properties = new Hashtable();
                        properties.put(Constants.DRIVER_ID, (String) reference.getProperty(Constants.DEVICE_SERIAL));                            
                        properties.put("scheduler.name", "S7Driver:" + (String) reference.getProperty(Constants.DEVICE_SERIAL));
                        properties.put("scheduler.period", 1000L);
                        //properties.put("scheduler.times", 5);
                        properties.put("scheduler.immediate", true);
                        properties.put("scheduler.concurrent", false);
                        
                        S7Driver s7drv = new S7DriverImpl(refbc);
                        s7drv.setPlcDriver(plcdriver);
                        //TODO: Lyfecycle, Where I destroy it?
                        s7drv.init();
                        S7Device device = (S7Device) bc.getService(reference);
                        //TODO: Change to "attach" method
                        device.attach(s7drv);
                        refbc.registerService(new String[]{Driver.class.getName(), Job.class.getName()}, s7drv, properties); 
                    }
                    return 1;
                }                                     
                
            }
        }
        return Device.MATCH_NONE;
    }

    @Override
    public String attach(ServiceReference reference) throws Exception {
        //TODO Here is the attach not in match
        return null;
    }
    
    public void bind(PlcDriver driver){
        this.plcdriver = driver;
    }
    
    public void unbind(ServiceReference driver){       
        this.plcdriver = null;
    }
    
    
}
