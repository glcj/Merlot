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

package com.ceos.merlot.modbus.sim.impl;

import com.ceos.merlot.modbus.dev.api.ModbusDevice;
import com.ceos.merlot.scheduler.api.JobContext;
import com.fathzer.soft.javaluator.DoubleEvaluator;
import com.fathzer.soft.javaluator.StaticVariableSet;

/**
 *
 * @author cgarcia
 */
public class ModbusSimFuncImpl extends ModbusSimImpl {

    public ModbusSimFuncImpl(ModbusDevice mbdev) {
        super(mbdev);
        eval = new DoubleEvaluator();
        variables = new StaticVariableSet<Double>();
    }

    @Override
    public void setFunction(String strFunction) {
        this.strFunction = strFunction;
    }
        
    @Override
    public void execute(JobContext context) {
        Double result = 0.0;
        if (!started) return;
        f = getValue(mbdev,tagF,0);
        x = getValue(mbdev,tagX,0); 
        y = getValue(mbdev,tagY,0);
        z = getValue(mbdev,tagZ,0);
        if (tagF != null) {
            variables.set("f", f);
        }
        if (tagX != null) {
            variables.set("x", x);
        }
        if (tagY != null) {
            variables.set("y", y);
        }
        if (tagZ != null) {
            variables.set("z", z);
        }        
        
        if (strFunction != null) {
            result = eval.evaluate(strFunction, variables);
        }
        
        if (tagF != null) {
            putValue(result, mbdev,tagF,0);
        }
             
        
    }
    
    

}
