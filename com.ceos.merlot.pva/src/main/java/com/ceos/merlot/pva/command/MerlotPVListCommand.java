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

import com.ceos.merlot.pva.core.MerlotRPCClientImpl;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.api.console.Session;
import org.epics.pvaccess.PVAConstants;
import org.epics.pvaccess.client.impl.remote.ClientContextImpl;
import org.epics.pvaccess.impl.remote.utils.GUID;
import org.epics.pvaccess.util.InetAddressUtil;
import org.epics.pvaccess.util.configuration.Configuration;
import org.epics.pvaccess.util.configuration.ConfigurationProvider;
import org.epics.pvaccess.util.configuration.impl.ConfigurationFactory;
import org.epics.pvdata.factory.FieldFactory;
import org.epics.pvdata.factory.PVDataFactory;
import org.epics.pvdata.misc.SerializeHelper;
import org.epics.pvdata.pv.FieldCreate;
import org.epics.pvdata.pv.PVDataCreate;
import org.epics.pvdata.pv.PVStructure;
import org.epics.pvdata.pv.ScalarType;
import org.epics.pvdata.pv.Structure;

/**
 *
 * @author cgarcia
 */
@Command(scope = "pva", name = "pvlist", description = "Retrieve information about servers.")
@Service
public class MerlotPVListCommand implements Action {
    
    FieldCreate fieldCreate = FieldFactory.getFieldCreate();
    PVDataCreate pvDataCreate = PVDataFactory.getPVDataCreate();
    
    private PrintStream out;
    
    @Reference
    Session session;
    
    @Reference
    ClientContextImpl context;
       
    @Option(name = "-w", aliases = "--wait", description = "Wait time, specifies timeout, default is 3.0 second(s).", required = false, multiValued = false)
    int wait = 3;    
    
    @Option(name = "-i", aliases = "--info", description = "Print server info (when server address list is given).", required = false, multiValued = false)
    boolean info;

    @Option(name = "-s", aliases = "--status", description = "Print server status (when server address list is given).", required = false, multiValued = false)
    boolean status;
    
    @Option(name = "-c", aliases = "--clients", description = "Print server number of clients by services in the server (when server address list/GUID is given).", required = false, multiValued = false)
    boolean clients;    

    @Option(name = "-d", aliases = "--dump", description = "Dumps entire server status (when server address list is given).", required = false, multiValued = false)
    boolean dump;    
    
    @Option(name = "-l", aliases = "--list", description = "List default services in the server (when server address list is given).", required = false, multiValued = false)
    boolean listservices;
                
    @Argument(index = 0, name = "server", description = "Server address or GUID starting with '0x'", required = false, multiValued = true)
    String server = null;     

