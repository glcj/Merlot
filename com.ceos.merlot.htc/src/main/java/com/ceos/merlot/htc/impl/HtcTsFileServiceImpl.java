/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

import com.ceos.merlot.htc.api.HtcConsumer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ceos.merlot.htc.api.HtcEvent;
import com.ceos.merlot.htc.api.HtcService;
import com.ceos.merlot.model.api.Model;
import java.time.Instant;
import org.epics.vtype.VNumber;

/**
 *
 * @author cgarcia
 */
public class HtcTsFileServiceImpl implements HtcService, HtcConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtcTsFileServiceImpl.class);
    private final Model s88model;
    //
    
    // Executor that will be used to construct new threads for consumers
    protected Executor RequestExecutor;

    //Disruptor 
    protected Disruptor<HtcEvent> RequestDisruptor; 
    
    //Ring Buffer    
    protected RingBuffer<HtcEvent> RequestRingBuffer; 
    
    // Specify the size of the ring buffer, must be power of 2.
    private int bufferSize = 1024;  
    private boolean started;
    private long starttime = 0;
    private long elapsedtime = 0;
    private long waittime = 60; //Default every 300 seconds
    private boolean push = false;    

    public HtcTsFileServiceImpl(Model s88model) {
        this.s88model = s88model;
    }
    
    @Override
    public void init() {
        RequestExecutor  = Executors.newCachedThreadPool();
        
        // Construct the Disruptor
        RequestDisruptor = new Disruptor<HtcEvent>(HtcEventImpl.FACTORY, 2048, DaemonThreadFactory.INSTANCE,
                    ProducerType.MULTI, new BlockingWaitStrategy());        

        RequestRingBuffer = RequestDisruptor.getRingBuffer();   
        
        RequestDisruptor.handleEventsWith(new HtcConsumerSoutImpl());          
        RequestDisruptor.start();    
        
        start();
    }

    @Override
    public void destroy() {  
        stop();
        RequestDisruptor.shutdown();
    }

    @Override
    public void start() {
        if (!started){           
        };
        started = true;
    }

    @Override
    public void stop() {           
        //RequestDisruptor.halt();
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
        
    
    @Override
    public HtcEvent getEvent() {
        if (!started) return null;
        long sequence = RequestRingBuffer.next();  // Grab the next sequence
    	HtcEvent de = RequestRingBuffer.get(sequence); // Get the entry in the Disruptor  
    	de.setSequence(sequence);
	return de;
    }

    @Override
    public void putEvent(HtcEvent event) {
        RequestRingBuffer.publish(event.getSequence());
    }
    
    @Override
    public void onEvent(Object event, long sequence, boolean endOfBatch) throws Exception {
        elapsedtime = (Instant.now().getEpochSecond() - starttime); 
        push = (elapsedtime > waittime);        
        HtcEvent myEvent = (HtcEvent) event;
        VNumber vnumber = (VNumber) myEvent.getPvReader().getValue();        
        System.out.println("HtcTsFileServiceImpl Event : " + event + " : " + myEvent.getTag() + " :... " + myEvent.getValue());
        myEvent.setTag(null);
        myEvent.setPvReader(null);
    }    
    
    @Override
    public void setMaxWaitTime(long seconds) {
        this.waittime = seconds;
    }

    @Override
    public long getMaxWaitTime() {
        return this.waittime;
    }    
    
    @Override
    public long getSamplesWaiting() {
        return 0L;
    };       
    
    @Override
    public long getRequestQueueSize() {
        return RequestRingBuffer.getBufferSize();
    }

    @Override
    public long getRequestQueueItems() {
        return (RequestRingBuffer.getBufferSize() - RequestRingBuffer.remainingCapacity());
    }    


    
    
    
}
