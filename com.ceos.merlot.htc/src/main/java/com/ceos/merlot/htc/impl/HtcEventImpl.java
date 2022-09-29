/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

import com.lmax.disruptor.EventFactory;
import com.ceos.merlot.htc.api.HtcEvent;
import org.epics.gpclient.PVReader;
import org.epics.vtype.VType;

/**
 *
 * @author cgarcia
 */
public class HtcEventImpl implements HtcEvent {
    
    private long Sequence = 0;
    private int TransactionID = 0;
    
    private String tag;
    private PVReader<VType> pvr;
    private VType value;
    
    public static final EventFactory<HtcEvent> FACTORY = new EventFactory<HtcEvent>() {
        @Override
        public HtcEvent newInstance()
        {
            return new HtcEventImpl();
        }
    };       

    @Override
    public long getSequence() {
        return Sequence;
    }

    @Override
    public void setSequence(long Sequence) {
        this.Sequence = Sequence;
    }

    @Override
    public int getTransactionID() {
        return TransactionID;
    }

    @Override
    public void setTransactionID(int TransactionID) {
        this.TransactionID = TransactionID;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public PVReader<VType> getPvReader() {
        return pvr;
    }

    @Override
    public void setPvReader(PVReader<VType> pvr) {
        this.pvr = pvr;
    }

    @Override
    public VType getValue() {
        return this.value;
    }

    @Override
    public void setValue(VType value) {
        this.value = value;
    }
    
    
    
    
    
}
