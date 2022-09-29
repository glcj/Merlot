/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.api;

/**
 *
 * @author cgarcia
 */
public interface HtcService {
    
    public void init();
    
    public void destroy();
    
    public void start();
    
    public void stop();   
    
    public boolean isStarted();
    
    public HtcEvent getEvent();
    
    public void putEvent(HtcEvent event);
    
    public void setMaxWaitTime(long seconds);
    
    public long getMaxWaitTime();
    
    public long getSamplesWaiting();    
    
    public long getRequestQueueSize();
    
    public long getRequestQueueItems();            
    
}
