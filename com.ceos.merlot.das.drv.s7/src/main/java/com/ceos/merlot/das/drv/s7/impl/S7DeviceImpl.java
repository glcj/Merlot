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

import com.ceos.merlot.api.Driver;
import com.ceos.merlot.api.DriverCallback;
import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.das.drv.basic.impl.BasicDeviceImpl;
import com.ceos.merlot.das.drv.s7.api.S7Device;
import com.ceos.merlot.das.drv.s7.api.S7Driver;
import java.util.List;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class S7DeviceImpl extends BasicDeviceImpl implements S7Device {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(S7DeviceImpl.class);    
    public static final String S7_DEVICE_CATEGORY = "com.ceos.s7";
    
    private S7Driver devDriver = null;
    
    public S7DeviceImpl(BundleContext bc) {
        super(bc);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }    
    
    @Override
    public void destroy() throws Exception {
        super.destroy();
    }

    @Override
    public void start() {     
        LOGGER.info("Starting S7Device ("+url+")");
        if (devDriver != null){
            devDriver.setUrl(url);
            devDriver.start();
            autostart = false;
        } else {
            LOGGER.info("S7Device the S7Driver is null.");
            autostart = true;
        }
    }
       
    @Override
    public void stop() {
        if (devDriver != null){
            devDriver.stop();
        }        
    }
    
    @Override
    public void noDriverFound() {
        super.noDriverFound();
    }

    @Override
    public void attach(Driver driver) {    
        this.devDriver = (S7Driver) driver;
        if (autostart) start();
    }

    //TODO Chequeo de excepciones
    @Override
    public void ReadRequest(String index, String item, DriverCallback cb) {
        super.ReadRequest(index, item, cb);
    }    
    
    @Override
    public void WriteRequest(String scalar, String id, List<String> values, DriverCallback cb) {
        super.WriteRequest(scalar,id,values, cb);
    }

    //TODO Descargar la información devuelta en la consola
    @Override
    public void execute(DriverEvent cb) {
        super.execute(cb);
        PlcReadResponse plcResponse = cb.getPlcReadResponse();
        if (plcResponse != null) {
            plcResponse.getFieldNames().forEach( fieldname ->{
                System.out.println("\r\nS7DeviceImpl FieldName: " + fieldname);
            });
        };
    }    
    
}
