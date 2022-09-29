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
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
@Command(scope = "model", name = "add", description = "Command for test tree.")
@Service
public class ModelAddCommand implements Action {
    
    @Reference
    BundleContext bundleContext;
    
    @Reference
    Model model;
    
    @Argument(index = 0, name = "uuid", description = "Node uuid in the model.", required = true, multiValued = false)
    String uuid;      
    
    @Override
    public Object execute() throws Exception {
        UUID uuid = UUID.randomUUID();
        model.putSite(uuid, "01", "Primera planta");
        
        UUID areauuid = UUID.randomUUID();
        model.putArea(uuid, areauuid, "0102", "Esta es una descripcion");
        
        areauuid = UUID.randomUUID();
        model.putArea(uuid, areauuid, "0102", "Esta es una descripcion"); 
        
        for (int i= 0; i<10 ; i++){
            uuid = UUID.randomUUID();
            model.putCell(areauuid, uuid, Integer.toString(i), "Descripcion: " + i);
        }
        
        return null;
        
        
    }
    
}
