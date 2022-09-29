/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.decanter.core;

import org.apache.iotdb.db.conf.IoTDBConstant;
import org.apache.iotdb.db.sync.conf.SyncSenderConfig;
import org.apache.iotdb.db.sync.conf.SyncSenderDescriptor;
import org.apache.iotdb.tsfile.common.constant.TsFileConstant;
import org.apache.karaf.main.ConfigProperties;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
public class IoTDBSyncFileConfigLoader {

    private SyncSenderDescriptor confSyncDescriptor = null;
    private SyncSenderConfig conf = null;
    private BundleContext bundleContext;

    public IoTDBSyncFileConfigLoader(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }    
        
    public void init(){
        String etcKaraf = System.getProperty(ConfigProperties.PROP_KARAF_ETC);
        System.setProperty(IoTDBConstant.IOTDB_CONF, etcKaraf); 
        System.setProperty(TsFileConstant.TSFILE_CONF, etcKaraf); 
        confSyncDescriptor = SyncSenderDescriptor.getInstance();
        System.out.println("Paso por aqui...: " + etcKaraf);
    }
    
    public void destroy(){
        
    }
    

    
}
