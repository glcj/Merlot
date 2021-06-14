/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.grafana.loki.impl;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Dictionary;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class LokiAppenderImpl implements EventHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(LokiAppenderImpl.class);
    
    private final String TIMESTAMP_ITEM = "TIMESTAMP";
    private final String LOG_ITEM = "LOG";
    
    private String url;
    private String tenant = null;
    private String username = null;
    private String password = null;
    private Dictionary<String, Object> config;    

    public LokiAppenderImpl() {
    }
        
    public void activate(Dictionary<String, Object> config) {
        this.config = config;
        url = (config.get("loki.url") != null) ? (String) config.get("loki.url") : "http://localhost:3100/loki/api/v1/push";
        tenant = (config.get("loki.tenant") != null) ? (String) config.get("loki.tenant") : null;
        username = (config.get("loki.username") != null) ? (String) config.get("loki.username") : null;
        password = (config.get("loki.password") != null) ? (String) config.get("loki.password") : null;
    }
    
    
    @Override
    public void handleEvent(Event event) {
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
                
                String jsonPush = "{\"streams\": [{ \"stream\": { \"job\": \"decanter\" }, \"values\": [ [ \"" + System.currentTimeMillis() * 1000L * 1000L + "\", \"" + log + "\" ] ] }]}";
                
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
