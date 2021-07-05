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

package com.ceos.merlot.iotdb.svr.command;

import com.ceos.merlot.iotdb.svr.api.IoTDBServer;
import org.apache.iotdb.db.conf.IoTDBConfig;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.iotdb.tsfile.common.conf.TSFileConfig;
import org.apache.iotdb.tsfile.common.conf.TSFileDescriptor;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.ShellTable;
import org.osgi.framework.BundleContext;

@Command(scope = "iotdb", name = "server", description = "Synchor.")
@Service
public class IoTDBServerAdminCommand implements Action {

    private IoTDBConfig confIoTDB = IoTDBDescriptor.getInstance().getConfig();
    
    @Reference
    BundleContext bundleContext;
    
    @Reference
    IoTDBServer service;
    
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
        

        table.addRow().addContent("metrics_port",confIoTDB.getMetricsPort()); //=8181

        //RPC Configuration
        table.addRow().addContent("rpc_address", confIoTDB.getRpcAddress()); //=0.0.0.0
        table.addRow().addContent("rpc_port", confIoTDB.getRpcPort()); //=6667
        table.addRow().addContent("rpc_thrift_compression_enable", confIoTDB.isRpcThriftCompressionEnable()); //=false
        table.addRow().addContent("rpc_max_concurrent_client_num", confIoTDB.getRpcMaxConcurrentClientNum()); //=65535

        //Dynamic Parameter Adapter Configuration
        //table.addRow().addContent("enable_parameter_adapter", confIoTDB.isEnableParameterAdapter()); //=true

        //Write Ahead Log Configuration
        table.addRow().addContent("enable_wal", confIoTDB.isEnableWal()); //=true
        table.addRow().addContent("flush_wal_threshold", confIoTDB.getFlushWalThreshold()); //=10000
        table.addRow().addContent("force_wal_period_in_ms", confIoTDB.getForceWalPeriodInMs()); //=10

        //Timestamp Precision Configuration
        table.addRow().addContent("timestamp_precision", confIoTDB.getTimestampPrecision()); //=ms

        //Directory Configuration
        //data dirs
        //table.addRow().addContent("base_dir", confIoTDB.getBaseDir()); //=data

        table.addRow().addContent("data_dirs", String.join(",",confIoTDB.getDataDirs())); //=data\\data

        //mult_dir_strategy
        table.addRow().addContent("multi_dir_strategy", confIoTDB.getMultiDirStrategyClassName()); //=MaxDiskUsableSpaceFirstStrategy

        //wal dir
        //table.addRow().addContent("wal_dir", confIoTDB.getWalFolder()); //=data\\wal
        
        /*
        table.addRow().addContent("tsfile_storage_fs", confIoTDB.getTsFileStorageFs()); //=LOCAL
        table.addRow().addContent("core_site_path", confIoTDB.getCoreSitePath()); //=/etc/hadoop/conf/core-site.xml
        table.addRow().addContent("hdfs_site_path", confIoTDB.getHdfsSitePath()); //=/etc/hadoop/conf/hdfs-site.xml
        table.addRow().addContent("hdfs_ip", String.join(",",confIoTDB.getHdfsIp())); //=localhost
        table.addRow().addContent("hdfs_port", confIoTDB.getHdfsPort()); //=9000
        table.addRow().addContent("dfs_nameservices", confIoTDB.getDfsNameServices()); //=hdfsnamespace
        table.addRow().addContent("dfs_ha_namenodes", String.join(",",confIoTDB.getDfsHaNamenodes())); //=nn1,nn2
        table.addRow().addContent("dfs_ha_automatic_failover_enabled", confIoTDB.isDfsHaAutomaticFailoverEnabled()); //=true
        table.addRow().addContent("dfs_client_failover_proxy_provider", confIoTDB.getDfsClientFailoverProxyProvider()); //=org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider
        table.addRow().addContent("hdfs_use_kerberos", confIoTDB.isUseKerberos()); //=false
        table.addRow().addContent("kerberos_keytab_file_path", confIoTDB.getKerberosKeytabFilePath()); //=/path
        table.addRow().addContent("kerberos_principal", confIoTDB.getKerberosPrincipal()); //=your principal
        */
        
        //Memory Control Configuration
        table.addRow().addContent("write_read_free_memory_proportion", "***"); //=6:3:1
        /*
        table.addRow().addContent("fetch_size", confIoTDB.getFetchSize()); //=10000
        */
        table.addRow().addContent("wal_buffer_size", confIoTDB.getWalBufferSize()); //=16777216
        //table.addRow().addContent("time_zone", confIoTDB.getZoneID()); //=+08:00
        table.addRow().addContent("tsfile_size_threshold", confIoTDB.getTsFileSizeThreshold()); //=536870912
        table.addRow().addContent("memtable_size_threshold", confIoTDB.getMemtableSizeThreshold()); //=134217728
        table.addRow().addContent("concurrent_flush_thread", confIoTDB.getConcurrentFlushThread()); //=0
        table.addRow().addContent("chunk_buffer_pool_enable", confIoTDB.isChunkBufferPoolEnable()); //=false
        table.addRow().addContent("default_ttl", confIoTDB.getDefaultTTL()); //=36000000

        //Upgrade Configurations
        table.addRow().addContent("upgrade_thread_num", confIoTDB.getUpgradeThreadNum()); //=1

