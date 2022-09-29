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

import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.core.Merlot.STATE;
import com.ceos.merlot.das.drv.basic.impl.BasicDriverImpl;
import com.ceos.merlot.das.drv.mb.api.MbDriver;
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
import java.util.logging.Level;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class MbDriverImpl extends BasicDriverImpl implements MbDriver, Job {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(MbDriverImpl.class);
   

    
    public MbDriverImpl(BundleContext bc) {
        super(bc);   
        this.bc = bc;
        this.plcDriver =  null;  
        this.stats_min_delay = 10000000L;
        this.state = STATE.STOPPED;
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
                long start = System.currentTimeMillis();
                PlcReadResponse plcReadResponse = null;
                PlcWriteResponse  plcWriteResponse = null;
                
                try {
                    ressequence = ResponseRingBuffer.next();  // Grab the next sequence
                    DriverEvent txevent = ResponseRingBuffer.get(ressequence); // Get the entry in the Disruptor
                    
                    if (connected && (state == STATE.EXECUTE)) {
                        switch(rxevent.getFunctionCode()) {
                            case FC_WRITE_DATA_BYTES:                            
                                plcWriteResponse = rxevent.getPlcWriteRequest().execute().get();
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
                    }
                    
                    txevent.setSequence(ressequence);
                    txevent.setFunctionCode(rxevent.getFunctionCode());
                    txevent.setPlcReadResponse(plcReadResponse);
                    txevent.setPlcWriteResponse(plcWriteResponse);
                    txevent.setCallback(rxevent.getCallback());
                    
                    rxevent.setCallback(null);
                    stats_SendMessages++;
                    
                } catch (Exception ex) {
                    // TODO Auto-generated catch block
                    stats_FailedMessages++;
                    ex.printStackTrace();
                    LOGGER.info("RequestDisruptor: " + ex.toString());
                } finally {
                    connected = plcConn.isConnected();
                    if (!connected){
                        if (!stats_RunningTime.isSuspended()) stats_RunningTime.suspend();
                        if (stats_StoppedTime.isSuspended()) stats_StoppedTime.resume();
                        state = STATE.HOLDING;
                    }
                    
                    
                    ResponseRingBuffer.publish(ressequence);
                    long finish = System.currentTimeMillis();
                    stats_delay = finish - start;
                    if (stats_delay > stats_max_delay){stats_max_delay = stats_delay;};
                    if (stats_delay < stats_min_delay){stats_min_delay = stats_delay;};                    
                }
            }
        });
        
        ResponseDisruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            try {        	
                if (event.getCallback() != null){
                    event.getCallback().execute(event);
                } else {
                    LOGGER.info("ResponseDisruptor: No callback assigned. ");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });    
        
        RequestDisruptor.start();
        ResponseDisruptor.start();
                         
    }    
    
    @Override
    public void destroy() throws Exception {
        super.destroy();
    }

    @Override
    public void onStopped() {
        LOGGER.info("Driver is STOPPED state.");
    }
    
    @Override
    public void onResetting() {
        LOGGER.info("Driver is RESETTING state.");            
        if (plcDriver != null){
            LOGGER.info("PlcDriver name: {}",plcDriver.getProtocolName());
            LOGGER.info("PlcDriver code: {}",plcDriver.getProtocolCode());
            try {
                plcConn = plcDriver.getConnection(url);
                if (plcConn != null){
                    state = STATE.IDLE;
                }
            } catch (Exception ex){
                LOGGER.info("Start: " + ex.toString());
                state = STATE.STOPPED;
            }
        } else {
            LOGGER.info("Native driver == null...");  
            state = STATE.STOPPED;
        };                           
    }    
    
    @Override
    public void onIdle() {
        LOGGER.info("Driver is IDLE state.");          
        super.onIdle();
    }
    
    @Override
    public void onStarting() {
        LOGGER.info("Driver is STARTING state.");          
        if (plcConn != null){
            try {
                plcConn.connect();
                connected = plcConn.isConnected(); 
                if (connected){
                    LOGGER.info("Native driver connected to ({}).",url); 
                    state = STATE.EXECUTE;
                } else {
                    LOGGER.info("Native driver don't connect to ({}).",url);
                    connected = false;                    
                    state = STATE.STOPPED;
                }                
            }  catch (Exception ex){
               LOGGER.info(ex.getLocalizedMessage());                
               state = STATE.STOPPED;               
            }

        } else {
            LOGGER.info("ERROR: plcConn is null...");
            connected = false;
            state = STATE.STOPPED;
        }
    }
    
    
    @Override
    public void onExecute() {
        LOGGER.info("Driver is EXECUTE state.");          
    }
    
    
    @Override
    public void onCompleting() {
        LOGGER.info("Driver is COMPLETING state.");        
        super.onCompleting();
    }
    
    
    @Override
    public void onComplete() {
        LOGGER.info("Driver is COMPLETE state.");        
        super.onComplete();
    }
    
    @Override
    public void onHolding() {
        LOGGER.info("Driver is HOLDING state.");
        state = STATE.HELD;
    }
    
    @Override
    public void onHeld() {
        LOGGER.info("Driver is HELD state.");        
    }

    @Override
    public void onUnholding() {
        LOGGER.info("Driver is UNHOLDING state.");        
        try {
            plcConn.connect();
            connected = plcConn.isConnected(); 
            if (connected){
                LOGGER.info("Native driver connected to ({}).",url); 
                state = STATE.EXECUTE;
            } else {
                LOGGER.info("Native driver don't connect to ({}).",url);
                connected = false;                    
                state = STATE.HELD;
            }                
        }  catch (Exception ex){
           LOGGER.info(ex.getLocalizedMessage());                
           state = STATE.STOPPED;               
        }
    }

    @Override
    public void onSuspending() {
        LOGGER.info("Driver is SUSPENDING state.");        
        try {
            connected = false;
            plcConn.close();
        } catch (Exception ex) {
            LOGGER.info(ex.getLocalizedMessage());
        } finally {
            state = STATE.SUSPENDED;
        }
    } 

    @Override
    public void onSuspended() {
        LOGGER.info("Driver is SUSPENDED state.");        
        super.onSuspended();
    }
    
    @Override
    public void onUnsuspending() {
        LOGGER.info("Driver is UNSUSPENDINGstate.");        
        try {
            plcConn.connect();
            connected = plcConn.isConnected(); 
            if (connected){
                LOGGER.info("Native driver connected to ({}).",url); 
                state = STATE.EXECUTE;
            } else {
                LOGGER.info("Native driver don't connect to ({}).",url);
                connected = false;                    
                state = STATE.SUSPENDED;
            }                
        }  catch (Exception ex){
           LOGGER.info(ex.getLocalizedMessage());                
           state = STATE.STOPPED;               
        }
    }    
    
    @Override
    public void onAborting() {
        LOGGER.info("Driver is ABORTING state.");        
        try {
            connected = false;
            plcConn.close();
        } catch (Exception ex) {
            LOGGER.info(ex.getLocalizedMessage());
        } finally {
            state = STATE.ABORTED;
        }
    }   
    
    @Override
    public void onAborted() {
        LOGGER.info("Driver is ABORTED state.");        
    }
    
    @Override
    public void onClearing() {
        LOGGER.info("Driver is CLEARING state.");           
        state = STATE.STOPPED;
    }
   
    @Override
    public void onStopping() {
        try {
            connected = false;
            plcConn.close();
        } catch (Exception ex) {
            LOGGER.info(ex.getLocalizedMessage());
        } finally {
            state = STATE.STOPPED;
        }         
    }


    @Override
    public void execute(JobContext context) {
        
        switch (state){
            case STOPPED : onStopped();
                break;
            case RESETTING : onResetting();
                break;  
            case IDLE : onIdle();
                break;                  
            case STARTING : onStarting();
                break;      
            case EXECUTE : onExecute();
                break;  
            case COMPLETING : onCompleting();
                break;
            case COMPLETE : onComplete();
                break;   
            case HOLDING : onHolding();
                break;    
            case HELD : onHeld();
                break;  
            case UNHOLDING : onUnholding();
                break;  
            case SUSPENDING : onSuspending();
                break;        
            case SUSPENDED : onSuspended();
                break;  
            case UNSUSPENDING : onUnsuspending();
                break;     
            case ABORTING: onAborting();
                break;   
            case ABORTED: onAborted();
                break;  
            case CLEARING: onClearing();
                break;   
            case STOPPING: onStopping();
                break;                  
        }
        
        if (started && !connected ) {
            try{
                plcConn.connect();
                connected = plcConn.isConnected();
                if (!connected){
                    LOGGER.info("Native driver don't connect to ({}).",url);                     
                }          
            }
            catch (Exception ex) {
                LOGGER.info("Reconnect: " + ex.toString());
            }
        };
        
        try{
            connected = plcConn.isConnected();
            if (connected){
                if (stats_RunningTime.isSuspended()) stats_RunningTime.resume();
                if (!stats_StoppedTime.isSuspended()) stats_StoppedTime.suspend();                
            } else {
                if (!stats_RunningTime.isSuspended()) stats_RunningTime.suspend();
                if (stats_StoppedTime.isSuspended()) stats_StoppedTime.resume();                  
            }
        } catch  (Exception ex) {
            LOGGER.info("Connected?: " + ex.toString());
        }
    }
    
}
