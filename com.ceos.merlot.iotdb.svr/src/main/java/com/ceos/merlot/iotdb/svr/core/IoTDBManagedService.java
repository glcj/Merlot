/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.svr.core;

import java.util.Dictionary;
import java.util.Enumeration;
import org.apache.iotdb.db.conf.IoTDBConfig;
import org.apache.iotdb.db.conf.IoTDBConstant;
import org.apache.iotdb.db.conf.IoTDBDescriptor;
import org.apache.karaf.main.ConfigProperties;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 *
 * @author cgarcia
 */
public class IoTDBManagedService implements ManagedService {
    private IoTDBDescriptor iotdbDescriptor = null;    
    private IoTDBConfig conf = null;
    private BundleContext bundleContext;
    
    public IoTDBManagedService(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }    
    
    @Override
    public void updated(Dictionary<String, ?> props) throws ConfigurationException {
        String key = null;
        if (props != null) {
            Enumeration<String> keys = props.keys();

            String etcKaraf = System.getProperty(ConfigProperties.PROP_KARAF_ETC);
            System.setProperty(IoTDBConstant.IOTDB_CONF, etcKaraf);
            iotdbDescriptor = IoTDBDescriptor.getInstance();  
            System.out.println("");
        } else {
            System.out.println("No encontro la configuración.");
        }
        /*
        while (keys.hasMoreElements()){
            key = keys.nextElement();
            //Web Page Configuration
            if(key.equalsIgnoreCase("metrics_port"))
                conf.setMetricsPort(Integer.parseInt((String) properties.get(key))); //=8181

            //RPC Configuration
            if(key.equalsIgnoreCase("rpc_address")); //=0.0.0.0
            if(key.equalsIgnoreCase("rpc_port")); //=6667
            if(key.equalsIgnoreCase("rpc_thrift_compression_enable")); //=false
            if(key.equalsIgnoreCase("rpc_max_concurrent_client_num")); //=65535

            //Dynamic Parameter Adapter Configuration
            if(key.equalsIgnoreCase("enable_parameter_adapter")); //=true

            //Write Ahead Log Configuration
            if(key.equalsIgnoreCase("enable_wal")); //=true
            if(key.equalsIgnoreCase("flush_wal_threshold")); //=10000
            if(key.equalsIgnoreCase("force_wal_period_in_ms")); //=10

            //Timestamp Precision Configuration
            if(key.equalsIgnoreCase("timestamp_precision")); //=ms

            //Directory Configuration
            //data dirs
            if(key.equalsIgnoreCase("base_dir")); //=data
                conf.setBaseDir((String) properties.get(key));
            if(key.equalsIgnoreCase("data_dirs")); //=data\\data

            //mult_dir_strategy
            if(key.equalsIgnoreCase("multi_dir_strategy")); //=MaxDiskUsableSpaceFirstStrategy

            //wal dir
            if(key.equalsIgnoreCase("wal_dir")); //=data\\wal

            if(key.equalsIgnoreCase("tsfile_storage_fs")); //=LOCAL
            if(key.equalsIgnoreCase("core_site_path")); //=/etc/hadoop/conf/core-site.xml
            if(key.equalsIgnoreCase("hdfs_site_path")); //=/etc/hadoop/conf/hdfs-site.xml
            if(key.equalsIgnoreCase("hdfs_ip")); //=localhost
            if(key.equalsIgnoreCase("hdfs_port")); //=9000
            if(key.equalsIgnoreCase("dfs_nameservices")); //=hdfsnamespace
            if(key.equalsIgnoreCase("dfs_ha_namenodes")); //=nn1,nn2
            if(key.equalsIgnoreCase("dfs_ha_automatic_failover_enabled")); //=true
            if(key.equalsIgnoreCase("dfs_client_failover_proxy_provider")); //=org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider
            if(key.equalsIgnoreCase("hdfs_use_kerberos")); //=false
            if(key.equalsIgnoreCase("kerberos_keytab_file_path")); //=/path
            if(key.equalsIgnoreCase("kerberos_principal")); //=your principal

            //Memory Control Configuration
            if(key.equalsIgnoreCase("write_read_free_memory_proportion")); //=6:3:1
            if(key.equalsIgnoreCase("fetch_size")); //=10000
            if(key.equalsIgnoreCase("wal_buffer_size")); //=16777216
            if(key.equalsIgnoreCase("time_zone")); //=+08:00
            if(key.equalsIgnoreCase("tsfile_size_threshold")); //=536870912
            if(key.equalsIgnoreCase("memtable_size_threshold")); //=134217728
            if(key.equalsIgnoreCase("concurrent_flush_thread")); //=0
            if(key.equalsIgnoreCase("chunk_buffer_pool_enable")); //=false
            if(key.equalsIgnoreCase("default_ttl")); //=36000000

            //Upgrade Configurations
            if(key.equalsIgnoreCase("upgrade_thread_num")); //=1

            //Merge Configurations
            if(key.equalsIgnoreCase("merge_thread_num")); //=1
            if(key.equalsIgnoreCase("merge_chunk_subthread_num")); //=4
            if(key.equalsIgnoreCase("merge_fileSelection_time_budget")); //=30000
            if(key.equalsIgnoreCase("merge_memory_budget")); //=2147483648
            if(key.equalsIgnoreCase("continue_merge_after_reboot")); //=false
            if(key.equalsIgnoreCase("merge_interval_sec")); //=3600
            if(key.equalsIgnoreCase("force_full_merge")); //=false
            if(key.equalsIgnoreCase("chunk_merge_point_threshold")); //=20480

            //Metadata Cache Configuration
            if(key.equalsIgnoreCase("meta_data_cache_enable")); //=true
            if(key.equalsIgnoreCase("filemeta_chunkmeta_free_memory_proportion")); //=3:6:10

            //Statistics Monitor configuration
            if(key.equalsIgnoreCase("enable_stat_monitor")); //=false
            if(key.equalsIgnoreCase("back_loop_period_in_second")); //=5
            if(key.equalsIgnoreCase("stat_monitor_detect_freq_in_second")); //=600
            if(key.equalsIgnoreCase("stat_monitor_retain_interval_in_second")); //=600
            if(key.equalsIgnoreCase("schema_manager_cache_size")); //=300000

            //External sort Configuration
            if(key.equalsIgnoreCase("enable_external_sort")); //=true
            if(key.equalsIgnoreCase("external_sort_threshold")); // = 60

            //Sync Server Configuration
            if(key.equalsIgnoreCase("is_sync_enable")); //=true
            if(key.equalsIgnoreCase("sync_server_port")); //=5555
            if(key.equalsIgnoreCase("ip_white_list")); //=0.0.0.0/0

            //performance statistic configuration
            if(key.equalsIgnoreCase("enable_performance_stat")); //=false
            if(key.equalsIgnoreCase("performance_stat_display_interval")); //=60000
            if(key.equalsIgnoreCase("performance_stat_memory_in_kb")); //=20

            //Configurations for watermark module
            if(key.equalsIgnoreCase("watermark_module_opened")); //=false
            if(key.equalsIgnoreCase("watermark_secret_key")); //=IoTDB*2019@Beijing
            if(key.equalsIgnoreCase("watermark_bit_string")); //=100101110100
            if(key.equalsIgnoreCase("watermark_method")); //=GroupBasedLSBMethod(embed_row_cycle=2,embed_lsb_num=5)

            //Configurations for creating schema automatically
            if(key.equalsIgnoreCase("enable_auto_create_schema")); //=true
            if(key.equalsIgnoreCase("default_storage_group_level")); //=2
            if(key.equalsIgnoreCase("default_boolean_encoding")); //=RLE
            if(key.equalsIgnoreCase("default_int32_encoding")); //=RLE
            if(key.equalsIgnoreCase("default_int64_encoding")); //=RLE
            if(key.equalsIgnoreCase("default_float_encoding")); //=GORILLA
            if(key.equalsIgnoreCase("default_double_encoding")); //=GORILLA
            if(key.equalsIgnoreCase("default_text_encoding")); //=PLAIN

            //Configurations for tsfile-format
            if(key.equalsIgnoreCase("group_size_in_byte")); //=134217728
            if(key.equalsIgnoreCase("page_size_in_byte")); //=65536
            if(key.equalsIgnoreCase("max_number_of_points_in_page")); //=1048576
            if(key.equalsIgnoreCase("time_series_data_type")); //=INT64
            if(key.equalsIgnoreCase("max_string_length")); //=128
            if(key.equalsIgnoreCase("float_precision")); //=2
            if(key.equalsIgnoreCase("time_encoder")); //=TS_2DIFF
            if(key.equalsIgnoreCase("value_encoder")); //=PLAIN
            if(key.equalsIgnoreCase("compressor")); //=SNAPPY
            if(key.equalsIgnoreCase("bloom_filter_error_rate")); //=0.05
            
            */
            
    }
}
