/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.modbus.sim.impl;

import com.ceos.merlot.modbus.sim.api.ModbusSim;
import com.ceos.merlot.modbus.sim.api.ModbusSimMBean;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

/**
 *
 * @author cgarcia
 */
public class ModbusSimMBeanImpl extends StandardMBean implements ModbusSimMBean {

    private final ModbusSim ms; 
    
    public ModbusSimMBeanImpl(ModbusSim ms) throws NotCompliantMBeanException {
        super(ModbusSimMBean.class);
        this.ms = ms;
    }
    
    @Override
    public void init() {
        
    }

    @Override
    public void destroy() {
        
    }

    @Override
    public void start() {
        ms.start();
    }

    @Override
    public void stop() {
        ms.stop();
    }

    @Override
    public void setFunction(String strFunction) {
        ms.setFunction(strFunction);
    }

    @Override
    public String getFunction() {
        return ms.getFunction();
    }

    @Override
    public double getF() {
        return ms.getF();
    }

    @Override
    public void setF(double f) {
        ms.setF(f);
    }
    
    @Override
    public double getX() {
        return ms.getX();
    }

    @Override
    public void setX(double x) {
        ms.setX(x);
    }    
    
    @Override
    public double getY() {
        return ms.getY();
    }

    @Override
    public void setY(double y) {
        ms.setY(y);
    }    
    
    @Override
    public double getZ() {
        return ms.getZ();
    }
    
    @Override
    public void setZ(double z) {
        ms.setZ(z);
    }    
    
}
