/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.decanter.api;

/**
 *
 * @author cgarcia
 */
public interface IoTDBAlert {
    
    public void init();
    
    public void destroy();
    
    public void start();
    
    public void stop();      
}
