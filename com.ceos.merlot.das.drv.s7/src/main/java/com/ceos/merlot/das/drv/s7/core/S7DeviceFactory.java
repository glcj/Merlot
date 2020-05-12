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

package com.ceos.merlot.das.drv.s7.core;

import com.ceos.merlot.das.drv.basic.api.BasicDevice;
import com.ceos.merlot.das.drv.basic.api.BasicDeviceFactory;
import com.ceos.merlot.das.drv.s7.api.S7Device;
import com.ceos.merlot.das.drv.s7.impl.S7DeviceImpl;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;

/**
 *
 * @author cgarcia
 */
public class S7DeviceFactory implements BasicDeviceFactory {

    private BundleContext bundleContext;

    public S7DeviceFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
            
    @Override
    public BasicDevice create(String serial, String url, String description) {
        Dictionary<String,String> properties = new Hashtable();   
        properties.put(Constants.DEVICE_CATEGORY, S7DeviceImpl.S7_DEVICE_CATEGORY);             
        properties.put(Constants.DEVICE_DESCRIPTION, description);
        properties.put(Constants.DEVICE_SERIAL, serial);
            
        try{
            S7Device s7device = new S7DeviceImpl(bundleContext);
            s7device.setUrl(url);
            bundleContext.registerService(Device.class.getName(), s7device, properties); 
            return s7device;
        } catch (Exception ex) {
            return null;
        }

    }
    
}
