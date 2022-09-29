/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.htc.impl;

import com.ceos.merlot.htc.api.HtcConsumer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ceos.merlot.htc.api.HtcEvent;
import com.ceos.merlot.htc.api.HtcService;
import com.ceos.merlot.model.api.Model;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import org.apache.iotdb.rpc.BatchExecutionException;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.Session;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.apache.karaf.config.core.ConfigRepository;
import org.epics.gpclient.PVReader;
import org.epics.vtype.Scalar;
import org.epics.vtype.VBoolean;
import org.epics.vtype.VDouble;
import org.epics.vtype.VInt;
import org.epics.vtype.VLong;
import org.epics.vtype.VNumber;
import org.epics.vtype.VString;
import org.epics.vtype.VType;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;

/**
 *
 * @author cgarcia
 */
public class HtcServerServiceImpl implements HtcService, HtcConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtcServerServiceImpl.class);   
    private static final String CONFIG_PID = "com.ceos.merlot.htc.server";
    private final BundleContext bundleContext;
    private final ConfigRepository configRepository;
    private final Model s88model;
    private EventAdmin eventAdmin = null;
    
    //Next Disruptor Services
    private final HtcService tsfileservice;
    
    // Executor that will be used to construct new threads for consumers
    protected Executor RequestExecutor;

    //Disruptor 
    protected Disruptor<HtcEvent> RequestDisruptor; 
    
    //Ring Buffer    
    protected RingBuffer<HtcEvent> RequestRingBuffer; 
    
    //IoTDB Session
    private Session session;
    private boolean connected = false;
    
    // Specify the size of the ring buffer, must be power of 2.
    private int bufferSize = 1024;  
    private boolean started;
    private long starttime = 0;
    private long elapsedtime = 0;
    private long waittime = 60; //Default every 300 seconds
    private boolean push = false;
            
    //Simple data collection
    private List<String> deviceIds;
    private List<Long> times;
    private List<List<String>> measurementsList;
    private List<List<TSDataType>> typesList;
    private List<List<VType>> valuesList;
    private List<PVReader<VType>> pvreaders;

    public HtcServerServiceImpl(BundleContext bundleContext, 
            ConfigRepository configRepository,
            HtcService tsfileservice,
            Model s88model) {
        this.bundleContext = bundleContext;
        this.configRepository = configRepository;
        this.deviceIds = new ArrayList(1024);  
        this.times = new ArrayList(1024); 
        this.measurementsList = new ArrayList(1024);
        this.typesList =  new ArrayList(1024);
        this.valuesList = new ArrayList(1024);
        this.pvreaders = new ArrayList(1024);
        this.tsfileservice = tsfileservice;
        this.s88model = s88model;
    }
     
    @Override
    public void init() {
        ServiceReference ref = bundleContext.getServiceReference(EventAdmin.class.getName());
        if (ref != null){
            eventAdmin = (EventAdmin) bundleContext.getService(ref);
        }
                
        RequestExecutor  = Executors.newCachedThreadPool();
        
        // Construct the Disruptor
        RequestDisruptor = new Disruptor<HtcEvent>(HtcEventImpl.FACTORY, 1024, DaemonThreadFactory.INSTANCE,
                    ProducerType.MULTI, new BlockingWaitStrategy());        

        RequestRingBuffer = RequestDisruptor.getRingBuffer(); 
        
        RequestDisruptor.handleEventsWith(this);          
        RequestDisruptor.start();           
                
        start();
    }

    @Override
    public void destroy() {     
        stop();
        RequestDisruptor.shutdown();
    }

    @Override
    public void start() {
        if (!started){  
            try {
                Dictionary<String, Object> dict = configRepository
                        .getConfigAdmin()
                        .getConfiguration(CONFIG_PID)
                        .getProperties();
                if (dict != null){  
                    String host = (String) dict.get("session_remote_host"); 
                    String port = (String) dict.get("session_remote_host_port"); 
                    String username = (String) dict.get("session_remote_user"); 
                    String password = (String) dict.get("session_remote_password");
                    LOGGER.info("IoTDB Session to {}:{} with User: {} and Password: **** .",host,port,username);                    
                    session = new Session(host, port, username, password);
                    if (session!=null) session.open();                 
                    LOGGER.info("Connected to iotdb host.");
                    starttime = Instant.now().getEpochSecond();
                    connected = true;
                } else {
                    LOGGER.info("Check iotdb server information.");
                    started = false;
                    return;
                };
            } catch (Exception ex) {
                started = false;                
                LOGGER.info(ex.getMessage());
                return;
            }                        
        };
        started = true;
    }

    @Override
    public void stop() {       
        //RequestDisruptor.halt();
        started = false;
    }

    @Override
    public boolean isStarted() {
        return started;
    }
        
    
    @Override
    public HtcEvent getEvent() {
        if (!started) return null;
        long sequence = RequestRingBuffer.next();  // Grab the next sequence
    	HtcEvent de = RequestRingBuffer.get(sequence); // Get the entry in the Disruptor  
    	de.setSequence(sequence);
	return de;
    }
    
    @Override
    public void onEvent(Object event, long sequence, boolean endOfBatch) throws Exception {
        elapsedtime = (Instant.now().getEpochSecond() - starttime); 
        push = (elapsedtime > waittime);
        
        if (event == null){
            return;
        }
        
        try {
            HtcEvent myEvent = (HtcEvent) event;        
            
            TSDataType tstype = TSDataType.DOUBLE;
            Object objValue = myEvent.getValue();
            
            if (objValue instanceof VInt) {
                tstype = TSDataType.INT32;
            } else if (objValue instanceof VLong) {
                tstype = TSDataType.INT64;
            } else if (objValue instanceof VBoolean) {
                tstype = TSDataType.BOOLEAN;
            } else if (objValue instanceof VDouble) {
                tstype = TSDataType.DOUBLE;
            } else if (objValue instanceof VString) {
                tstype = TSDataType.TEXT;
            }
                                   
            addItem(myEvent.getTag(), 
                    ((Scalar) objValue).getTime().getTimestamp().toEpochMilli(), 
                    Collections.singletonList("value"),
                    Collections.singletonList(tstype),
                    Collections.singletonList(myEvent.getValue()),
                    myEvent.getPvReader());

            if (connected && (push || (deviceIds.size() > 1020))) {

                if (!SendAllItemsToServer()){              
                    SendAllItemsToTsFile();
                }
                starttime = Instant.now().getEpochSecond();
            } else if ((push || (deviceIds.size() > 1020))){
                session.open(true);
                connected = true;
                if (!SendAllItemsToServer()){
                    SendAllItemsToTsFile();
                }  
                starttime = Instant.now().getEpochSecond();            
            }
            myEvent.setTag(null);
            myEvent.setPvReader(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }    

    @Override
    public void putEvent(HtcEvent event) {
        RequestRingBuffer.publish(event.getSequence());
    }

    @Override
    public void setMaxWaitTime(long seconds) {
        this.waittime = seconds;
    }

    @Override
    public long getMaxWaitTime() {
        return this.waittime;
    }
    
    @Override
    public long getSamplesWaiting() {
        return this.deviceIds.size();
    };    

    @Override
    public long getRequestQueueSize() {
        return RequestRingBuffer.getBufferSize();
    }

    @Override
    public long getRequestQueueItems() {
        return (RequestRingBuffer.getBufferSize() - RequestRingBuffer.remainingCapacity());
    }    

    private void addItem(String deviceId, long time, List measurements, List types, List values,PVReader pvreader ){
        if (time == 0) time = System.currentTimeMillis();              
        deviceIds.add(s88model.getDomain(deviceId)+"."+deviceId);
        times.add(time);
        measurementsList.add(measurements);
        typesList.add(types);
        valuesList.add(values);
        pvreaders.add(pvreader);
    }
    
    private boolean SendAllItemsToServer(){
        try {
            List<List<Object>> objectsList = new ArrayList(valuesList.size());
            for(List<VType> values:valuesList){             
                objectsList.add(Collections.singletonList(((Scalar) values.get(0)).getValue()));
            }
            
            session.insertRecords(deviceIds, times, measurementsList, typesList, objectsList);
        }catch (IoTDBConnectionException ex){
            LOGGER.info(ex.toString());
            connected = false;
            return false;
        } catch (StatementExecutionException ex) {
            LOGGER.info(ex.toString());
            return false;
        }
        ClearAllItemList();        
        return true;
    }
    
    private void SendAllItemsToTsFile(){
        for (int i=0; i < deviceIds.size(); i++){
            HtcEvent event = tsfileservice.getEvent();            
            if (event != null){
                event.setTag(deviceIds.get(i));                
                event.setPvReader(pvreaders.get(i));
                event.setValue(valuesList.get(i).get(0));
                tsfileservice.putEvent(event);
            }
        }
        ClearAllItemList();
    }    
    
    private void ClearAllItemList(){
        deviceIds.clear();
        times.clear();
        measurementsList.clear();
        typesList.clear();
        valuesList.clear();
        pvreaders.clear();
    }
    
    
    
    
}
