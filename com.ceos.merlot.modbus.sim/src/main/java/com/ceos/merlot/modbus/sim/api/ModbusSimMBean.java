/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.modbus.sim.api;

/**
 *
 * @author cgarcia
 */
public interface ModbusSimMBean {
    
    public void init();
    
    public void destroy();
    
    public void start();
    
    public void stop();
    
    public void setFunction(String strFunction);
    
    public String getFunction();

    public double getF();
    
    public void setF(double f);    
    
    public double getX();    
    
    public void setX(double x);     
    
    public double getY();    
    
    public void setY(double y);     
    
    public double getZ();    
    
    public void setZ(double z);     
    
}
