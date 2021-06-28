/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.ceos.merlot.grafana.core;

import com.ceos.merlot.grafana.api.GrafanaService;
import java.util.Dictionary;
import java.util.logging.Level;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class GrafanaManagedService implements ManagedService  {

    private final static Logger LOGGER = LoggerFactory.getLogger(GrafanaManagedService .class);
    
    private final String CONFIG_PID = "com.ceos.merlot.grafana.api.GrafanaService";
    private final BundleContext bundleContext;

    public GrafanaManagedService(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
        
    @Override
    public void updated(Dictionary<String, ?> dict) throws ConfigurationException {
        System.out.println("Diccionario: " + dict.toString());        
        try {
            ServiceReference<?>[] serviceReferences = bundleContext.getServiceReferences(CONFIG_PID, null);
            for (ServiceReference ref:serviceReferences){
                GrafanaService service = (GrafanaService) bundleContext.getService(ref);
                service.stop();
                service.update((Dictionary<String, Object>) dict);
                service.start();
            }
        } catch (Exception ex) {
            LOGGER.error(ex.toString());
        }

    }
    
}
