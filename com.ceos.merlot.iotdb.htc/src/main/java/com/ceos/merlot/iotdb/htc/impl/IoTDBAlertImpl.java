/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.htc.impl;

import com.ceos.merlot.iotdb.htc.api.IoTDBAlert;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 *
 * @author cgarcia
 */
public class IoTDBAlertImpl implements IoTDBAlert, EventHandler {

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void handleEvent(Event event) {
        for (String name : event.getPropertyNames()) {
            System.err.println("Alert: " + name + ":" + event.getProperty(name));
        }
        System.out.println("Alert Thread: " + Thread.currentThread().getName());        
    }
    
}
