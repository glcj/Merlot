/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

import com.ceos.merlot.htc.api.HtcProducer;
import com.ceos.merlot.htc.api.HtcProducerMBean;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 *
 * @author cgarcia
 */
public class HtcProducerMBeanImpl extends StandardMBean implements HtcProducerMBean {
    private final BundleContext bundleContext;
    private HtcProducer producer; 
    private ServiceRegistration<HtcProducerMBean> registration = null;    
    
    public HtcProducerMBeanImpl(BundleContext bundleContext,HtcProducer producer) throws NotCompliantMBeanException {
        super(HtcProducerMBean.class);
        this.producer = producer;
        this.bundleContext = bundleContext;
    }    

    @Override
    public void start() {
        producer.start();
    }

    @Override
    public void stop() {
        producer.stop();
    }

    @Override
    public void destroy() {
        try {
            producer.stop(bundleContext);
            registration.unregister();
        } catch (Exception ex) {
            
        }
    }
    
    @Override
    public boolean isStarted() {
        return producer.isStarted();
    }

    @Override
    public void addTag(String tag, String hyst, String scan) {
        producer.addTag(tag, hyst, scan);
    }

    @Override
    public void delTag(String tag) {
        producer.delTag(tag);
    }
        
    @Override
    public String getTags() {
        return "Json";
    }
    
    public void setServiceRegistration(ServiceRegistration<HtcProducerMBean> registration){
        this.registration = registration;
    }

    public void setHtcProducer(HtcProducer producer){
        this.producer = producer;
    }
    
    @Override
    public String getConfigPid() {
        return producer.getConfigPid();
    }
    
    
    
}
