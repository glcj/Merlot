/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.db.api;

import org.epics.pvaccess.server.rpc.RPCService;
import org.epics.pvdatabase.PVRecord;
import org.osgi.framework.ServiceReference;

/**
 *
 * @author cgarcia
 */
public interface DBRPCReferenceListener {

    public void bind(ServiceReference reference);

    public void bind(RPCService service);

    public void unbind(ServiceReference reference);
    
}
