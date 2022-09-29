/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.model.core;

import com.ceos.merlot.model.impl.ModelImpl;
import java.util.Dictionary;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class ModelManagedService implements ManagedService  {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelManagedService.class);
    
    @Override
    public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
        LOGGER.info("Model update.");
    }
    
}
