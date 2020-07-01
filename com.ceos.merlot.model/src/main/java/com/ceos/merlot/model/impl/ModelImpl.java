/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.model.impl;

import com.ceos.merlot.model.api.Model;

/**
 *
 * @author cgarcia
 */
public class ModelImpl implements Model {

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public String getDomain(Long id) {
        if (id == 1) return "root.site01.area01.cell01.unit01.";
        if (id == 2) return "root.site01.area01.cell01.unit01.";        
        if (id == 3) return "root.site01.area01.cell01.unit01.";   
        if (id == 4) return "root.site01.area01.cell01.unit05.";          
        if (id == 5) return "root.site01.area02.cell01.unit08.";  
        if (id == 6) return "root.site01.area02.cell01.unit08.";  
        if (id == 7) return "root.site01.area03.cell01.unit10.";          
        if (id == 8) return "root.site01.area03.cell01.unit10.";          
        
        return null;
    }
    
}
