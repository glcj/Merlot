/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.htc.impl;

import com.ceos.merlot.iotdb.htc.api.IoTDBCollector;
import com.ceos.merlot.scheduler.api.Job;
import com.ceos.merlot.scheduler.api.JobContext;
import java.time.LocalTime;
import java.util.HashMap;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author cgarcia
 */
public class IoTDBCollectorImpl implements IoTDBCollector, Job  {

    private EventAdmin dispatcher;    
    
    public IoTDBCollectorImpl(EventAdmin dispatcher) {
        this.dispatcher = dispatcher;
    }        
        
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
    public void execute(JobContext context) {
      HashMap<String, Object> data = new HashMap<>();
      data.put("type", "bundle_1");
      data.put("change", LocalTime.now().toString());
      data.put("symbolicName", this.getClass().getName());
      Event event = new Event("decanter/collect/ceos", data);
      dispatcher.postEvent(event); 
      data.put("type", "bundle_2");
      data.put("change", LocalTime.now().toString());
      Event event2 = new Event("decanter/alert/ceos", data);      
      dispatcher.postEvent(event2);        
    }
    
}
