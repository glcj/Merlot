/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.core;

import com.ceos.merlot.htc.api.HtcProducer;
import com.ceos.merlot.htc.api.HtcProducerMBean;
import com.ceos.merlot.htc.api.HtcService;
import com.ceos.merlot.htc.impl.HtcProducerImpl;
import com.ceos.merlot.htc.impl.HtcProducerMBeanImpl;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.karaf.config.core.ConfigRepository;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class HtcManagedService implements ManagedServiceFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(HtcManagedService.class);       
    private static String KEY_FILE = "felix.fileinstall.filename";
    private static Pattern FILE_PATTERN = Pattern.compile("-(?<groupname>\\w+).cfg$");
    private String filter_service =  "(&(" + Constants.OBJECTCLASS + "=" + HtcProducer.class.getName() + ")" +
                        "(htc.group=*))";
    private String filter_service_conf =  "(&(" + Constants.OBJECTCLASS + "=" + HtcProducer.class.getName() + ")" +
                        "(config.pid=*))";        
    private String filter_mbservice =  "(&(" + Constants.OBJECTCLASS + "=" + HtcProducerMBean.class.getName() + ")" +
                        "(jmx.objectname=*))"; 
    private String filter_mbservice_conf =  "(&(" + Constants.OBJECTCLASS + "=" + HtcProducerMBean.class.getName() + ")" +
                        "(config.pid=*))";     
    
    private final BundleContext bundleContext;
    private final HtcService htc;

    public HtcManagedService(BundleContext bundleContext, 
            HtcService htc) {
        this.bundleContext = bundleContext;
        this.htc = htc;
    }
        
    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updated(String pid, Dictionary<String, ?> props) throws ConfigurationException {
        String strGroup;
        try {
            String propfile = (String) props.get(KEY_FILE);
            if (propfile == null){
                LOGGER.info("Group file not available.");
                return;
            }
            Matcher groupName = FILE_PATTERN.matcher(propfile);
            groupName.find();
            if (groupName.group("groupname") == null){
                LOGGER.info("Group file name not available..");
                return;                
            }
            
            strGroup = groupName.group("groupname");
            String htcGroupFilter = filter_service.replace("*", strGroup);
            ServiceReference[] references = bundleContext.getServiceReferences((String) null, htcGroupFilter);
            if (references != null){
                for (ServiceReference reference:references){
                    HtcProducer producer = (HtcProducer) bundleContext.getService(reference);
                    producer.stop(bundleContext);
                }
            }
            
            HtcProducer htcProducer = new HtcProducerImpl(htc,strGroup);
            htcProducer.setConfigPid(pid);
            if (props!=null) {
                Enumeration<String> keys = props.keys();
                for (Enumeration e = props.keys(); e.hasMoreElements();) {
                    Object key = e.nextElement();
                    String configs = props.get(key).toString();
                    String[] config = configs.split(",");
                    if (config.length >= 2){
                        htcProducer.addTag((String) key, config[0], config[1], config[2]);
                    }
                }
                //TODO: Estandarizar a setServiceRegistration. Usar como patron.
                //TODO: Agregar config.pid
                Hashtable service_props = new Hashtable<String, String>();
                service_props.put("htc.group", strGroup);
                service_props.put("config.pid", pid); 
                ServiceRegistration servicereg = bundleContext.registerService(new String[]{HtcProducer.class.getName()}, htcProducer, service_props);                
                htcProducer.setServiceRegistration(servicereg);
                
                //TODO: Agregar config.pid
                Hashtable mbean_props = new Hashtable();
                String strProp  = "com.ceos.merlot:type=htc,name=com.ceos.htc.group,id="+strGroup;
                mbean_props.put("jmx.objectname", strProp);
                mbean_props.put("config.pid", pid); 
                
                String mbGroupFilter = filter_mbservice.replace("*", strProp);

                ServiceReference[] mbreferences = bundleContext.getServiceReferences((String) null, mbGroupFilter);
                if (mbreferences == null){
                    HtcProducerMBean htcmbean = new HtcProducerMBeanImpl(bundleContext,htcProducer); 
                    ServiceRegistration mbeanreg = bundleContext.registerService(new String[]{HtcProducerMBean.class.getName()}, htcmbean, mbean_props);
                    htcmbean.setServiceRegistration(mbeanreg);
                } else {
                    HtcProducerMBean htcmbean = (HtcProducerMBean) bundleContext.getService(mbreferences[0]);
                    htcmbean.setHtcProducer(htcProducer);
                }                
            }
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
    }

    //TODO: Ubicar los servicios con (config.pid=pid) y deregistrarlos.
    @Override
    public void deleted(String pid) {
        try {
            
            String mbGroupFilter = filter_mbservice_conf.replace("*", pid);
            ServiceReference[] references = bundleContext.getServiceReferences((String) null, mbGroupFilter );
            if (references != null){
                for (ServiceReference reference:references){
                    HtcProducerMBean mbean = (HtcProducerMBean) bundleContext.getService(reference);
                    mbean.getServiceRegistration().unregister();
                }
            }               
            
            String htcGroupFilter = filter_service_conf.replace("*", pid);
            references = bundleContext.getServiceReferences((String) null, htcGroupFilter);
            if (references != null){
                for (ServiceReference reference:references){
                    HtcProducer producer = (HtcProducer) bundleContext.getService(reference);
                    producer.stop();
                    producer.getServiceRegistration().unregister();
                }
            }                     
            
        } catch (Exception ex) {
            System.out.println("Si pasa por aca ya fue borrado el grupo...: " + pid);
            LOGGER.info(ex.getMessage());
        } 

    }
    
    public void destroy() {
        try {
            ServiceReference[] references = bundleContext.getServiceReferences(HtcProducer.class.getName(), null);
            if (references != null){
                for (ServiceReference reference:references){
                    HtcProducer producer = (HtcProducer) bundleContext.getService(reference);
                    producer.stop(bundleContext);
                }
            }            
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
        }
    }
    
}
