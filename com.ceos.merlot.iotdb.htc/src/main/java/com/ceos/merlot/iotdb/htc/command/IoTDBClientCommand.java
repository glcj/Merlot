/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.htc.command;

import org.apache.iotdb.client.WinClient;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
@Command(scope = "iotdb", name = "client_x", description = "Synchor.")
@Service
public class IoTDBClientCommand  implements Action {
    @Reference
    BundleContext bundleContext;
  
        /*  
    @Reference
    IoTDBSyncFile service;
  
    @Option(name = "-s", aliases = "--start", description = "Start sync service.", required = false, multiValued = false)
    boolean start = false;

    @Option(name = "-k", aliases = "--kill", description = "Stop sync service.", required = false, multiValued = false)
    boolean stop = false;  
    
    @Option(name = "-c", aliases = "--conf", description = "Print configuration of  sync service.", required = false, multiValued = false)
    boolean conf = false;    
    

    @Argument(index = 0, name = "uid", description = "The device unit identifier.", required = true, multiValued = false)
    int uid;
    */
    @Override
    public Object execute() throws Exception {
        String args[] = {"-h","127.0.0.1","-p","6667","-u","root","-pw","root"};
        WinClient.main(args);
        return null;
    }
    
}