    @Override
    public Object execute() throws Exception {
        out = session.getConsole();
        MerlotRPCClientImpl pvClient = null;
        Structure topStructure = null;
        String opField = "op";
        String op = "channels";
        
        if (info) op="info";
        if (status) op="status";
        if (clients) op="clients";
        if (dump) op="dump";
        if (listservices) opField ="help";
        
        try {

            topStructure = fieldCreate.createFieldBuilder()
                    .setId("epics:nt/NTURI:1.0")
                    .add("schema", ScalarType.pvString)
                    .add("path", ScalarType.pvString)
                    .add("help", ScalarType.pvString)
                    .addNestedStructure("query")
                        .add(opField, ScalarType.pvString)
                    .endNested()
                    .createStructure();
            
            PVStructure pvQuery = pvDataCreate.createPVStructure(topStructure);
            
            pvQuery.getStringField("schema").put("pva");
            pvQuery.getStringField("path").put("server");
            
            if (opField.equalsIgnoreCase("op"))
            pvQuery.getStringField("query.op").put(op);

            if (server != null){
                String srv = server.replace("[", "");
                srv = srv.replace("]", "");
                pvClient = new MerlotRPCClientImpl(srv,"server");
                PVStructure pvResult = pvClient.request(pvQuery, wait);

                if (pvResult == null) {
                    out.println("Without a response from the server, verify the information provided.");
                } else {
                    out.println(pvResult);
                }
                pvClient.destroy();                

            } else {
                listServers(wait*1000);
            }
        } catch (Throwable ex) {
            out.println("Exception ex: " + ex);
        } finally {
            if (pvClient != null) pvClient.destroy();

        }            

        return null;
    }
   
    
    public void listServers(int delay) throws Throwable {

        final ConfigurationProvider configurationProvider = ConfigurationFactory.getProvider();
        Configuration config = configurationProvider.getConfiguration("pvAccess-client");
        if (config == null)
                config = configurationProvider.getConfiguration("system");

        String addressList = config.getPropertyAsString("EPICS_PVA_ADDR_LIST", "");
        boolean autoAddressList = config.getPropertyAsBoolean("EPICS_PVA_AUTO_ADDR_LIST", true);
        int broadcastPort = config.getPropertyAsInteger("EPICS_PVA_BROADCAST_PORT", PVAConstants.PVA_BROADCAST_PORT);

        // where to send address
        InetSocketAddress[] broadcastAddresses = InetAddressUtil.getBroadcastAddresses(broadcastPort);
        
        // set broadcast address list
        if (addressList != null && addressList.length() > 0)
        {
                // if auto is true, add it to specified list
                InetSocketAddress[] appendList = null;
                if (autoAddressList == true)
                        appendList = broadcastAddresses;

                broadcastAddresses = InetAddressUtil.getSocketAddressList(addressList, broadcastPort, appendList);
        }

        out.println("Searching...");

        DatagramChannel datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(true);
        datagramChannel.socket().setBroadcast(true);
        datagramChannel.socket().setSoTimeout(delay);	// 1 sec
        datagramChannel.bind(new InetSocketAddress(0));


        ByteBuffer sendBuffer = ByteBuffer.allocate(1024);

        sendBuffer.put(PVAConstants.PVA_MAGIC);
        sendBuffer.put(PVAConstants.PVA_VERSION);
        sendBuffer.put((byte)0x80);	// big endian
        sendBuffer.put((byte)0x03);	// search
        sendBuffer.putInt(4+1+3+16+2+1+2);		// payload size

        sendBuffer.putInt(0);	    // sequenceId
        sendBuffer.put((byte)0x81); // reply required // TODO unicast vs multicast; for now we mark ourselves as unicast
        sendBuffer.put((byte)0);		// reserved
        sendBuffer.putShort((short)0);  // reserved

        // NOTE: is it possible (very likely) that address is any local address ::ffff:0.0.0.0
        InetSocketAddress address = (InetSocketAddress)datagramChannel.getLocalAddress();
        InetAddressUtil.encodeAsIPv6Address(sendBuffer, address.getAddress());
        sendBuffer.putShort((short)address.getPort());

        sendBuffer.put((byte)0x00);	// no restriction on protocol
        sendBuffer.putShort((byte)0x00);	// count


        send(datagramChannel, broadcastAddresses, sendBuffer);

        ByteBuffer receiveBuffer = ByteBuffer.allocate(1024);

        DatagramPacket dp = new DatagramPacket(receiveBuffer.array(), receiveBuffer.capacity());
        while (true)
        {
//			SocketAddress responseFrom = datagramChannel.receive(receiveBuffer);
//			if (responseFrom == null)
//				break;

                SocketAddress responseFrom;
                try
                {
                        datagramChannel.socket().receive(dp);
                        responseFrom = dp.getSocketAddress();
                        ((Buffer) receiveBuffer).position(dp.getLength());
                }
                catch (SocketTimeoutException ste) {
                        break;
                }

                ((Buffer) receiveBuffer).flip();
                processSearchResponse((InetSocketAddress)responseFrom, receiveBuffer);
        }

        for (ServerEntry se : serverMap.values())
                System.out.println(se);

        out.println("done.");

    }    
        
