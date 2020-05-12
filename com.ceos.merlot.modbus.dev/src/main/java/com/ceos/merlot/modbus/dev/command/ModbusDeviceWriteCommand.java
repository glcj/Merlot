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

import com.ceos.merlot.modbus.dev.api.ModbusDevice;
import com.ceos.merlot.modbus.dev.api.ModbusDeviceArray;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@Command(scope = "modbus", name = "write", description = "List modbus device")
@Service
public class ModbusDeviceWriteCommand implements Action {

    @Reference
    BundleContext bundleContext;
    
    @Argument(index = 0, name = "uid", description = "The device unit identifier.", required = true, multiValued = false)
    int uid;
    
    //@Option(name = "-r", aliases = { "--reg" }, description = "The data bank to dump.", required = true, multiValued = false)
    @Argument(index = 1, name = "type", description = "1:Digital register, 2:Coil, 3:Input register, 4:Holding register.", required = true, multiValued = false)    
    int type;
    
    //@Option(name = "-s", aliases = { "--start" }, description = "The initial register.", required = true, multiValued = false)
    @Argument(index = 2, name = "register", description = "Register address in the device.", required = true, multiValued = false)     
    int register;

    //@Option(name = "-l", aliases = { "--length" }, description = "Number of registers to dump.", required = true, multiValued = false)
    @Argument(index = 3, name = "value", description = "Value to write.", required = true, multiValued = false)     
    short value;     
    
    Boolean boolValue = false;
    
	public Object execute() throws Exception {

        try {
            //Bundle bundle = bundleContext.getSe
        	ServiceReference<?> reference = bundleContext.getServiceReference(ModbusDeviceArray.class.getName());
        	ModbusDeviceArray mdbarray = (ModbusDeviceArray) bundleContext.getService(reference);
        	ModbusDevice mbd =  (ModbusDevice) mdbarray.getModbusDevicesArray()[uid];
        	if (mbd != null) {         		
        		switch (type) {
        			case 1: boolValue = (value > 0)?true:false;
        					mbd.setDiscreteInput(register, boolValue);
        					break;
        			case 2: boolValue = (value > 0)?true:false;
        					mbd.setCoil(register, boolValue);
        					break;
        			case 3: mbd.setInputRegister(register,  value);
        					break;
        			case 4: mbd.setHoldingRegister(register, value);
        					break;
        			default: System.out.println("No data �rea deine.");
        					break;
        		}
        	};
        } catch (NumberFormatException ex) {
            // It was not a number, so ignore.
        }
		return null;
	}

}
