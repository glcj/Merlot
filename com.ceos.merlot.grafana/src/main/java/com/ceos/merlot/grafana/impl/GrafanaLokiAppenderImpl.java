/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.grafana.impl;

import com.ceos.merlot.grafana.api.GrafanaService;
import com.ceos.merlot.model.core.PhysicalModelEnum;
import io.netty.buffer.ByteBuf;
import static io.netty.buffer.Unpooled.wrappedBuffer;
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
 * This Loki Appender is based on the reference implementation 
 * of Karaf (https://github.com/apache/karaf-decanter/tree/main/appender/loki).
 *
 * @author cgarcia
 */
public class GrafanaLokiAppenderImpl implements GrafanaService, EventHandler {

    private final static Logger LOGGER = LoggerFactory.getLogger(GrafanaLokiAppenderImpl.class);
    
    private final String TIMESTAMP = "TIMESTAMP";
    private final String LOG = "LOG";
    private static final String CONFIG_PID = "com.ceos.merlot.grafana";  
    
    private static final String STR_STREAM = "{\"streams\": [{ \"stream\": { ";
    private static final String STR_DQUOTES = "\"";
    private static final String STR_COLON = "\":\""; 
    private static final String STR_VALUES = " }, \"values\": [ [ \""; 
    private static final String STR_COMMA = "\", \""; 
    private static final String STR_ENDING = "\" ] ] }]}"; 
    
    
    private final BundleContext bundleContext;
    private final ConfigRepository configRepository;
    
    private boolean started = false;
    private String url;
    private String tenant = null;
    private String username = null;
    private String password = null;
    private String token = null;    
    private Dictionary<String, Object> config; 
    private StringBuilder sb = new StringBuilder();
    

    public GrafanaLokiAppenderImpl(BundleContext bundleContext, ConfigRepository configRepository) {
        this.bundleContext = bundleContext;
        this.configRepository = configRepository;
    }


            
    @Override
    public void init() {
        System.out.println("GrafanaLokiAppenderImpl.init");
    }

    @Override
    public void destroy() {
        System.out.println("GrafanaLokiAppenderImpl.destroy");
    }

    @Override
    public void start() {
        System.out.println("GrafanaLokiAppenderImpl.start");
        started = true;
    }

    @Override
    public void stop() {
        System.out.println("GrafanaLokiAppenderImpl.stop");
        started = false;
    }  
    
    public void update(Dictionary<String, Object> config) {
        System.out.println("Actualizando Loki");
        this.config = config;
        url = (config.get("loki.url") != null) ? (String) config.get("loki.url") : "http://localhost:3100/loki/api/v1/push";
        tenant = (config.get("loki.tenant") != null) ? (String) config.get("loki.tenant") : null;
        username = (config.get("loki.username") != null) ? (String) config.get("loki.username") : null;
        password = (config.get("loki.password") != null) ? (String) config.get("loki.password") : null;
        token = (config.get("loki.token") != null) ? (String) config.get("loki.token") : null;        
    }    
    
    
    @Override
    public void handleEvent(Event event) {
        System.out.println("GrafanaLokiAppenderImpl: " + event.getProperty("prueba"));          
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
                
                String log = (String) event.getProperty(LOG);
                Instant timestamp = (Instant) event.getProperty(TIMESTAMP);                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      
                long nanos = TimeUnit.SECONDS.toNanos(timestamp.getEpochSecond()) + timestamp.getNano();
                
                //TODO: Repleace StringBuilder with ByteBuf
                sb = new StringBuilder(STR_STREAM);
                
                //Here comes the S88 to labels
                for(PhysicalModelEnum model:PhysicalModelEnum.values()) {
                    if (event.getProperty(model.name()) != null) {
                        sb.append(STR_DQUOTES).
                           append(model.name()).
                           append(STR_COLON).
                           append(event.getProperty(model.name())).
                           append(STR_DQUOTES).
                           append(",");                            
                    }
                }
                sb.deleteCharAt(sb.lastIndexOf(","));
                sb.append(STR_VALUES).
                   append(nanos).
                   append(STR_COMMA).
                   append(log).
                   append(STR_ENDING);

                //String jsonPush = "{\"streams\": [{ \"stream\": { \"job\": \"decanter\" }, \"values\": [ [ \"" + nanos + "\", \"" + log + "\" ] ] }]}";
                System.out.println("Body: " + sb.toString());
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()))) {
                    writer.write(sb.toString());
                    writer.flush();
                }
                if (connection.getResponseCode() != 204) {
                    LOGGER.warn("Can't push to Loki ({}): {}", connection.getResponseCode(), connection.getResponseMessage());
                }
                
                sb = null;
            } catch (Exception e) {
                LOGGER.warn("Error occurred while pushing to Loki", e);
            }
    }


    
}
