/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.das.drv.s7.impl;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class S7SysEventProcessorImpl implements EventHandler{

    private static final Logger LOGGER = LoggerFactory.getLogger(S7SysEventProcessorImpl.class);     
    
    protected final BundleContext bundleContext;
    protected final EventAdmin eventAdmin;    
    
    public S7SysEventProcessorImpl (BundleContext bundleContext, EventAdmin eventAdmin) {
        this.bundleContext = bundleContext;
        this.eventAdmin = eventAdmin;
    }    
    
    @Override
    public void handleEvent(Event event) {
        System.out.println("Procesando un Evento SYS."); 
    }
    
}
