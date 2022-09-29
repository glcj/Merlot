/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.model.command;

import com.ceos.merlot.model.api.Model;
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
@Command(scope = "model", name = "tree", description = "Command for test tree.")
@Service
public class ModelTreeCommand implements Action {

    @Reference
    BundleContext bundleContext;
    
    @Reference
    Model model;
    
    @Argument(index = 0, name = "uuid", description = "Node uuid in the model.", required = false, multiValued = false)
    String uuid;        
    
    @Option(name = "-p", aliases = "--persist", description = "Persist the model tree.", required = false, multiValued = false)
    Boolean persist = false;  

    @Option(name = "-r", aliases = "--reload", description = "Reload model tree from config.", required = false, multiValued = false)
    Boolean reload = false;      
    
    /*
    @Argument(index = 0, name = "uid", description = "The device unit identifier.", required = true, multiValued = false)
    int uid;
    */    
    
    @Override
    public Object execute() throws Exception {
        if (persist) {
            model.persist();
        } else if (reload){
            model.reload();
        }else {
            printTree();
        }
        return null;
    }
    
    private void printTree(){
        StringBuilder sb = new StringBuilder();
        model.printTree(sb);
        System.out.println(sb.toString());        
    }
    
}
