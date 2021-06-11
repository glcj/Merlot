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

package com.ceos.merlot.das.drv.s7.impl;

import com.ceos.merlot.api.Driver;
import com.ceos.merlot.api.DriverCallback;
import com.ceos.merlot.api.DriverEvent;
import com.ceos.merlot.das.drv.basic.impl.BasicDeviceImpl;
import com.ceos.merlot.das.drv.s7.api.S7Device;
import com.ceos.merlot.das.drv.s7.api.S7Driver;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.model.PlcSubscriptionHandle;
import org.apache.plc4x.java.s7.events.S7AlarmEvent;
import org.apache.plc4x.java.s7.events.S7ModeEvent;
import org.apache.plc4x.java.s7.events.S7SysEvent;
import org.apache.plc4x.java.s7.events.S7UserEvent;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class S7DeviceImpl extends BasicDeviceImpl implements S7Device {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(S7DeviceImpl.class);    
    public static final String S7_DEVICE_CATEGORY = "com.ceos.s7";
    private static final Pattern EVENT_PATTERN =
            Pattern.compile("(?<substr>subscription=)|(?<mode>MODE)|(?<sys>SYS)|(?<usr>USR)|(?<alm>ALM)");
        
    private static final String SUBSCRIPTION_TYPE = "substr";
    private static final String EVENT_MODE_TYPE = "mode";
    private static final String EVENT_SYSTEM_TYPE = "sys";
    private static final String EVENT_USER_TYPE = "usr";
    private static final String EVENT_ALARM_TYPE = "alm";
    
    
    private Matcher matcher;
    private PlcSubscriptionRequest.Builder plcsubscription = null;
    private PlcSubscriptionRequest sub = null;
    private PlcSubscriptionResponse subresponse = null;
        
    private S7Driver devDriver = null;
    
    protected final EventAdmin eventAdmin;
    
    Consumer<PlcSubscriptionEvent> eventConsumer = new Consumer<PlcSubscriptionEvent>() {
        @Override
        public void accept(PlcSubscriptionEvent event) {
            Event msgEvent = null;
            if (event instanceof S7ModeEvent)
            msgEvent = new Event("decanter/process/s7/MODE", ((S7ModeEvent) event).getMap());
            if (event instanceof S7SysEvent)
            msgEvent = new Event("decanter/process/s7/SYS", ((S7SysEvent) event).getMap());  
            if (event instanceof S7UserEvent)
            msgEvent = new Event("decanter/process/s7/USER", ((S7UserEvent) event).getMap());              
            if (event instanceof S7AlarmEvent)
            msgEvent = new Event("decanter/process/s7/ALARM", ((S7AlarmEvent) event).getMap());
            if (msgEvent != null)
            eventAdmin.sendEvent(msgEvent);
        }
    };
    
     
    public S7DeviceImpl(BundleContext bc, EventAdmin eventAdmin) {
        super(bc);
        this.eventAdmin = eventAdmin;
    }

    @Override
    public void init() throws Exception {
        super.init();
    }    
    
    @Override
    public void destroy() throws Exception {
        super.destroy();
    }

    @Override
    public void start() {     
        LOGGER.info("Starting S7Device ("+url+")");
        if (devDriver != null){
            devDriver.setUrl(url);
            devDriver.start();
            autostart = false;   
            doSubscription();
        } else {
            LOGGER.info("S7Device the S7Driver is null.");
            autostart = true;
        }
    }
       
    @Override
    public void stop() {
        if (devDriver != null){
            devDriver.stop();
        }        
    }
    
    @Override
    public void noDriverFound() {
        super.noDriverFound();
    }

    @Override
    public void attach(Driver driver) {    
        this.devDriver = (S7Driver) driver;
        if (autostart) start();
    }

    //TODO Chequeo de excepciones
    @Override
    public void ReadRequest(String index, String item, DriverCallback cb) {
        super.ReadRequest(index, item, cb);
    }    
    
    @Override
    public void WriteRequest(String scalar, String id, List<String> values, DriverCallback cb) {
        super.WriteRequest(scalar,id,values, cb);
    }

    @Override
    public void SubscriptionRequest(String... events) {
        plcsubscription = devDriver.getPlcConnection().subscriptionRequestBuilder();
        if ((plcsubscription != null) && (events.length > 0)) {
            for (String event:events) {
                plcsubscription.addEventField(event, event.toUpperCase());
            }
            sub = plcsubscription.build();
            try {
                subresponse = sub.execute().get();
            } catch (Exception ex) {
                LOGGER.info(ex.toString());
            } 
        };            
    }
        
    @Override
    public void UnsubscriptionRequest(String... events) {
        super.UnsubscriptionRequest(events);
    }

    @Override
    public void ConsumerRegister(String event, Consumer<PlcSubscriptionEvent> consumer) {
        subresponse.getSubscriptionHandle(event).register(consumer);
    }    
    
    @Override
    public void ConsumerUnRegister(String event) {
        super.ConsumerUnRegister(event); //To change body of generated methods, choose Tools | Templates.
    }

    
    //TODO Descargar la informaci�n devuelta en la consola
    @Override
    public void execute(DriverEvent cb) {
        super.execute(cb);
        PlcReadResponse plcResponse = cb.getPlcReadResponse();
        if (plcResponse != null) {
            plcResponse.getFieldNames().forEach( fieldname ->{
                System.out.println("\r\nS7DeviceImpl FieldName: " + fieldname);
            });
        };
    }

    private void doSubscription() {
        plcsubscription = devDriver.getPlcConnection().subscriptionRequestBuilder();
        if (plcsubscription != null) {   
            matcher = EVENT_PATTERN.matcher(url);
            matcher.find();
            try {
                if (matcher.group(SUBSCRIPTION_TYPE) != null) {
                    while(matcher.find()) {
                        plcsubscription.addEventField(matcher.group(0), matcher.group(0));
                    }
                    sub = plcsubscription.build();
                    subresponse = sub.execute().get();
                    subresponse.getSubscriptionHandles().forEach( h -> h.register(eventConsumer));  
                }
            } catch (Exception ex) {
                LOGGER.error(ex.toString());
            }
        }
    }
    
}
