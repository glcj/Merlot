/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.das.drv.s7.impl;

import com.ceos.merlot.model.core.PhysicalModelEnum;
import java.util.HashMap;
import java.util.Map;
import org.apache.plc4x.java.s7.events.S7ModeEvent;
import org.apache.plc4x.java.s7.readwrite.types.ModeTransitionType;
import org.apache.plc4x.java.s7.utils.S7EventHelper;
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
public class S7ModeEventProcessorImpl implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(S7ModeEventProcessorImpl.class);     
    public static final String TOPIC_EVENT_MODE   = "decanter/appender/s7/loki/mode"; 
    
    protected final BundleContext bundleContext;
    protected final EventAdmin eventAdmin;

    public S7ModeEventProcessorImpl(BundleContext bundleContext, EventAdmin eventAdmin) {
        this.bundleContext = bundleContext;
        this.eventAdmin = eventAdmin;
    }
    
    @Override
    public void handleEvent(Event event) {
        System.out.println("Procesando un Evento MODO."); 
        Map<String, Object> properties = new HashMap<>();        
        String strLOG = ModeEventProcessing(event);

        properties.put(PhysicalModelEnum.ENTERPRISE.name(),"PDVSA");
        properties.put(PhysicalModelEnum.SITE.name(),"REFINACION");
        properties.put(PhysicalModelEnum.AREA.name(),"FCC");
        properties.put(PhysicalModelEnum.UNIT.name(),"B51");
        properties.put(PhysicalModelEnum.CELL.name(),"CELL");
        properties.put(PhysicalModelEnum.EMODULE.name(),"EMODULE");
        properties.put(PhysicalModelEnum.CMODULE.name(),"CMODULE");        
        
        properties.put("LOG", strLOG);
        properties.put("TIMESTAMP", event.getProperty("TIMESTAMP"));
        
        Event msgEvent = new Event(TOPIC_EVENT_MODE, properties);
        eventAdmin.sendEvent(msgEvent);
        System.out.println("El modo: " + strLOG);
    }
    
    /**
     * 
     */
    public static String ModeEventProcessing(final Event event) { 
        StringBuilder sb = new StringBuilder("CPU is in : ");
        if (ModeTransitionType.isDefined((short) event.getProperty("CURRENT_MODE"))){
            short currentmode = (short) event.getProperty("CURRENT_MODE");
            sb.append(ModeTransitionType.enumForValue(currentmode).name());
        } else {
            sb.append("UNDEFINED");
        }
        return sb.toString();
    }     
    
}
