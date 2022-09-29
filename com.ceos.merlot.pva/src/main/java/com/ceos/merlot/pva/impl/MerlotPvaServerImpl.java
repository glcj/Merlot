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

package com.ceos.merlot.pva.impl;

import com.ceos.merlot.pva.api.MerlotPvaServer;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.epics.pvaccess.PVAException;
import org.epics.pvaccess.client.ChannelProvider;
import org.epics.pvaccess.impl.remote.utils.GUID;
import org.epics.pvaccess.server.impl.remote.ServerContextImpl;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class MerlotPvaServerImpl implements MerlotPvaServer  {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(MerlotPvaServerImpl.class);     
    
    ServerContextImpl context;
    
    ChannelProvider channelProvider;

    public MerlotPvaServerImpl() {
    }          
    
    @Override
    public void init() {
        try {
            context = ServerContextImpl.startPVAServer(channelProvider.getProviderName(),0,true,null);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(os);
            context.printInfo(ps);           
            GUID guid = new GUID(context.getGUID());
            String strGuid = "GUID: " + guid.toString();
            ps.write(strGuid.getBytes());
            LOGGER.info(os.toString());  
        } catch (Exception ex) {
            LOGGER.info(ex.toString());
        }
    }

    @Override
    public void destroy() {
        try {
            context.destroy();
        } catch (PVAException | IllegalStateException ex) {
            LOGGER.info(ex.toString());
        }
    }

    @Override
    public void setChannelProvider(ChannelProvider chp) {
        channelProvider = chp;      
    }
    
}
