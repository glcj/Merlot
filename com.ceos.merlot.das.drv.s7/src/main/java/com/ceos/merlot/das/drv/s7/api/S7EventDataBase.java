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

package com.ceos.merlot.das.drv.s7.api;

import java.util.Set;

/**
 *
 * @author cgarcia
 */
public interface S7EventDataBase {
        
    
    public void addAS(String as);
    
    public void delAS(String as);
    
    public void reloadModeEventString(String as);
    
    public void reloadSysEventString(String as);
    
    public void reloadUserEventString(String as);
    
    public void reloadAlarmSEventString(String as);
    
    public void reloadAlarm8EventString(String as);
    
    
    public Set<Long> idsModeEventString(String as);
    
    public Set<Long> idsSysEventString(String as);
    
    public Set<Long> idsUserEventString(String as);
    
    public Set<Long> idsAlarmSEventString(String as);
    
    public Set<Long> idsAlarm8EventString(String as);    

        
    public String getModeEventString(String as, Long id);
    
    public String getSysEventString(String as, Long id);
    
    public String getUserEventString(String as, Long id);
    
    public String getAlarmSEventString(String as, Long id);
    
    public String getAlarm8EventString(String as, Long id);    
    
}
