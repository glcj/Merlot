/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.modbus.svr.impl;

import com.ceos.merlot.modbus.svr.api.ModbusServer;
import com.ceos.merlot.modbus.svr.api.ModbusServerMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

/**
 *
 * @author cgarcia
 */
public class ModbusServerMBeanImpl extends StandardMBean implements ModbusServerMBean{

    private final ModbusServer mserver; 
    
    public ModbusServerMBeanImpl(ModbusServer mserver) throws NotCompliantMBeanException {
        super(ModbusServerMBean.class);
        this.mserver = mserver;
    }    
    
    @Override
    public void init() throws Exception {
        mserver.init();
    }

    @Override
    public void destroy() throws Exception {
        mserver.destroy();
    }

    @Override
    public void start() {
        mserver.start();
    }

    @Override
    public void stop() {
        mserver.stop();
    }

    @Override
    public void setPort(int port) {
        mserver.setPort(port);
    }

    @Override
    public int getPort() {
        return mserver.getPort();
    }

    @Override
    public void setHost(String host) {
        mserver.setHost(host);
    }

    @Override
    public String getHost() {
        return mserver.getHost();
    }

    @Override
    public boolean isStarted() {
        return mserver.isStarted();
    }

    @Override
    public long getCheckInterval() {
        return mserver.getCheckInterval();
    }

    @Override
    public void setCheckInterval(long newCheckInterval) {
        mserver.setCheckInterval(newCheckInterval);
    }

    @Override
    public long getLastReadThroughput() {
        return mserver.getLastReadThroughput();
    }

    @Override
    public long getLastWriteThroughput() {
        return mserver.getLastWriteThroughput();
    }

    @Override
    public long getCumulativeReadBytes() {
        return mserver.getCumulativeReadBytes();
    }

    @Override
    public long getCumulativeWrittenBytes() {
        return mserver.getCumulativeWrittenBytes();
    }

    @Override
    public long getCurrentReadBytes() {
        return mserver.getCurrentReadBytes();
    }

    @Override
    public long getCurrentWrittenBytes() {
        return mserver.getCurrentWrittenBytes();
    }
    
    
    
}
