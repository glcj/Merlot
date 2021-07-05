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

package com.ceos.merlot.das.drv.s7.impl;

import com.ceos.merlot.das.drv.s7.api.S7EventProcessor;
import com.ceos.merlot.model.core.PhysicalModelEnum;
import java.util.HashMap;
import java.util.Map;
import org.apache.plc4x.java.s7.readwrite.types.ModeTransitionType;
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
public class S7EventProcessorImpl implements S7EventProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(S7EventProcessorImpl.class);
    public static final String TOPIC_EVENT_LOKI  = "decanter/appender/s7/loki/";     
    
    protected final BundleContext bundleContext;
    protected final EventAdmin eventAdmin;    
    
    
    public S7EventProcessorImpl(BundleContext bundleContext, EventAdmin eventAdmin) {
        this.bundleContext = bundleContext;
        this.eventAdmin = eventAdmin;
    }    
    
    
    @Override
    public void handleEvent(Event event) {
        System.out.println("Procesando un Evento MODO."); 
        Map<String, Object> properties = new HashMap<>();        
        String strLOG = EventProcessing(event);

        //TODO: Buscar informacion en el arbol S88
        properties.put("AS", event.getProperty("AS_NAME"));
        
        properties.put(PhysicalModelEnum.ENTERPRISE.name(),"PDVSA");
        properties.put(PhysicalModelEnum.SITE.name(),"REFINACION");
        properties.put(PhysicalModelEnum.AREA.name(),"FCC");
        properties.put(PhysicalModelEnum.UNIT.name(),"B51");
        properties.put(PhysicalModelEnum.CELL.name(),"CELL");
        properties.put(PhysicalModelEnum.EMODULE.name(),"EMODULE");
        properties.put(PhysicalModelEnum.CMODULE.name(),"CMODULE");        
        
        properties.put("LOG", strLOG);
        properties.put("TIMESTAMP", event.getProperty("TIMESTAMP"));
        
        Event msgEvent = new Event(TOPIC_EVENT_LOKI, properties);
        eventAdmin.sendEvent(msgEvent);
        System.out.println("El modo: " + strLOG);
        
    }

    @Override
    public String EventProcessing(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
