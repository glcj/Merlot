/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.api;

import org.epics.gpclient.PVReader;
import org.epics.vtype.VType;

/**
 *
 * @author cgarcia
 */
public interface HtcEvent {
    
    public long getSequence();
    public void setSequence(long Sequence);
    
    public int getTransactionID();
    public void setTransactionID(int TransactionID);
    
    public String getTag();
    public void setTag(String tag);
    
    public PVReader<VType> getPvReader();
    public void setPvReader(PVReader<VType> pvr);

    public VType getValue();
    public void setValue(VType value);
    
    
    
}
