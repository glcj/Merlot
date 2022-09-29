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

package com.ceos.merlot.das.drv.mb.impl;

import com.ceos.merlot.das.drv.basic.api.BasicDriverMBean;
import com.ceos.merlot.das.drv.basic.impl.BasicDriverMBeanImpl;
import com.ceos.merlot.das.drv.mb.api.MbDevice;
import com.ceos.merlot.das.drv.mb.api.MbDriver;
import com.ceos.merlot.scheduler.api.Job;
import java.util.Hashtable;
import org.apache.plc4x.java.api.PlcDriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.osgi.service.device.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author cgarcia
 */
public class MbReferringDriverImpl implements Driver{
    private static final Logger LOGGER = LoggerFactory.getLogger(MbReferringDriverImpl.class);
    private BundleContext bc;    
    private PlcDriver plcdriver = null;

    
    public MbReferringDriverImpl(BundleContext bc) {
        this.bc = bc;
    }
        
    @Override
    public int match(ServiceReference reference) throws Exception {
        //1. Detecta es un dispositivo modbus      
        if ((reference != null) && (plcdriver != null)){
            if (reference.getProperty(Constants.DEVICE_CATEGORY) != null){
                if (MbDeviceImpl.MB_DEVICE_CATEGORY.matches(
                   (String) reference.getProperty(Constants.DEVICE_CATEGORY))){

                    BundleContext refbc = reference.getBundle().getBundleContext();
                    String filterdriver =  "(DRIVER_ID=" + (String) reference.getProperty(Constants.DEVICE_SERIAL) + ")"; 
                    
                    ServiceReference[] refdrvs = refbc.getServiceReferences(Driver.class.getName(), filterdriver);
                    if (refdrvs == null) {
                        Hashtable properties = new Hashtable();
                        properties.put(Constants.DRIVER_ID, (String) reference.getProperty(Constants.DEVICE_SERIAL));                            
                        properties.put("scheduler.name", "ModbusDriver:" + (String) reference.getProperty(Constants.DEVICE_SERIAL));
                        properties.put("scheduler.period", 3000L);
                        //properties.put("scheduler.times", 5);
                        properties.put("scheduler.immediate", true);
                        properties.put("scheduler.concurrent", false);
                        
                        MbDriver mbdrv = new MbDriverImpl(refbc);
                        mbdrv.setPlcDriver(plcdriver);
                        //TODO: Lyfecycle, Where I destroy it?
                        mbdrv.init();
                        MbDevice device = (MbDevice) bc.getService(reference);
                        //TODO: Change to "attach" method
                        device.attach(mbdrv);
                        refbc.registerService(new String[]{Driver.class.getName(), Job.class.getName()}, mbdrv, properties); 
                        
                        //Add the MBean
                        Hashtable mbean_props = new Hashtable();
                        BasicDriverMBean mbean = new BasicDriverMBeanImpl(mbdrv);
                        String strProp  = "org.apache.karaf:type=merlot,name=" + MbDeviceImpl.MB_DEVICE_CATEGORY+",id="+(String) reference.getProperty(Constants.DEVICE_SERIAL);
                        mbean_props.put("jmx.objectname", strProp);
                        refbc.registerService(new String[]{BasicDriverMBean.class.getName()}, mbean, mbean_props);                        
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
