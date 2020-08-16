/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.model.impl;

import com.ceos.merlot.model.api.Model;
import com.ceos.merlot.model.api.ModelMBean;

/**
 *
 * @author cgarcia
 */
public class ModelMBeanImpl implements ModelMBean {

    private final Model model;

    public ModelMBeanImpl(Model model) {
        this.model = model;
    }
                
    @Override
    public String getEnterprise() {
        return null;
    }

    @Override
    public String getPlant() {
        return null;
    }

    @Override
    public String getArea() {
        return null;
    }

    @Override
    public String getCell() {
        return null;
    }

    @Override
    public String getUnit() {
        return null;
    }

    @Override
    public String getDomain(Long id) {
        return null;
    }

    @Override
    public String getDomain(String pv) {
        return null;
    }

    @Override
    public String getStorageGroup() {
        return null;
    }
    
}
