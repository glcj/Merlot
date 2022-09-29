/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.modbus.svr.api;

/**
 *
 * @author cgarcia
 */
public interface ModbusServerMBean {
    
    public void init() throws Exception;

    public void destroy() throws Exception;

    public void start();

    public void stop();

    public void setPort(int port);

    public int getPort();

    public void setHost(String host);

    public String getHost();
    
    public boolean isStarted();
    
    public long getCheckInterval();
    
    public void setCheckInterval(long newCheckInterval);
        
    public long getLastReadThroughput();
    
    public long getLastWriteThroughput();
    
    public long getCumulativeReadBytes();
    
    public long getCumulativeWrittenBytes();
    
    public long getCurrentReadBytes();
    
    public long getCurrentWrittenBytes();        
    
}
