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

package com.ceos.merlot.grafana.impl;

import com.ceos.merlot.grafana.api.GrafanaService;
import java.util.Dictionary;
import org.apache.karaf.config.core.ConfigRepository;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class GrafanaJsonConnectorImpl implements GrafanaService {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GrafanaJsonConnectorImpl.class);
    
    private static final String CONFIG_PID = "com.ceos.merlot.grafana";    
    private final BundleContext bundleContext;
    private final ConfigRepository configRepository; 

    public GrafanaJsonConnectorImpl(BundleContext bundleContext, ConfigRepository configRepository) {
        this.bundleContext = bundleContext;
        this.configRepository = configRepository;
    }
    
    
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
    public void update(Dictionary<String, Object> config) {
        System.out.println("Actualizando Connector");
    }
    
}
