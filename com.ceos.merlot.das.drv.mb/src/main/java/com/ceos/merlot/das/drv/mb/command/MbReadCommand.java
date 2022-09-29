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

package com.ceos.merlot.das.drv.mb.command;

import com.ceos.merlot.api.DriverCallback;
import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.das.drv.mb.api.MbDevice;
import com.ceos.merlot.das.drv.mb.impl.MbDeviceImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.apache.karaf.shell.api.console.SessionFactory;
import org.apache.karaf.shell.support.table.ShellTable;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.model.PlcField;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.device.Device;
import org.apache.plc4x.java.modbus.base.field.ModbusFieldCoil;
import org.apache.plc4x.java.modbus.base.field.ModbusField;
import org.apache.plc4x.java.modbus.base.field.ModbusFieldHoldingRegister;
import org.apache.plc4x.java.modbus.base.field.ModbusFieldInputRegister;
import org.apache.plc4x.java.modbus.base.field.ModbusExtendedRegister;

/**
 *
 * @author cgarcia
 */
@Command(scope = "mb", name = "read", description = "Read a list of fields registers from Modbus device.")
@Service
public class MbReadCommand implements Action, DriverCallback  {
    
    @Reference
    BundleContext bc;        
    
    @Reference
    Session session;

    @Reference
    SessionFactory sessionFactory;
    
    @Option(name = "-t", aliases = "--tabular", description = "Show the data like tabular data..", required = false, multiValued = false)
    boolean tabular = false;    

    @Argument(index = 0, name = "serial", description = "Device serial.", required = true, multiValued = false)
    String serial;  
    
    @Argument(index = 1, name = "ref", description = "User reference value.", required = true, multiValued = false)
    String userRef;    

    @Argument(index = 2, name = "field", description = "Field to read.", required = true, multiValued = false)
    String userField;        
    
    
    @Override
    public Object execute() throws Exception {
        String filter =  "(&(|(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + Device.class.getName() + ")" +
            "(" + org.osgi.framework.Constants.OBJECTCLASS + "=" + com.ceos.merlot.api.Device.class.getName() + "))" +
            "(&(" + org.osgi.service.device.Constants.DEVICE_CATEGORY + "=" + MbDeviceImpl.MB_DEVICE_CATEGORY + ")" +
            "(" + org.osgi.service.device.Constants.DEVICE_SERIAL + "=" + serial + ")))";  

        ServiceReference[] refs = bc.getServiceReferences((String) null, filter);
        if (refs == null){
            System.out.println("Device not found. SERIAL: " + serial);
        } else {
            ServiceReference ref = refs[0];
            MbDevice mbdevice = (MbDevice) bc.getService(ref);
            mbdevice.ReadRequest(userRef, userField, this);
            
        }        
        return null;
        
    }

    @Override
    public void execute(DriverEvent cb) {
        ByteBuf byteBuf = null;
        try {
            PlcReadResponse response = cb.getPlcReadResponse();
            PlcReadRequest request = response.getRequest();
            
            Object obj =  response.getObject(userRef);
            System.out.println("\r\n");
            if (obj instanceof ArrayList){
                ArrayList<Object> objArray = (ArrayList) obj;
                if (objArray.get(0) instanceof Boolean){
                    if (tabular) {
                        printCoilsInColumn(request, objArray);
                    } else {                    
                        System.out.println("Boolean values: " + objArray);
                    }
                } else if (objArray.get(0) instanceof Byte[]){
                    byte[] backend = new byte[objArray.size() * 2];
                    int i = 0;
                    for (Object objValue:objArray) {
                        Byte[] myBytes = (Byte[]) objValue;
                        backend[i] = myBytes[0];
                        backend[i+1] = myBytes[1];
                        i+=2;
                    }
                    if (tabular) {
                        printRegisterInColumn(request, backend);
                    } else {
                        byteBuf = Unpooled.wrappedBuffer(backend);

                        System.out.println("Array Byte Hex dump:");
                        System.out.println(ByteBufUtil.prettyHexDump(byteBuf));
                        System.out.println("");                         
                    }
                    

                } else {
                    System.out.println("Clase: " + objArray.get(0).getClass());
                }
            } else if (obj instanceof Boolean) {
                System.out.println("Boolean value: " + obj);
            } else if (obj instanceof Byte[]) {
                Byte[] myBytes = (Byte[]) obj;
                byte[] backend = ArrayUtils.toPrimitive(myBytes);
                if (tabular) printRegisterInColumn(request, backend);
                byteBuf = Unpooled.wrappedBuffer(backend);
                System.out.println("Byte Hex dump:");
                System.out.println(ByteBufUtil.prettyHexDump(byteBuf));
                System.out.println("");                
            }
                            
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (byteBuf != null)byteBuf.release();
        }
    }
    
    private void printCoilsInColumn(PlcReadRequest request, ArrayList<Object> objArray) {
        List<PlcField> fields = request.getFields();
        ModbusField mbField = (ModbusField) fields.get(0);
        int ini = mbField.getAddress();
        int end = 0;        

        if (mbField instanceof ModbusFieldCoil) ini+= 10000;
        end = ini + mbField.getNumberOfElements();
        ShellTable table = new ShellTable();
        table.column("Register");
        table.column("Boolean value");   
        
        String booleancolumn = null;
        int item = 0;
        for (int i=ini; i < end; i++){
            booleancolumn= objArray.get(item).toString();
            table.addRow().addContent(i,booleancolumn);
            item++;
        }
        System.out.println();
        table.print(System.out);
        System.out.println();         
    }    
    
    private void printRegisterInColumn(PlcReadRequest request, byte[] backend) {
        List<PlcField> fields = request.getFields();
        ModbusField mbField = (ModbusField) fields.get(0);
        int ini = mbField.getAddress();
        int end = 0;        
        ByteBuf byteBuf = Unpooled.wrappedBuffer(backend);
        if (mbField instanceof ModbusExtendedRegister) ini+= 40000;
        if (mbField instanceof ModbusFieldInputRegister) ini+=30000;        
        if (mbField instanceof ModbusFieldHoldingRegister) ini+=40000;
        end = ini + mbField.getNumberOfElements();
        
        ShellTable table = new ShellTable();
        table.column("Register");
        table.column("Hex");

        String hexcolumn = null;
        for (int i=ini; i < end; i++){
            hexcolumn = Integer.toHexString(byteBuf.readShort() & 0xffff);
            table.addRow().addContent(i,hexcolumn);
        }
        System.out.println();
        table.print(System.out);
        System.out.println();  
        byteBuf.release();
        
    }
    
}
