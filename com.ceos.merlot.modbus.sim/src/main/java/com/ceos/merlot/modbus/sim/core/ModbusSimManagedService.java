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

package com.ceos.merlot.modbus.sim.core;

import com.ceos.merlot.modbus.dev.api.ModbusDeviceArray;
import com.ceos.merlot.modbus.sim.api.ModbusSim;
import com.ceos.merlot.modbus.sim.impl.ModbusSimFuncImpl;
import com.ceos.merlot.modbus.sim.impl.ModbusSimRandomImpl;
import com.ceos.merlot.modbus.sim.impl.ModbusSimSignalImpl;
import com.ceos.merlot.scheduler.api.Job;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Properties;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author cgarcia
 */
public class ModbusSimManagedService implements ManagedService {
    
    private final BundleContext bundleContext;
    private final ModbusDeviceArray mbdevs;
    
    public ModbusSimManagedService(BundleContext bundleContext, ModbusDeviceArray mbdevs) {
        this.bundleContext = bundleContext;
        this.mbdevs = mbdevs;
    }  
    
    public void init(){        
    }
    
    public void destroy(){ 
        try {            
            ServiceReference[] services = bundleContext.getServiceReferences(ModbusSim.class.getName(), null);
            if (services !=null){
                for(ServiceReference service:services){
                    ModbusSim mbsim = (ModbusSim) bundleContext.getService(service);
                    mbsim.stop();
                    mbsim.destroy();                   
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }        
    }    

    @Override
    public void updated(Dictionary props) throws ConfigurationException {

        if (props !=null){
            Enumeration<String> keys = props.keys();
            String key = null;            
            try {            
                ServiceReference[] services = bundleContext.getServiceReferences(ModbusSim.class.getName(), null);
                if (services !=null){
                    for(ServiceReference service:services){
                        ModbusSim mbsim = (ModbusSim) bundleContext.getService(service);
                        mbsim.stop();
                        mbsim.destroy();
                        service.getBundle().uninstall();
                        bundleContext.ungetService(service);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            
            while(keys.hasMoreElements()) {
                key = keys.nextElement();  

                if (!("felix.fileinstall.filename".equalsIgnoreCase(key)) &&
                    !("service.pid".equalsIgnoreCase(key))){

                    try {
                        ModbusSim mbsim = null;
                                                
                        String[] fields = ((String)props.get(key)).split(",");
                        int device = Integer.parseInt(fields[0]);                        
                        Long scan = Long.parseLong(fields[2]);
                        if ("rand".equalsIgnoreCase(fields[1])){
                            if (mbdevs.getModbusDevice(device) != null){
                                mbsim = new ModbusSimRandomImpl(mbdevs.getModbusDevice(device));
                                String[] address ={fields[3]};
                                mbsim.setVariables(address);
                            }
                        } else if ("func".equalsIgnoreCase(fields[1])){
                            if (mbdevs.getModbusDevice(device) != null){
                                mbsim = new ModbusSimFuncImpl(mbdevs.getModbusDevice(device));
                                String[] address ={fields[3],fields[4],fields[5],fields[6]};
                                mbsim.setVariables(address);
                                mbsim.setFunction(fields[7]);                             
                            }                            
                        } else if ("signal".equalsIgnoreCase(fields[1])){
                            if (mbdevs.getModbusDevice(device) != null){
                                mbsim = new ModbusSimSignalImpl(mbdevs.getModbusDevice(device));
                                String[] address ={fields[3],fields[4],fields[5],fields[6]};
                                mbsim.setVariables(address);
                                mbsim.setFunction(fields[7]); 
                                scan = 100L; //alway 100 ms
                            }                                         
                        }
                                                
                        Dictionary osgiprops = new Properties();                        
                        osgiprops.put("scheduler.name", "ModbusSim:" + key);
                        osgiprops.put("scheduler.period", scan);
                        //properties.put("scheduler.times", 5);
                        osgiprops.put("scheduler.immediate", true);
                        osgiprops.put("scheduler.concurrent", false);  
                        if (mbsim != null) {
                            mbsim.init();                          
                            bundleContext.registerService(new String[]{ModbusSim.class.getName(), Job.class.getName()}, mbsim, osgiprops);                         
                            mbsim.start();                              
                        }
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
}



    

