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

package com.ceos.merlot.db.api;


import com.ceos.merlot.scheduler.api.Job;
import java.util.Collection;
import org.epics.pvdatabase.PVRecord;
import org.epics.pvdatabase.PVRecordClient;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
public interface DBControl extends PVRecordClient, Job {
        
    void setBundleContext(BundleContext bundleContext);
    
    void attach(String device, Collection<PVRecord> pvRecords);    
}
