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

package com.ceos.merlot.db.core;

import org.epics.nt.NTScalar;
import org.epics.nt.NTScalarArray;
import org.epics.nt.NTScalarArrayBuilder;
import org.epics.nt.NTScalarBuilder;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.PVUInt;
import org.epics.pvdata.pv.PVUIntArray;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdatabase.PVRecord;

/**
 *
 * @author cgarcia
 */
public class DBUIntFactory extends DBBaseFactory {
    private static FieldCreate fieldCreate = FieldFactory.getFieldCreate();

    
    @Override
    public PVRecord create(String recordName) {
        NTScalarBuilder ntScalarBuilder = NTScalar.createBuilder();
        PVStructure pvStructure = ntScalarBuilder.
            value(ScalarType.pvUInt).
            addDescriptor(). 
            add("id", fieldCreate.createScalar(ScalarType.pvString)).
            add("scan_rate", fieldCreate.createScalar(ScalarType.pvString)).
            add("scan_enable", fieldCreate.createScalar(ScalarType.pvBoolean)).
            add("write_enable", fieldCreate.createScalar(ScalarType.pvBoolean)).              
            addAlarm().
            addTimeStamp().
            addDisplay().
            addControl(). 
            createPVStructure();   
        PVRecord pvRecord = new DBUIntRecord(recordName,pvStructure);
        return pvRecord;
    }

    @Override
    public PVRecord createArray(String recordName, int length) {
        NTScalarBuilder ntScalarBuilder = NTScalar.createBuilder();                
        NTScalarArrayBuilder ntScalarArrayBuilder = NTScalarArray.createBuilder();
        PVStructure pvStructure = ntScalarArrayBuilder.
            value(ScalarType.pvUInt).
            addDescriptor(). 
            add("id", fieldCreate.createScalar(ScalarType.pvString)).
            add("scan_rate", fieldCreate.createScalar(ScalarType.pvString)).
            add("scan_enable", fieldCreate.createScalar(ScalarType.pvBoolean)).
            add("write_enable", fieldCreate.createScalar(ScalarType.pvBoolean)).
            addAlarm().
            addTimeStamp().
            addDisplay().
            addControl(). 
            createPVStructure();
        PVUIntArray pvValue = (PVUIntArray) pvStructure.getScalarArrayField("value", ScalarType.pvUInt);
        pvValue.setCapacity(length);
        pvValue.setLength(length);               
        PVRecord pvRecord = new DBUIntRecord(recordName,pvStructure);
        return pvRecord;
    }
    
    class DBUIntRecord extends PVRecord {
        
        private PVUInt value;      
        
        public DBUIntRecord(String recordName,PVStructure pvStructure) {
            super(recordName, pvStructure);
            value = (PVUInt) pvStructure.getIntField("value");
        }    

        /**
         * Implement real time data to the record.
         * The main code is here.
         */
        public void process()
        {
            super.process();

        } 
    }
    
}
