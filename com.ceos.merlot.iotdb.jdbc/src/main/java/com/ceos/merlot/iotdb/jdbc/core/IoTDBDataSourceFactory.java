/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.jdbc.core;

import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.apache.iotdb.jdbc.IoTDBDriver;
import org.ops4j.pax.jdbc.common.BeanConfig;
import org.osgi.service.jdbc.DataSourceFactory;

/**
 *
 * @author cgarcia
 */
public class IoTDBDataSourceFactory implements DataSourceFactory {

    public IoTDBDataSourceFactory() throws ClassNotFoundException {
        Class.forName(IoTDBCommonDataSource.IOTDB_DRIVER);        
    }
    
    @Override
    public DataSource createDataSource(Properties props) throws SQLException {
        IoTDBDataSource dataSource = new IoTDBDataSource();
        String url = props.getProperty(JDBC_URL);
        if (url == null) {
            dataSource.setUrl("jdbc:iotdb:" + props.getProperty(JDBC_DATABASE_NAME));
            props.remove(JDBC_DATABASE_NAME);
        } else {
            dataSource.setUrl(url);
            props.remove(JDBC_URL);
        }

        if (!props.isEmpty()) {
            BeanConfig.configure(dataSource, props);
        }
        return dataSource;        
        
    }

    @Override
    public ConnectionPoolDataSource createConnectionPoolDataSource(Properties props) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public XADataSource createXADataSource(Properties props) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Driver createDriver(Properties props) throws SQLException {
        IoTDBDriver driver = new IoTDBDriver();
        return driver;
    }
    
}
