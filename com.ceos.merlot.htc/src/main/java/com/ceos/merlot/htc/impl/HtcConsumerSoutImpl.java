/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

import com.ceos.merlot.htc.api.HtcConsumer;
import com.ceos.merlot.htc.api.HtcEvent;

/**
 *
 * @author cgarcia
 */
public class HtcConsumerSoutImpl implements HtcConsumer {

    private boolean started = false;
    
    
    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void start() {    
        started = true;
    }

    @Override
    public void stop() {
        started = false;
    }

    @Override
    public void onEvent(Object event, long sequence, boolean endOfBatch) throws Exception {
        //if (!started) return;
        HtcEvent myEvent = (HtcEvent) event;
        System.out.println("Event: " + event + " : " + myEvent.getTag() + " : " + myEvent.getPvReader().getValue());
        myEvent.setPvReader(null);
    }
    
}
