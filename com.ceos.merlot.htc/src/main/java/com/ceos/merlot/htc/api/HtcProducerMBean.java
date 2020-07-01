/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.api;

import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author cgarcia
 */
public interface HtcProducerMBean {

    public void start();

    public void stop();
    
    public void destroy();

    public boolean isStarted();    
    
    public void addTag(String tag, String hyst, String scan);
    
    public void delTag(String tag);
    
    public String getTags();
    
    public void setServiceRegistration(ServiceRegistration<HtcProducerMBean> registration); 
    
    public void setHtcProducer(HtcProducer producer);
    
    public String getConfigPid();
    
}
