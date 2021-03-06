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

import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.das.drv.basic.impl.BasicDriverImpl;
import com.ceos.merlot.das.drv.s7.api.S7Driver;
import com.ceos.merlot.impl.DriverEventImpl;
import com.ceos.merlot.scheduler.api.Job;
import com.ceos.merlot.scheduler.api.JobContext;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class S7DriverImpl extends BasicDriverImpl implements S7Driver, Job{
    
    private static final Logger LOGGER = LoggerFactory.getLogger(S7DriverImpl.class);
   
    private Boolean started = false;    
    private Boolean connected = false;
  
    
    public S7DriverImpl(BundleContext bc) {
        super(bc);   
        this.bc = bc;
        this.plcDriver =  null;                        
    }
    
    @Override
    public void init() throws Exception {

        RequestExecutor  = Executors.newCachedThreadPool();
        ResponseExecutor = Executors.newCachedThreadPool();

        // Construct the Disruptor
        RequestDisruptor = new Disruptor<DriverEvent>(DriverEventImpl.FACTORY, 1024, DaemonThreadFactory.INSTANCE,
                    ProducerType.MULTI, new BlockingWaitStrategy());

        ResponseDisruptor = new Disruptor<DriverEvent>(DriverEventImpl.FACTORY, 1024, DaemonThreadFactory.INSTANCE,
                    ProducerType.SINGLE, new BlockingWaitStrategy());       
        
        RequestRingBuffer = RequestDisruptor.getRingBuffer();
        ResponseRingBuffer = ResponseDisruptor.getRingBuffer();         
        
        
        RequestDisruptor.handleEventsWith(new EventHandler<DriverEvent>() {
            @Override
            public void onEvent(DriverEvent rxevent, long sequence, boolean endOfBatch) throws Exception {
                PlcReadResponse plcReadResponse = null;
                PlcWriteResponse  plcWriteResponse = null;
                try {

                    ressequence = ResponseRingBuffer.next();  // Grab the next sequence
                    DriverEvent txevent = ResponseRingBuffer.get(ressequence); // Get the entry in the Disruptor

                    switch(rxevent.getFunctionCode()) {
                        case FC_WRITE_DATA_BYTES:
                            plcWriteResponse = rxevent.getPlcWriteRequest().execute().get(1, TimeUnit.SECONDS);
                            break;
                        case FC_READ_MEMORY_BYTES:
                            plcReadResponse = rxevent.getPlcReadRequest().execute().get(2, TimeUnit.SECONDS);                            
                            break;
                        case M_INITIATE_CNF:;
                            break;
                        case M_READ_CNF:;
                            break;
                        case M_WRITE_CNF:;
                            break;
                        case M_MULTIPLE_READ_CNF:;
                            break;
                        case M_MULTIPLE_WRITE_CNF:;
                            break;
                        case M_CYCL_READ_INIT_CNF:;
                            break;
                        case M_CYCL_READ_START_CNF:;
                            break;
                        case M_CYCL_READ_STOP_CNF:;
                            break;
                        case M_CYCL_READ_DELETE_CNF:;
                            break;
                        case M_PASSWORD_CNF:;
                            break;
                        case M_PASSWORD_LEN:;
                            break;
                        case M_RESET_PASSWORD:;
                            break;
                        default:  
                            LOGGER.info("No command function in the event.");
                            break;                          
                    }

                    txevent.setSequence(ressequence);
                    txevent.setFunctionCode(rxevent.getFunctionCode());
                    txevent.setPlcReadResponse(plcReadResponse);
                    txevent.setPlcWriteResponse(plcWriteResponse);
                    txevent.setCallback(rxevent.getCallback());
                    
                    rxevent.setCallback(null);
                    
                } catch (Exception ex) {
                    // TODO Auto-generated catch block
                    LOGGER.info(ex.toString());
                } finally {
                    ResponseRingBuffer.publish(ressequence);
                }
            }
        });
        
        ResponseDisruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            try {        	
                if (event.getCallback() != null){
                    event.getCallback().execute(event);
                } else {
                    LOGGER.info("No callback assigned. Fields: {}.", event.getPlcReadResponse().getFieldNames());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });        
                         
    }    
    
    @Override
    public void destroy() throws Exception {
        super.destroy();
    }

    @Override
    public void start() {
        super.start(); 
        if (!connected ) {
            try{
                if (plcDriver != null){
                    LOGGER.info("PlcDriver name: {}",plcDriver.getProtocolName());
                    LOGGER.info("PlcDriver code: {}",plcDriver.getProtocolCode());
                    plcConn = plcDriver.getConnection(url);
                    plcConn.connect();
                } else {
                    LOGGER.info("Driver nativo == null...");                    
                };
                if (plcConn != null){
                    connected = plcConn.isConnected(); 
                    if (connected){
                        LOGGER.info("Native driver connected to ({}).",url);    
                    } else {
                        LOGGER.info("Native driver don't connect to ({}).",url);
                    }
                } else {
                    LOGGER.info("ERROR: plcConn is null...");
                    connected = false;
                }
                started = true;

            } catch (Exception ex) {
                LOGGER.info(ex.toString());
            }
        };                
    }    
    
    @Override
    public void stop() {
        super.stop();
        if (connected) {
            try{
                plcConn.close();
                connected = plcConn.isConnected();             
                started = false;
                if (!connected){
                    LOGGER.info("Disconnected from ({}).",url);
                }                
            } catch (Exception ex) {
                LOGGER.info(ex.toString());
            }
        };                 
    }

    @Override
    public void execute(JobContext context) {
        if (started && !connected ) {
            try{
                plcConn = plcDriver.getConnection(url);
                connected = plcConn.isConnected();
                if (!connected){
                    LOGGER.info("Native driver don't connect to ({}).",url);
                }                
            }
            catch (Exception ex) {
                LOGGER.info(ex.toString());
            }
        };
    }
    
    
}
