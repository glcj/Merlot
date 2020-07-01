/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.command;

import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import com.ceos.merlot.htc.api.HtcEvent;
import com.ceos.merlot.htc.api.HtcService;

/**
 *
 * @author cgarcia
 */
@Command(scope = "iotdb", name = "testhtc", description = "Command for test.")
@Service
public class HtcTest implements Action {

    @Reference
    HtcService htc;
    
    @Override
    public Object execute() throws Exception {
        htc.start();
        for (int i = 1000; i < 1010; i++){
            HtcEvent event = htc.getEvent();
            event.setTransactionID(i*2);
            htc.putEvent(event);            
        }
        return null;
    }
    
}
