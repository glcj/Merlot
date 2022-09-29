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

import com.ceos.merlot.das.drv.s7.api.S7EventProcessor;
import com.ceos.merlot.model.core.PhysicalModelEnum;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class S7AlarmEventProcessorImpl extends S7EventProcessorImpl{
    private static final Logger LOGGER = LoggerFactory.getLogger(S7AlarmEventProcessorImpl.class);

    public S7AlarmEventProcessorImpl(BundleContext bundleContext, EventAdmin eventAdmin) {
        super(bundleContext, eventAdmin);
    }
    
    /**
     * 
     */
    @Override    
    public String EventProcessing(final Event event) { 
        return null;
    }      
        
}
