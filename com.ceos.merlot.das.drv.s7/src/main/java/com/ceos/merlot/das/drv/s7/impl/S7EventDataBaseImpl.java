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

import com.ceos.merlot.das.drv.s7.api.S7EventDataBase;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author cgarcia
 */
public class S7EventDataBaseImpl  implements S7EventDataBase{
        
    private static HashMap<String, ASMessages> asdb;

    public S7EventDataBaseImpl() {
        asdb = new HashMap();
    }

    @Override
    public void addAS(String as) {
        ASMessages asmgs = new ASMessages();
        asdb.put(as, asmgs);
    }

    @Override
    public void delAS(String as) {
        asdb.get(as).modes = null;
        asdb.get(as).syss = null;
        asdb.get(as).userss = null;
        asdb.get(as).alarmss = null;
        asdb.get(as).alarm8s = null;        
        asdb.remove(as);
    }
    
           
    @Override
    public void reloadModeEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reloadSysEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reloadUserEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reloadAlarmSEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reloadAlarm8EventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Long> idsModeEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Long> idsSysEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Long> idsUserEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Long> idsAlarmSEventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Set<Long> idsAlarm8EventString(String as) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getModeEventString(String as, Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSysEventString(String as, Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getUserEventString(String as, Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAlarmSEventString(String as, Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAlarm8EventString(String as, Long id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    private class ASMessages {
        public HashMap<Long, String> modes;
        public HashMap<Long, String> syss;
        public HashMap<Long, String> userss;
        public HashMap<Long, String> alarmss;
        public HashMap<Long, String> alarm8s;
        
        public ASMessages() {
            modes   = new HashMap();
            syss    = new HashMap();
            userss  = new HashMap();
            alarmss = new HashMap();
            alarm8s = new HashMap(); 
        }
    }
    
}
