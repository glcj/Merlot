/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

import com.ceos.merlot.htc.api.HtcEvent;
import com.ceos.merlot.htc.api.HtcProducer;
import com.ceos.merlot.htc.api.HtcService;
import com.ceos.merlot.scheduler.api.Job;
import com.ceos.merlot.scheduler.api.JobContext;
import java.time.Duration;
import java.util.HashMap;
import java.util.Hashtable;
import org.epics.gpclient.GPClient;
import org.epics.gpclient.PVEvent;
import org.epics.gpclient.PVReader;
import org.epics.gpclient.PVReaderConfiguration;
import org.epics.gpclient.PVReaderListener;
import org.epics.vtype.VType;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class HtcProducerImpl implements HtcProducer, PVReaderListener<VType>{

    private static final Logger LOGGER = LoggerFactory.getLogger(HtcProducerImpl.class);     
    private ServiceReference<HtcProducer> reference;
    private ServiceRegistration<HtcProducer> registration;
    private final HtcService htc;
    private final HashMap<String, Item> items;
    private final HashMap<PVReader<VType>, Item> pvritems;  
    private final String group;
    private String configpid;
    
    
    public HtcProducerImpl(HtcService htc, String group) {
        this.htc = htc;
        this.items = new HashMap();
        this.pvritems  = new HashMap();
        this.group = group;
    }


    private boolean started= false;
    
    @Override
    public void init() {
        System.out.println("Producer Init...");
    }

    @Override
    public void destroy() {
        System.out.println("Producer Destroy...");        
        items.forEach((key,item) ->{
            item.pvr.close();
        });  
    }

    @Override
    public void start() {
        System.out.println("Producer Start...");        
        pvritems.clear();        
        items.forEach((key,item) ->{
            if (item.pvr == null){
                item.pvr = GPClient.read("pva://"+key)
                        .addReadListener(this)
                        .maxRate(Duration.ofMillis(item.scan_rate))
                        .start();
            } else if (item.pvr.isPaused()){
                item.pvr.setPaused(false);                
            };
            pvritems.put(item.pvr, item);
        });
        started = true;
    }

    @Override
    public void stop() {
        System.out.println("Producer Stop...");        
        items.forEach((key,item) ->{
            item.pvr.setPaused(true);
        });     
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }


    @Override
    public void pvChanged(PVEvent pve, PVReader<VType> reader) {
        if (!started) return;
        if (pve.isType(PVEvent.Type.VALUE)) {
            HtcEvent htcevent = htc.getEvent();
            if (htcevent != null){
                htcevent.setTag(pvritems.get(reader).tag);
                htcevent.setPvReader(reader);
                htc.putEvent(htcevent);
            }
        } else {
            LOGGER.info(pve.toString());
        }
    }

    @Override
    public void addTag(String tag, String hyst, String scan) {
        Item item = new Item();
        try {
            item.tag = tag;
            item.hyst = Double.parseDouble(hyst);
            item.scan_rate = Integer.parseInt(scan);
            //100 ms minimum sampling time.
            if (item.scan_rate < 100){ item.scan_rate = 100; };
            
            items.put(tag, item);
            
        } catch (Exception ex){
            
        }
    }

    @Override
    public void delTag(String tag) {
        items.remove(tag);
    }

    
    
    
    @Override
    public void start(BundleContext context) throws Exception {
        Hashtable props = new Hashtable<String, String>();
        props.put("htc.group", group);
        registration = context.registerService(HtcProducer.class,
                this,
                props);
        reference = registration.getReference();
        start();    
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        stop();
        destroy();
        registration.unregister();
    }

    @Override
    public void setConfigPid(String configpid) {
        this.configpid = configpid;
    }

    @Override
    public String getConfigPid() {
        return configpid;
    }
   
    private class Item {
        public String tag;
        public PVReader<VType> pvr;
        public Double hyst;
        public int scan_rate;
    }
    
}
