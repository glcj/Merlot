/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.model.command;

import com.ceos.merlot.model.api.Model;
import java.util.UUID;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
@Command(scope = "model", name = "domine", description = "Command for test tree.")
@Service
public class ModelDomineCommand implements Action {

    @Reference
    BundleContext bundleContext;
    
    @Reference
    Model model;        
    
    @Argument(index = 0, name = "id", description = "Node id in the model.", required = true, multiValued = false)
    String id;   
    
    @Option(name = "-u", aliases = "--uuid", description = "The id represent UUID of the node in the model.", required = false, multiValued = false)
    Boolean isuuid = false;        
    
    /*
    @Option(name = "-r", aliases = "--reload", description = "Reload model tree from config.", required = false, multiValued = false)
    Boolean reload = false;      
    
    @Argument(index = 0, name = "uid", description = "The device unit identifier.", required = true, multiValued = false)
    int uid;
    */       
    
    @Override
    public Object execute() throws Exception {
        if (isuuid){
            UUID uuid = UUID.fromString(id);
            System.out.println(model.getDomain(uuid));                        
        } else {
            System.out.println(model.getDomain(id));
        };
        return null;
    }
    
}
