/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.das.drv.mb.core;

import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
public class MBDeviceSubscriptionHandler {
    
    private static BundleContext bc;
    private static String topic;

    public MBDeviceSubscriptionHandler(BundleContext bc, String topic) {
        this.bc = bc;
        this.topic = topic;
    }
    
    
    
    
    
}
