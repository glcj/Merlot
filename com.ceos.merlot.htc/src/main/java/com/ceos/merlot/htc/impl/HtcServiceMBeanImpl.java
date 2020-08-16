/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

import com.ceos.merlot.htc.api.HtcService;
import com.ceos.merlot.htc.api.HtcServiceMBean;

/**
 *
 * @author cgarcia
 */
public class HtcServiceMBeanImpl implements HtcServiceMBean {
    
    private HtcService service;
    
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void start() {
        service.start();
    }

    @Override
    public void stop() {
        service.stop();
    }

    @Override
    public boolean isStarted() {
        return service.isStarted();
    }

    @Override
    public void setMaxWaitTime(long seconds) {
        service.setMaxWaitTime(seconds);
    }

    @Override
    public long getMaxWaitTime() {
        return service.getMaxWaitTime();
    }
  
    @Override
    public long getSamplesWaiting() {
        return service.getSamplesWaiting();
    };       
    
    @Override
    public long getRequestQueueSize() {
        return service.getRequestQueueSize();
    }

    @Override
    public long getRequestQueueItems() {
        return service.getRequestQueueItems();
    }

    @Override
    public void setHtcService(HtcService service) {
        this.service = service;
    }
    
}
