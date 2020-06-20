/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.htc.core;

import com.ceos.merlot.scheduler.api.Job;
import com.ceos.merlot.scheduler.api.JobContext;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author cgarcia
 */
public class IoTDBCollector implements Job {


    private EventAdmin dispatcher;    
    

    public IoTDBCollector(EventAdmin dispatcher) {
        this.dispatcher = dispatcher;
    }    

    @Override
    public void execute(JobContext context) {
        
    }
    

    
}
