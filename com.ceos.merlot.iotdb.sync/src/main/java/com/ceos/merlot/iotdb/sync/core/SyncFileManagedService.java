/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.sync.core;

import java.util.Dictionary;
import java.util.Enumeration;
import org.apache.iotdb.db.conf.IoTDBConstant;
import org.apache.iotdb.db.sync.conf.SyncSenderConfig;
import org.apache.iotdb.db.sync.conf.SyncSenderDescriptor;
import org.apache.karaf.main.ConfigProperties;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author cgarcia
 */
public class SyncFileManagedService implements ManagedService {

    private SyncSenderDescriptor confSyncDescriptor = null;
    private SyncSenderConfig conf = null;
    private BundleContext bundleContext;

    public SyncFileManagedService(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }    
    
    
    @Override
    public void updated(Dictionary<String, ?> props) throws ConfigurationException {
        String key = null;
        if (props == null) return;
        System.out.println("Actualizando la configuracion...: " + props.size());        
        Enumeration<String> keys = props.keys();
        String etcKaraf = System.getProperty(ConfigProperties.PROP_KARAF_ETC);
        System.setProperty(IoTDBConstant.IOTDB_CONF, etcKaraf);   
        
        confSyncDescriptor = SyncSenderDescriptor.getInstance();
        System.out.println("Paso por aqui: " + keys);
        
        /*
        while (keys.hasMoreElements()){
            key = keys.nextElement();
             if(key.equalsIgnoreCase("server_ip"))
                 conf.setServerIp((String) properties.get(key));
             if(key.equalsIgnoreCase("server_port"))
                 conf.setServerPort(Integer.parseInt((String) properties.get(key)));
             if(key.equalsIgnoreCase("sync_period_in_second"));
                
             if(key.equalsIgnoreCase("sync_storage_groups"));
             if(key.equalsIgnoreCase("max_number_of_sync_file_retry"));
        }
        */
    }
    
}
