/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

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

/**
 *
 * @author cgarcia
 */
public class HtcServiceImpl implements HtcService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtcServiceImpl.class);   
    
    // Executor that will be used to construct new threads for consumers
    protected Executor RequestExecutor;

    //Disruptor 
    protected Disruptor<HtcEvent> RequestDisruptor; 
    
    //Ring Buffer    
    protected RingBuffer<HtcEvent> RequestRingBuffer; 
    
    // Specify the size of the ring buffer, must be power of 2.
    private int bufferSize = 1024;  
    private boolean started;
    
    
    @Override
    public void init() {
        System.out.println("Disruptor Inicio...");
        RequestExecutor  = Executors.newCachedThreadPool();
        
        // Construct the Disruptor
        RequestDisruptor = new Disruptor<HtcEvent>(HtcEventImpl.FACTORY, 1024, DaemonThreadFactory.INSTANCE,
                    ProducerType.MULTI, new BlockingWaitStrategy());        

        RequestRingBuffer = RequestDisruptor.getRingBuffer();    
        
        start();
    }

    @Override
    public void destroy() {
        System.out.println("Disruptor Destruyo...");        
        stop();
        RequestDisruptor.shutdown();
    }

    @Override
    public void start() {
        if (!started){
            System.out.println("Disruptor Arrancando el servicio...");            
            RequestDisruptor.handleEventsWith(new HtcConsumerSoutImpl());          
            RequestDisruptor.start();
        };
        started = true;
    }

    @Override
    public void stop() {
        System.out.println("Disruptor Parando el servicio...");        
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
    public long getRequestQueueSize() {
        return RequestRingBuffer.getBufferSize();
    }

    @Override
    public long getRequestQueueItems() {
        return (RequestRingBuffer.getBufferSize() - RequestRingBuffer.remainingCapacity());
    }    
    
    
    
}
