/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.model.api;

/**
 *
 * @author cgarcia
 */
public interface ModelMBean {
    
    public String getEnterprise();
    
    public String getPlant();
    
    public String getArea();
    
    public String getCell();
    
    public String getUnit(); 
    
    public String getDomain(Long id);

    public String getDomain(String pv);     

    public String getStorageGroup();
    
}
