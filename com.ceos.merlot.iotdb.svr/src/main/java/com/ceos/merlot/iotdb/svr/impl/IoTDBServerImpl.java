/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.svr.impl;

import com.ceos.merlot.iotdb.svr.api.IoTDBServer;
import org.apache.iotdb.db.conf.IoTDBConfigCheck;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.iotdb.db.service.IoTDB;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class IoTDBServerImpl implements IoTDBServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(IoTDBServer.class);    
    private IoTDB daemon = null;
    private IoTDBDescriptor iotdbDescriptor = null;       
    private BundleContext bundleContext;
    
    public IoTDBServerImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    @Override
    public void init() {
        IoTDBConfigCheck.getInstance().checkConfig();
        daemon = IoTDB.getInstance();
    }

    @Override
    public void destroy() {

    }

    @Override
    public void start() {
        daemon.active();
    }

    @Override
    public void stop() {
        daemon.stop();
    }
    
}
