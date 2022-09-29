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
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Dictionary;
import java.util.concurrent.TimeUnit;
import org.apache.karaf.config.core.ConfigRepository;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class GrafanaStreamAppenderImpl implements GrafanaService, EventHandler {
    
    private final static Logger LOGGER = LoggerFactory.getLogger(GrafanaLokiAppenderImpl.class);
    
    private static final String CONFIG_PID = "com.ceos.merlot.grafana";    
    private final BundleContext bundleContext;
    private final ConfigRepository configRepository;
    
    private final String TIMESTAMP_ITEM = "TIMESTAMP";
    private final String LOG_ITEM = "LOG";
    
    private boolean started = false;    
    private String url;
    private String tenant = null;
    private String username = null;
    private String password = null;
    private String key = null;    
    private Dictionary<String, Object> config;    

    public GrafanaStreamAppenderImpl(BundleContext bundleContext, ConfigRepository configRepository) {
        this.bundleContext = bundleContext;
        this.configRepository = configRepository;
    }

    @Override
    public void init() {
        System.out.println("GrafanaStreamAppenderImpl.init");
    }

    @Override
    public void destroy() {
        System.out.println("GrafanaStreamAppenderImpl.destroy");
    }

    @Override
    public void start() {
        System.out.println("GrafanaStreamAppenderImpl.start");
        started = true;
    }

    @Override
    public void stop() {
        System.out.println("GrafanaStreamAppenderImpl.stop");
        started = false;
    }    
    
    
    public void update(Dictionary<String, Object> config) {
        System.out.println("Actualizando Stream");        
        this.config = config;
        url = (config.get("stream.url") != null) ? (String) config.get("stream.url") : "http://localhost:3000/api/live/push/merlot";
        tenant = (config.get("stream.tenant") != null) ? (String) config.get("stream.tenant") : null;
        username = (config.get("stream.username") != null) ? (String) config.get("stream.username") : null;
        password = (config.get("stream.password") != null) ? (String) config.get("stream.password") : null;
        key = (config.get("stream.key") != null) ? (String) config.get("stream.key") : null;
    }
    
    
    @Override
    public void handleEvent(Event event) {
        System.out.println("GrafanaStreamAppenderImpl: " + event.getProperty("prueba"));        
        if (!started) return;        
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                if (tenant != null) {
                    connection.setRequestProperty("X-Scope-OrgId", tenant);
                }
                if (username != null) {
                    String authentication = username + ":" + password;
                    byte[] encodedAuthentication = Base64.getEncoder().encode(authentication.getBytes(StandardCharsets.UTF_8));
                    String authenticationHeader = "Basic " + new String(encodedAuthentication);
                    connection.setRequestProperty("Authorization", authenticationHeader);
                }
                
                String log = (String) event.getProperty(LOG_ITEM);
                Instant timestamp = (Instant) event.getProperty(TIMESTAMP_ITEM);  
                
                long nanos = TimeUnit.SECONDS.toNanos(timestamp.getEpochSecond()) + timestamp.getNano();
                String jsonPush = "{\"streams\": [{ \"stream\": { \"job\": \"decanter\" }, \"values\": [ [ \"" + nanos + "\", \"" + log + "\" ] ] }]}";
                
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
                    writer.write(jsonPush);
                    writer.flush();
                }
                if (connection.getResponseCode() != 204) {
                    LOGGER.warn("Can't push to Loki ({}): {}", connection.getResponseCode(), connection.getResponseMessage());
                }
            } catch (Exception e) {
                LOGGER.warn("Error occurred while pushing to Loki", e);
            }
    }    

}
