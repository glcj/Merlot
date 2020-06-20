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

package com.ceos.merlot.das.drv.basic.impl;

import com.ceos.merlot.api.Device;
import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.core.Merlot;
import com.ceos.merlot.das.drv.basic.api.BasicDevice;
import com.ceos.merlot.das.drv.basic.api.BasicDriver;
import com.ceos.merlot.das.drv.basic.api.BasicOptimizer;
import com.ceos.merlot.das.drv.basic.core.BasicDriverEvent;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.time.LocalDateTime;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcRequest;
import org.apache.plc4x.java.api.messages.PlcResponse;
import org.apache.plc4x.java.spi.PlcDriver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasicDriverImpl implements BasicDriver {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicDriverImpl.class);
	
    static final String BASIC_DEVICE_CATEGORY = "basic";
    static final String BASIC_DRIVER_ID = "com.ceos.basic";	
        
    // Executor that will be used to construct new threads for consumers
    protected Executor RequestExecutor;
    protected Executor ResponseExecutor;
    
    //Disruptor 
    protected Disruptor<DriverEvent> RequestDisruptor;
    protected Disruptor<DriverEvent> ResponseDisruptor;    
    
    //Ring Buffer    
    protected RingBuffer<DriverEvent> RequestRingBuffer;
    protected RingBuffer<DriverEvent> ResponseRingBuffer;
    
    
    protected PlcDriver plcDriver = null;
    protected PlcConnection plcConn = null;
    protected PlcReadRequest.Builder plcBuilder = null;
    protected PlcRequest plcRequest = null;
    protected PlcResponse plcResponse = null;
    protected String url = null;
    protected Boolean started = false;    
    protected Boolean connected = false;
  
    
    //Some stats
    protected LocalDateTime stats_StartDateTime;
    protected LocalDateTime stats_LastFailDateTime;
    protected StopWatch stats_RunningTime = new StopWatch() ;
    protected StopWatch stats_StoppedTime = new StopWatch();
    protected long stats_SendMessages = 0L;
    protected long stats_FailedMessages = 0L;
    protected long stats_delay = 0L;
    protected long stats_max_delay = 0L;
    protected long stats_min_delay = 0L;
    
    // The factory for the event
    //MerlotBasicDeviceEventFactory factory = new MerlotBasicDeviceEventFactory();

    // Specify the size of the ring buffer, must be power of 2.
    int bufferSize = 1024;   
    protected long ressequence = 0;
    boolean returnvalue = false;
	
    protected BundleContext bc;
    
    protected BasicDevice bdev;
    
    protected BasicOptimizer optimizer = null;
    
    
    /**
     * 
     * @param bc 
     */
    public BasicDriverImpl(BundleContext bc){        
        this.bc = bc;
        this.plcDriver =  null; 
        this.stats_RunningTime.start(); this.stats_RunningTime.suspend();
        this.stats_StoppedTime.start(); this.stats_StoppedTime.suspend();
    }

    @Override
    public void init() throws Exception {
        
        
        
        RequestExecutor  = Executors.newCachedThreadPool();
        ResponseExecutor = Executors.newCachedThreadPool();

        // Construct the Disruptor
        RequestDisruptor = new Disruptor<DriverEvent>(BasicDriverEvent.FACTORY, 1024, DaemonThreadFactory.INSTANCE,
                    ProducerType.MULTI, new BlockingWaitStrategy());

        ResponseDisruptor = new Disruptor<DriverEvent>(BasicDriverEvent.FACTORY, 1024, DaemonThreadFactory.INSTANCE,
                    ProducerType.SINGLE, new BlockingWaitStrategy());       
        

        RequestRingBuffer = RequestDisruptor.getRingBuffer();
        ResponseRingBuffer = ResponseDisruptor.getRingBuffer();          
        
        
        RequestDisruptor.handleEventsWith(new EventHandler<DriverEvent>() {
            @Override
            public void onEvent(DriverEvent rxevent, long sequence, boolean endOfBatch) throws Exception {
                long start = System.currentTimeMillis();
                try {

                    ressequence = ResponseRingBuffer.next();  // Grab the next sequence
                    DriverEvent txevent = ResponseRingBuffer.get(ressequence); // Get the entry in the Disruptor

                    switch(rxevent.getFunctionCode()) {
                        case M_INITIATE_CNF:;
                            LOGGER.info("Executing : Merlot.M_INITIATE_CNF.");
                            break;
                        case M_READ_CNF:;
                            LOGGER.info("Executing : Merlot.M_READ_CNF.");
                            break;
                        case M_WRITE_CNF:;
                            LOGGER.info("Executing : Merlot.M_WRITE_CNF.");
                            break;
                        case M_MULTIPLE_READ_CNF:;
                            LOGGER.info("Executing : Merlot.M_MULTIPLE_READ_CNF.");
                            break;
                        case M_MULTIPLE_WRITE_CNF:;
                            LOGGER.info("Executing : Merlot.M_MULTIPLE_WRITE_CNF.");
                            break;
                        case M_CYCL_READ_INIT_CNF:;
                            LOGGER.info("Executing : Merlot.M_CYCL_READ_INIT_CNF.");
                            break;
                        case M_CYCL_READ_START_CNF:;
                            LOGGER.info("Executing : Merlot.M_CYCL_READ_START_CNF.");
                            break;
                        case M_CYCL_READ_STOP_CNF:;
                            LOGGER.info("Executing : Merlot.M_CYCL_READ_STOP_CNF.");
                            break;
                        case M_CYCL_READ_DELETE_CNF:;
                            LOGGER.info("Executing : Merlot.M_CYCL_READ_DELETE_CNF.");
                            break;
                        case M_PASSWORD_CNF:;
                            LOGGER.info("Executing : Merlot.M_PASSWORD_CNF.");
                            break;
                        case M_PASSWORD_LEN:;
                            LOGGER.info("Executing : Merlot.M_PASSWORD_LEN.");
                            break;
                        case M_RESET_PASSWORD:;
                            LOGGER.info("Executing : Merlot.M_RESET_PASSWORD.");
                            break;
                        default:  
                            LOGGER.info("No command function in the event.");
                            break;                          
                    }
                                                          
                    if (plcRequest != null){
                        plcResponse = plcRequest.execute().get();
                    }
                    
                    txevent.setSequence(ressequence);
                    txevent.setFunctionCode(rxevent.getFunctionCode());
                    txevent.setData(rxevent.getData()); //Pointer to data buffer
                    txevent.setCallback(rxevent.getCallback());
                    
                    rxevent.setCallback(null);
                    
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    ResponseRingBuffer.publish(ressequence);
                    long finish = System.currentTimeMillis();
                    stats_delay = finish - start;
                    if (stats_delay > stats_max_delay){stats_max_delay = stats_delay;};
                    if (stats_min_delay == 0L){stats_min_delay = stats_delay;};
                    if (stats_delay < stats_min_delay){stats_min_delay = stats_delay;};
                }
            }
        });
        
        ResponseDisruptor.handleEventsWith((event, sequence, endOfBatch) -> {
            try {        	
                if (event.getCallback() != null){
                    event.getCallback().execute(event);
                };
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        RequestDisruptor.start();
        ResponseDisruptor.start();
                    
        start();
    }

    @Override
    public void destroy() throws Exception {
        stop();
        RequestDisruptor.shutdown();
        ResponseDisruptor.shutdown();        
    }

    @Override
    public void start() {
        try {
            if(stats_RunningTime.isSuspended()) stats_RunningTime.resume();
            if(!stats_StoppedTime.isSuspended()) stats_StoppedTime.suspend();
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
    }

    @Override
    public void stop() {
        //TODO Evaluate drainAndHalt()
        if(!stats_RunningTime.isSuspended()) stats_RunningTime.suspend();
        if(stats_StoppedTime.isSuspended()) stats_StoppedTime.resume();        
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bc = bundleContext;
    }
    
    @Override
    public DriverEvent getEvent() {
        if ((!connected) ||  (!started)) return null;
        long sequence = RequestRingBuffer.next();  // Grab the next sequence
    	DriverEvent de = RequestRingBuffer.get(sequence); // Get the entry in the Disruptor  
    	de.setSequence(sequence);
	return de;
    }

    @Override
    public void putEvent(DriverEvent event) {
        RequestRingBuffer.publish(event.getSequence());
    }    

    @Override
    public int match(ServiceReference reference) throws Exception {
        LOGGER.info("match: " + reference.getProperty("DEVICE_CATEGORY"));
        if (BASIC_DRIVER_ID.matches((String) reference.getProperty("DEVICE_CATEGORY"))) {
            return 1;
        } else {
            return Device.MATCH_NONE;            
        }
    }

    @Override
    public String attach(ServiceReference reference) throws Exception {
        LOGGER.info("attach: " + reference.getProperty("DEVICE_CATEGORY"));
        BasicDevice device = (BasicDevice) bc.getService(reference);
        device.attach(this);
        return null;        
    }

    @Override
    public void setPlcDriver(PlcDriver plcDriver) {
        this.plcDriver = plcDriver;
    }

    @Override
    public PlcDriver getPlcDriver() {
        return plcDriver;
    }

    @Override
    public PlcConnection getPlcConnection() {
        return plcConn;
    }
            
    @Override
    public DriverEvent EventFactory(Merlot m) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public LocalDateTime getStartDateTime() {
        return this.stats_StartDateTime;
    }
    
    @Override
    public LocalDateTime getLastFailDateTime() {
        return this.stats_LastFailDateTime;
    }    

    @Override
    public long getRunningTime() {
        return this.stats_RunningTime.getTime();
    }

    @Override
    public long getStoppedTime() {
        return this.stats_StoppedTime.getTime();
    }

    @Override
    public long getSendMessages() {
        return this.stats_SendMessages;
    }

    @Override
    public long getFailedMessages() {
        return this.stats_FailedMessages;
    }

    @Override
    public long getDelay() {
        return stats_delay;
    }

    @Override
    public long getMaxDelay() {
        return stats_max_delay;
    }

    @Override
    public long getMinDelay() {
        return stats_min_delay;
    }

    @Override
    public long getRequestQueueSize() {
        return RequestRingBuffer.getBufferSize();
    }

    @Override
    public long getRequestQueueItems() {
        return (RequestRingBuffer.getBufferSize() - RequestRingBuffer.remainingCapacity());
    }

    @Override
    public long getResponseQueueSize() {
        return ResponseRingBuffer.getBufferSize();
    }

    @Override
    public long getResponseQueueItems() {
        return (ResponseRingBuffer.getBufferSize() - ResponseRingBuffer.remainingCapacity());
    }
 
    
    
    

}