        //Merge Configurations
        table.addRow().addContent("merge_thread_num", confIoTDB.getMergeThreadNum()); //=1
        table.addRow().addContent("merge_chunk_subthread_num", confIoTDB.getMergeChunkSubThreadNum()); //=4
        table.addRow().addContent("merge_fileSelection_time_budget", confIoTDB.getMergeFileSelectionTimeBudget()); //=30000
        table.addRow().addContent("merge_memory_budget", confIoTDB.getMergeMemoryBudget()); //=2147483648
        table.addRow().addContent("continue_merge_after_reboot", confIoTDB.isContinueMergeAfterReboot()); //=false
        table.addRow().addContent("merge_interval_sec", confIoTDB.getMergeIntervalSec()); //=3600
        table.addRow().addContent("force_full_merge", confIoTDB.isForceFullMerge()); //=false
        //table.addRow().addContent("chunk_merge_point_threshold", confIoTDB.getChunkMergePointThreshold()); //=20480

        //Metadata Cache Configuration
        table.addRow().addContent("meta_data_cache_enable", confIoTDB.isMetaDataCacheEnable()); //=true
        table.addRow().addContent("filemeta_chunkmeta_free_memory_proportion", "***"); //=3:6:10

        //Statistics Monitor configuration
        table.addRow().addContent("enable_stat_monitor", confIoTDB.isEnableStatMonitor()); //=false
        //table.addRow().addContent("back_loop_period_in_second", confIoTDB.getBackLoopPeriodSec()); //=5
        //table.addRow().addContent("stat_monitor_detect_freq_in_second", confIoTDB.getStatMonitorDetectFreqSec()); //=600
        //table.addRow().addContent("stat_monitor_retain_interval_in_second", confIoTDB.getStatMonitorRetainIntervalSec()); //=600
        table.addRow().addContent("schema_manager_cache_size", confIoTDB.getmManagerCacheSize()); //=300000

        //External sort Configuration
        table.addRow().addContent("enable_external_sort", confIoTDB.isEnableExternalSort()); //=true
        table.addRow().addContent("external_sort_threshold", confIoTDB.getExternalSortThreshold()); // = 60

        //Sync Server Configuration
        table.addRow().addContent("is_sync_enable", confIoTDB.isSyncEnable()); //=true
        table.addRow().addContent("sync_server_port", confIoTDB.getSyncServerPort()); //=5555
        table.addRow().addContent("ip_white_list", confIoTDB.getIpWhiteList()); //=0.0.0.0/0

        //performance statistic configuration
        table.addRow().addContent("enable_performance_stat", confIoTDB.isEnablePerformanceStat()); //=false
        table.addRow().addContent("performance_stat_display_interval", confIoTDB.getPerformanceStatDisplayInterval()); //=60000
        table.addRow().addContent("performance_stat_memory_in_kb", confIoTDB.getPerformanceStatMemoryInKB()); //=20

        //Configurations for watermark module
        table.addRow().addContent("watermark_module_opened", confIoTDB.isEnableWatermark()); //=false
        table.addRow().addContent("watermark_secret_key", confIoTDB.getWatermarkSecretKey()); //=IoTDB*2019@Beijing
        table.addRow().addContent("watermark_bit_string", confIoTDB.getWatermarkBitString()); //=100101110100
        /*
        table.addRow().addContent("watermark_method", confIoTDB.getWatermarkMethod()); //=GroupBasedLSBMethod(embed_row_cycle=2,embed_lsb_num=5)
        */

        //Configurations for creating schema automatically
        table.addRow().addContent("enable_auto_create_schema", confIoTDB.isAutoCreateSchemaEnabled()); //=true
        table.addRow().addContent("default_storage_group_level", confIoTDB.getDefaultStorageGroupLevel()); //=2
        table.addRow().addContent("default_boolean_encoding", confIoTDB.getDefaultBooleanEncoding()); //=RLE
        table.addRow().addContent("default_int32_encoding", confIoTDB.getDefaultInt32Encoding()); //=RLE
        table.addRow().addContent("default_int64_encoding", confIoTDB.getDefaultInt64Encoding()); //=RLE
        table.addRow().addContent("default_float_encoding", confIoTDB.getDefaultFloatEncoding()); //=GORILLA
        table.addRow().addContent("default_double_encoding", confIoTDB.getDefaultDoubleEncoding()); //=GORILLA
        table.addRow().addContent("default_text_encoding", confIoTDB.getDefaultTextEncoding()); //=PLAIN

        TSFileConfig confTSF = TSFileDescriptor.getInstance().getConfig();
        
        //Configurations for tsfile-format
        table.addRow().addContent("group_size_in_byte", confTSF.getGroupSizeInByte()); //=134217728
        table.addRow().addContent("page_size_in_byte", confTSF.getPageSizeInByte()); //=65536
        table.addRow().addContent("max_number_of_points_in_page", confTSF.getMaxNumberOfPointsInPage()); //=1048576
        table.addRow().addContent("time_series_data_type", confTSF.getTimeSeriesDataType()); //=INT64
        table.addRow().addContent("max_string_length", confTSF.getMaxStringLength()); //=128
        table.addRow().addContent("float_precision", confTSF.getFloatPrecision()); //=2
        table.addRow().addContent("time_encoder", confTSF.getTimeEncoder()); //=TS_2DIFF
        table.addRow().addContent("value_encoder", confTSF.getValueEncoder()); //=PLAIN
        table.addRow().addContent("compressor", confTSF.getCompressor()); //=SNAPPY
        table.addRow().addContent("bloom_filter_error_rate", confTSF.getBloomFilterErrorRate()); //=0.05
       
        table.print(System.out);
    }
    

}
