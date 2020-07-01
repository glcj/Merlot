/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.sync.command;

import org.apache.iotdb.cli.Cli;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
@Command(scope = "iotdb", name = "client", description = "Synchor.")
@Service
public class IoTDBClientCommand  implements Action {
    @Reference
    BundleContext bundleContext;
  
        /*  
    @Reference
    IoTDBSyncFile service;
  */
    @Option(name = "-h", aliases = "--host", description = "IP address of the IoTDB Server.", required = false, multiValued = false)
    String host = null;

    @Option(name = "-p", aliases = "--port", description = "Port address of the IoTDB Server.", required = false, multiValued = false)
    String port = null;  
    
    @Option(name = "-u", aliases = "--user", description = "User name registered in the IoTDB Server.", required = false, multiValued = false)
    String user = null;  
    
    @Option(name = "-pw", aliases = "--password", description = "Password of the user registered in the IoTDB Server.", required = false, multiValued = false)
    String pass = null;      
    
    /*
    @Argument(index = 0, name = "uid", description = "The device unit identifier.", required = true, multiValued = false)
    int uid;
    */
    
    @Override
    public Object execute() throws Exception {
        if ((host != null) && (port != null) && (user != null) && (pass != null)) {
            String args[] = {"-h",host,"-p",port,"-u",user,"-pw",pass};
            Cli.main(args);
        }
        return null;
    }
    
}
