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

import org.apache.plc4x.java.s7.readwrite.types.ModeTransitionType;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 *
 * @author cgarcia
 */
public class S7EventProcessorImpl implements EventHandler{

    private static final String EVENT_MODE = "MODE";
    private static final String EVENT_SYS = "SYS";
    private static final String EVENT_USER = "USER";
    private static final String EVENT_ALARM8 = "ALARM8";    
    private static final String EVENT_NOTIFY = "NOTIFY"; 
    private static final String EVENT_ALARMSQ = "ALARMSQ"; 
    private static final String EVENT_ALARMS = "ALARMS";   
    private static final String EVENT_NOTIFY8 = "NOTIFY8";     
    private static final String EVENT_ALARMACK_IND = "ALARMACK_IND";
    
    @Override
    public void handleEvent(Event event) {
        StringBuilder sb = new StringBuilder();
        String eventtype = (String) event.getProperty("TYPE");
        if (EVENT_MODE.equals(eventtype))   sb.append(ModeEvent(event));
        if (EVENT_SYS.equals(eventtype))    sb.append(SysEvent(event));
        if (EVENT_USER.equals(eventtype))   sb.append(UserEvent(event));
        
    }
    
    private String ModeEvent(Event event){
        StringBuilder sb = new StringBuilder("CPU is in : ");        
        if (ModeTransitionType.isDefined((short) event.getProperty("CURRENT_MODE"))){
            short currentmode = (short) event.getProperty("CURRENT_MODE");
            sb.append(ModeTransitionType.enumForValue(currentmode).name());
        } else {
            sb.append("UNDEFINED");
        }
        return sb.toString();
    }
    
    private String SysEvent(Event event){
    
        return null;
    }    
    
    private String UserEvent(Event event){
    
        return null;
    }    
    
    private String AlarmEvent(Event event){
    
        return null;
    }        
    
}
