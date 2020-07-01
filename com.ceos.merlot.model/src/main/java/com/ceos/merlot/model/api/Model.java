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
public interface Model {
    
    public void init();
    
    public void destroy();
    
    public void start();
    
    public void stop();   
    
    /*
    * Retrieves the Domino of the specified object. 
    * The result is a String that follows the style defined in RFC-1034.
    * The String Domain is rebuilt based on the domain tree defined according 
    * to ISA-S88 / Pack-ML.
    * @param id: unique id identification of the object.
    * @return: Domain to which the object belongs.
    */
    public String getDomain(Long id);
    
}
