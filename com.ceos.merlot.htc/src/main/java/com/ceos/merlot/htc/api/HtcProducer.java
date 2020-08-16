/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author cgarcia
 */
public interface HtcProducer extends BundleActivator{
    
    public void init();
    
    public void destroy();
    
    public void start();
    
    public void stop();   
    
    public boolean isStarted();    
    
    public void addTag(String tag, String ds, String hyst, String scan);
    
    public void delTag(String tag);
    
    public void setServiceRegistration(ServiceRegistration<HtcProducer> registration); 

    public ServiceRegistration<HtcProducer> getServiceRegistration();     
    
    public void setConfigPid(String confipid);
    
    public String getConfigPid();
    
}
