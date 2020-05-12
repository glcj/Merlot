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

package com.ceos.merlot.pva.command;

import com.ceos.merlot.pva.api.MerlotPvaClient;
import java.io.PrintStream;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.epics.pvaClient.PvaClient;
import org.epics.pvaClient.PvaClientChannel;
import org.epics.pvaClient.PvaClientGet;

/**
 *
 * @author cgarcia
 */
@Command(scope = "pva", name = "pvinfo", description = "Retrieves the introspection interface for selected channels .")
@Service
public class MerlotPVInfoCommand implements Action {

    private PrintStream out;
        
    @Reference
    Session session;
        
    @Reference
    MerlotPvaClient pvaClientService;
    
    @Option(name = "-w", aliases = "--wait", description = "Wait time, specifies timeout, default is 3.0 second(s).", required = false, multiValued = false)
    int wait = 3;

    @Argument(index = 0, name = "channel", description = "Channel <PV name>", required = true, multiValued = false)
    String strChannel = null;       
    
    @Override
    public Object execute() throws Exception {
        out = session.getConsole();
        PvaClient pvaClient = pvaClientService.getPvaClient();
        if (pvaClient == null) {
            out.println("Global PvaClient is not deployed.");
            return null;
        }
       
        PvaClientChannel  clientChannel =  pvaClient.createChannel(strChannel, "pva");
        clientChannel.connect(wait);
        
        out.println("ChannelName: " + clientChannel.getChannel().getChannelName());
        out.println("RemoteAddress: " + clientChannel.getChannel().getRemoteAddress());
        out.println("Type: \r\n");
        PvaClientGet getChannel = clientChannel.get();
        out.println(getChannel.getData().getStructure());
            
        clientChannel.destroy();
        //pvaClient.clearRequester();
        //pva.destroy();
        
        return null;
    }
    
}
