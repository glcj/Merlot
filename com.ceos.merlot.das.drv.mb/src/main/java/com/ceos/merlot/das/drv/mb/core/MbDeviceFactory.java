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

package com.ceos.merlot.das.drv.mb.core;

import com.ceos.merlot.das.drv.basic.api.BasicDevice;
import com.ceos.merlot.das.drv.basic.api.BasicDeviceFactory;
import com.ceos.merlot.das.drv.mb.api.MbDevice;
import com.ceos.merlot.das.drv.mb.impl.MbDeviceImpl;
import java.util.Dictionary;
import java.util.Hashtable;
import org.osgi.framework.BundleContext;
import org.osgi.service.device.Constants;
import org.osgi.service.device.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class MbDeviceFactory implements BasicDeviceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(MbDeviceFactory.class); 
    private BundleContext bundleContext;

    public MbDeviceFactory(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
            
    @Override
    public BasicDevice create(String serial, String url, String description) {
        Dictionary<String,String> properties = new Hashtable();   
        properties.put(Constants.DEVICE_CATEGORY, MbDeviceImpl.MB_DEVICE_CATEGORY);             
        properties.put(Constants.DEVICE_DESCRIPTION, description);
        properties.put(Constants.DEVICE_SERIAL, serial);
            
        try{
            MbDevice mbDevice = new MbDeviceImpl(bundleContext);
            mbDevice.setUrl(url);
            bundleContext.registerService(Device.class.getName(), mbDevice, properties); 
            return mbDevice;
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
        return null;
    }
        
}
