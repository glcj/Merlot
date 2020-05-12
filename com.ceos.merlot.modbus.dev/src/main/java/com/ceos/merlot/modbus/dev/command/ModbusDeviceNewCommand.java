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

package com.ceos.merlot.modbus.dev.command;

import java.util.Hashtable;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;

import com.ceos.merlot.modbus.dev.api.ModbusDevice;
import com.ceos.merlot.modbus.dev.impl.ModbusDeviceImpl;

@Command(scope = "modbus", name = "new", description = "Create a new modbus device.")
@Service
public class ModbusDeviceNewCommand implements Action {
	
    @Reference
    BundleContext bc;
    
    private ModbusDevice mbd = null;
    
	public Object execute() throws Exception {
		Hashtable<String, String> deviceProps = new Hashtable<String, String>();
		
	    deviceProps.put(org.osgi.service.device.Constants.DEVICE_CATEGORY, "mb1");
	    deviceProps.put(org.osgi.service.device.Constants.DEVICE_DESCRIPTION, "From command line");	
	    deviceProps.put(org.osgi.service.device.Constants.DRIVER_ID, "mb1");	 
	    deviceProps.put(org.osgi.service.device.Constants.DEVICE_SERIAL, "AB12");
	    deviceProps.put(org.osgi.framework.Constants.SERVICE_PID, "my.device.tuner");
	    
	    ModbusDeviceImpl mbdi = new ModbusDeviceImpl(100,100,100,100);
	    mbdi.setUnitIdentifier((byte) 123);
		
	    bc.registerService(new String[] {ModbusDevice.class.getName()}, mbdi, deviceProps);
	    
	    System.out.println("Dispositivo registrado");
      
		return null;
	}

}
