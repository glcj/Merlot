/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.das.drv.s7.impl;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 *
 * @author cgarcia
 */
public class S7EventProcessorImpl implements EventHandler{

    @Override
    public void handleEvent(Event event) {
        System.out.println("Tipo recibido: " + event.getProperty("TYPE"));
    }
    
}
