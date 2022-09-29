/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.svr.impl;

import com.ceos.merlot.iotdb.svr.api.IoTDBServerConfigLoader;
import java.io.File;
import org.apache.iotdb.db.conf.IoTDBConstant;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.iotdb.tsfile.common.conf.TSFileConfig;
import org.apache.iotdb.tsfile.common.conf.TSFileDescriptor;
import org.apache.karaf.main.ConfigProperties;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
public class IoTDBServerConfigLoaderImpl implements IoTDBServerConfigLoader {
    private BundleContext bundleContext;
    private IoTDBDescriptor iotdbDescriptor = null;       
    
    public IoTDBServerConfigLoaderImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    public void init(){
        String baseIoTDB = System.getProperty(ConfigProperties.PROP_KARAF_BASE) + File.separator + "iotdb";        
        String etcKaraf = System.getProperty(ConfigProperties.PROP_KARAF_ETC);
        System.setProperty(IoTDBConstant.IOTDB_HOME, baseIoTDB);         
        System.setProperty(IoTDBConstant.IOTDB_CONF, etcKaraf);         
        iotdbDescriptor = IoTDBDescriptor.getInstance();   
        
        TSFileConfig confTSF = TSFileDescriptor.getInstance().getConfig();
    }   
    
    public void destroy(){
        
    }    
    
}
