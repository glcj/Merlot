/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.db.impl;

import com.ceos.merlot.db.api.DBRPCReferenceListener;
import org.epics.pvaccess.server.rpc.RPCService;
import org.epics.pvdatabase.PVDatabase;
import org.epics.pvdatabase.PVRecord;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class DBRPCReferenceListenerImpl implements DBRPCReferenceListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBRPCReferenceListenerImpl.class); 
    private final BundleContext bundleContext;
    private final PVDatabase master;

    public DBRPCReferenceListenerImpl(BundleContext bundleContext,
            PVDatabase master) {
        this.bundleContext = bundleContext;
        this.master = master;
    }
    
    
    @Override
    public void bind(ServiceReference reference) {
        try {
            if (reference == null) return;
            PVRecord rpcservice = (PVRecord) bundleContext.getService(reference);
            boolean isRecordAdd = master.addRecord(rpcservice);
            if (isRecordAdd){
                LOGGER.info("Service {} was Add.",reference.getProperty("rpcservice.id"));
            } else  {
                LOGGER.info("Service {} can't be Add.",reference.getProperty("rpcservice.id"));            
            }
        } catch (Exception ex){
            LOGGER.info(ex.getMessage());
        }        
    }

    //TODO: Check for compatible type
    @Override
    public void bind(RPCService service) {
        try {
            PVRecord record = (PVRecord) service;
                   
            boolean isRecordAdd = master.addRecord(record);
            if (isRecordAdd){
                LOGGER.info("Service {} was add.");
            } else  {
                LOGGER.info("Service {} can't be add.");            
            }
        } catch (Exception ex){
            LOGGER.info(ex.getMessage());
        }
    }

    @Override
    public void unbind(ServiceReference reference) {
        try {
            if (reference == null) return;
            PVRecord rpcservice = (PVRecord) bundleContext.getService(reference);
            boolean isRecordRemove = master.removeRecord(rpcservice);
            if (isRecordRemove){
                LOGGER.info("Service {} was remove.",reference.getProperty("rpcservice.id"));
            } else  {
                LOGGER.info("Service {} can't be remove.",reference.getProperty("rpcservice.id"));            
            }
        } catch (Exception ex){
            LOGGER.info(ex.getMessage());
        }        
    }
    
}