    private boolean send(DatagramChannel channel, 
                    InetSocketAddress[] sendAddresses, ByteBuffer buffer) 
    {
        // noop check
        if (sendAddresses == null)
                return false;

        for (int i = 0; i < sendAddresses.length; i++)
        {
                try
                {
                        // prepare buffer
                        //buffer.flip();
                    
                        ((Buffer) buffer).flip();
                        channel.send(buffer, sendAddresses[i]);
                }
                catch (NoRouteToHostException nrthe)
                {
                        out.println("No route to host exception caught when sending to: " + sendAddresses[i] + ".");
                        continue;
                }
                catch (Throwable ex) 
                {
                        ex.printStackTrace();
                        return false;
                }
        }

        return true;
    }   
    
    private final Map<GUID, ServerEntry> serverMap =
            new HashMap<GUID, ServerEntry>();    

    private class ServerEntry {
        GUID guid;
        String protocol;
        // Set removes duplicates
        final Set<InetSocketAddress> addresses = new HashSet<InetSocketAddress>();
        byte version;

        public ServerEntry(GUID guid, String protocol,
                        InetSocketAddress address, byte version) {
                this.guid = guid;
                this.protocol = protocol;
                addresses.add(address);
                this.version = version;
        }

        public void addAddress(InetSocketAddress address)
        {
                addresses.add(address);
        }

        @Override
        public String toString() {
                StringBuffer b = new StringBuffer(200);
                b.append("GUID ").append(guid).append(", version ").append(version).append(": ");
                b.append(protocol).append('@');
                b.append(Arrays.toString(addresses.toArray()));
                return b.toString();
        }

    }    
    
    private final void processSearchResponse(InetSocketAddress responseFrom, ByteBuffer socketBuffer) throws IOException
    {
            // magic code
            final byte magicCode = socketBuffer.get();

            // version
            byte version = socketBuffer.get(); 

            // flags
            byte flags = socketBuffer.get();
            if ((flags & 0x80) == 0x80)
                    socketBuffer.order(ByteOrder.BIG_ENDIAN);
            else
                    socketBuffer.order(ByteOrder.LITTLE_ENDIAN);

            // command
            byte command = socketBuffer.get();
            if (command != 0x04) 
                    return;

            // read payload size
            int payloadSize = socketBuffer.getInt();
            if (payloadSize < (12+4+16+2))
                    return;

            // check magic code
            if (magicCode != PVAConstants.PVA_MAGIC)
                    return;

            // 12-byte GUID
            byte[] guid = new byte[12]; 
            socketBuffer.get(guid);

            /*int searchSequenceId = */socketBuffer.getInt();

            // 128-bit IPv6 address
            byte[] byteAddress = new byte[16]; 
            socketBuffer.get(byteAddress);

            final int port = socketBuffer.getShort() & 0xFFFF;

            // NOTE: Java knows how to compare IPv4/IPv6 :)

            InetAddress addr;
            try {
                    addr = InetAddress.getByAddress(byteAddress);
            } catch (UnknownHostException e) {
                    return;
            }

            // accept given address if explicitly specified by sender
            System.out.println("Dirección: " + responseFrom.getAddress());
            if (!addr.isAnyLocalAddress())
                    responseFrom = new InetSocketAddress(addr, port);
            else
                    responseFrom = new InetSocketAddress(responseFrom.getAddress(), port);

            final String protocol = SerializeHelper.deserializeString(socketBuffer);

            /*boolean found = */socketBuffer.get(); // != 0;

            GUID g = new GUID(guid);
            //System.out.println("GUID: " + g);
            ServerEntry se = serverMap.get(g);
            if (se != null)
                    se.addAddress(responseFrom);
            else
                    serverMap.put(g, new ServerEntry(g, protocol, responseFrom, version));
    }
   
    
}
