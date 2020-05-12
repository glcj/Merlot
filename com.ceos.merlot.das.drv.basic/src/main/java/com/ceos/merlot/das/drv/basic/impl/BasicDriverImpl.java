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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
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
            RequestDisruptor.start();
            ResponseDisruptor.start();
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
    }

    @Override
    public void stop() {
        //TODO Evaluate drainAndHalt()
        RequestDisruptor.halt();
        ResponseDisruptor.halt();
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bc = bundleContext;
    }
    
    @Override
    public DriverEvent getEvent() {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }
 
    

}
