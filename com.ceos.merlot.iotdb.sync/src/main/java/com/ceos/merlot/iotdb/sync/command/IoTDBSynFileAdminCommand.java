/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package com.ceos.merlot.iotdb.sync.command;

import com.ceos.merlot.iotdb.sync.api.IoTDBSyncFile;
import org.apache.iotdb.db.sync.conf.SyncSenderConfig;
import org.apache.iotdb.db.sync.conf.SyncSenderDescriptor;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.ShellTable;
import org.osgi.framework.BundleContext;

@Command(scope = "iotdb", name = "sync", description = "Synchor.")
@Service
public class IoTDBSynFileAdminCommand implements Action {

    @Reference
    BundleContext bundleContext;
    
    @Reference
    IoTDBSyncFile service;
    
    @Option(name = "-s", aliases = "--start", description = "Start sync service.", required = false, multiValued = false)
    boolean start = false;

    @Option(name = "-k", aliases = "--kill", description = "Stop sync service.", required = false, multiValued = false)
    boolean stop = false;  
    
    @Option(name = "-c", aliases = "--conf", description = "Print configuration of  sync service.", required = false, multiValued = false)
    boolean conf = false;    
    
    /*
    @Argument(index = 0, name = "uid", description = "The device unit identifier.", required = true, multiValued = false)
    int uid;
    */
    
    @Override
    public Object execute() throws Exception {
            if (stop) {
                service.stop();
            } else if (start){
                service.start();
            }  else if (conf) {
                printConfig();
            }
            return null;
    }
    
    private void printConfig(){
        ShellTable table = new ShellTable();
        table.column("propertie");
        table.column("value");            
        
        SyncSenderConfig config = SyncSenderDescriptor.getInstance().getConfig();
        
        table.addRow().addContent("serverIp", config.getServerIp());
        table.addRow().addContent("serverPort", config.getServerPort());
        table.addRow().addContent("syncPeriodInSecond", config.getSyncPeriodInSecond());
        table.addRow().addContent("senderFolderPath", config.getSenderFolderPath());
        table.addRow().addContent("lockFilePath", config.getLockFilePath());
        table.addRow().addContent("uuidPath", config.getUuidPath());
        table.addRow().addContent("lastFileInfoPath", config.getLastFileInfoPath());
        table.addRow().addContent("snapshotPath", config.getSnapshotPath());        
        table.addRow().addContent("maxNumOfSyncFileRetry", config.getMaxNumOfSyncFileRetry());         
        table.addRow().addContent("storageGroupList", config.getStorageGroupList());  
        
        table.print(System.out);
    }

}
