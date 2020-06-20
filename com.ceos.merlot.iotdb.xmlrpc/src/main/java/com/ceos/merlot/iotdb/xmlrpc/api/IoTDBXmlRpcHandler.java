/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.xmlrpc.api;

/**
 *
 * @author cgarcia
 */
public interface IoTDBXmlRpcHandler {
    
    public String execute(String method) throws Exception;    
    
    
}
