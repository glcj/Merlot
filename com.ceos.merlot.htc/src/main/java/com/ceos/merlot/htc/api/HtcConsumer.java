/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.api;

import com.lmax.disruptor.EventHandler;

/**
 *
 * @author cgarcia
 */
public interface HtcConsumer extends EventHandler{
    
    public void init();
    
    public void destroy();
    
    public void start();
    
    public void stop();      
    
}
