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
import com.ceos.merlot.modbus.dev.core.ModbusDeviceHelper;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.epics.pvdata.pv.ScalarType;
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
    @Argument(index = 1, name = "type", description = "0:Digital output coil, 1:Digital input Coil, 3:Input register, 4:Holding register.", required = true, multiValued = false)    
    int registerType = -1;
    
    //@Option(name = "-s", aliases = { "--start" }, description = "The initial register.", required = true, multiValued = false)
    @Argument(index = 2, name = "register", description = "Register address in the device.", required = true, multiValued = false)     
    int register;
    
    @Argument(index = 3, name = "scalar", description = "Scalar for the value.", required = true, multiValued = false)     
    String scalar;    

    //@Option(name = "-l", aliases = { "--length" }, description = "Number of registers to dump.", required = true, multiValued = false)
    @Argument(index = 4, name = "value", description = "Value to write.", required = true, multiValued = false)     
    double value = 0.0;     
    
    @Option(name = "-l", aliases = { "--le" }, description = "Use Little Endian format.", required = false, multiValued = false)    
    boolean blnLE = false;
    
    Boolean boolValue = false;
    
    public Object execute() throws Exception {

        try {
            ServiceReference<?> reference = bundleContext.getServiceReference(ModbusDeviceArray.class.getName());
            ModbusDeviceArray mdbarray = (ModbusDeviceArray) bundleContext.getService(reference);
            ModbusDevice mbd =  (ModbusDevice) mdbarray.getModbusDevicesArray()[uid];
            ScalarType valueScalar = ScalarType.getScalarType(scalar);
                       
            ModbusDeviceHelper.putValue(value, mbd, valueScalar, registerType, register, blnLE);

        } catch (NumberFormatException ex) {
            // It was not a number, so ignore.
            ex.printStackTrace();
        }
            return null;
    }

}
