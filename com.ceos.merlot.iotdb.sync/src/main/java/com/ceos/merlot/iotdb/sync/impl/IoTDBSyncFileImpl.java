/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.sync.impl;

import com.ceos.merlot.iotdb.sync.api.IoTDBSyncFile;
import com.ceos.merlot.scheduler.api.Job;
import com.ceos.merlot.scheduler.api.JobContext;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.iotdb.db.sync.conf.SyncSenderConfig;
import org.apache.iotdb.db.sync.conf.SyncSenderDescriptor;
import org.apache.iotdb.db.sync.sender.manage.SyncFileManager;
import org.apache.iotdb.db.sync.sender.transfer.SyncClient;
import org.osgi.framework.BundleContext;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class IoTDBSyncFileImpl implements IoTDBSyncFile, Job {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IoTDBSyncFileImpl.class);   
    
    private static SyncSenderConfig config = null;
    private SyncClient syncli = null;

    private BundleContext bundleContext;
    private boolean started = false;
    
    
    public IoTDBSyncFileImpl(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }
    
    @Override
    public void init() {       
        config = SyncSenderDescriptor.getInstance().getConfig();
        syncli = SyncClient.getInstance();
    }

    //TODO: Remove lock file
    @Override
    public void destroy() {
        syncli.stop();
    }    
    
    @Override
    public void start() {
        try {
            //TODO: Replace verifySingleton with Karaf way lock. System.exit(1)!!!!
            //verifySingleton();
            syncli.startMonitor();
            syncli.startTimedTask();
        } catch (Exception ex) {
            ex.printStackTrace();
            LOGGER.info(ex.getMessage());
        }
    }

    @Override
    public void stop() {
        syncli.stop();
    }    
    
    @Override
    public void execute(JobContext context) {
        try {
            if (started){
                syncli.syncAll();
            };
        } catch (Exception ex) {
            
        }
    }


  /**
   * Try to lock lockfile. if failed, it means that sync client has benn started.
   *
   * @param lockFile path of lock file
   */
  private boolean lockInstance(final String lockFile) {
    try {
      final File file = new File(lockFile);
      final RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
      final FileLock fileLock = randomAccessFile.getChannel().tryLock();
      if (fileLock != null) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
          try {
            fileLock.release();
            randomAccessFile.close();
          } catch (Exception e) {
            LOGGER.error("Unable to remove lock file: {}", lockFile, e);
          }
        }));
        return true;
      }
    } catch (Exception e) {
      LOGGER.error("Unable to create and/or lock file: {}", lockFile, e);
    }
    return false;
  }    

  //TODO: Use Karaf lock system.
  private void verifySingleton() throws IOException {
    String[] dataDirs = IoTDBDescriptor.getInstance().getConfig().getDataDirs();
    for (String dataDir : dataDirs) {
      config.update(dataDir);
      /*
      File lockFile = new File(config.getLockFilePath());
      if (!lockFile.getParentFile().exists()) {
        lockFile.getParentFile().mkdirs();
      }
      if (!lockFile.exists()) {
        lockFile.createNewFile();
      }

      if (!lockInstance(config.getLockFilePath())) {
        LOGGER.error("Sync client is already running.");
      }
      */
    }
  }    
    
}
