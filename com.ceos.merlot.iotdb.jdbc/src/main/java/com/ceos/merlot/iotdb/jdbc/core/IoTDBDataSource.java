/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.jdbc.core;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;

/**
 * A factory for connections to the physical data source that this DataSource
 * object represents. An alternative to the DriverManager facility, 
 * a DataSource object is the preferred means of getting a connection. 
 * An object that implements the DataSource interface will typically be 
 * registered with a naming service based on the JavaTM Naming and 
 * Directory (JNDI) API.
 * 
 * The DataSource interface is implemented by a driver vendor. There are 
 * three types of implementations:
 * 
 * <b>Basic implementation</b> -- produces a standard Connection object
 * <b>Connection pooling implementation</b> -- produces a Connection object 
 * that will automatically participate in connection pooling. 
 * This implementation works with a middle-tier connection pooling manager.
 * <b>Distributed transaction implementation</b> -- produces a Connection 
 * object that may be used for distributed transactions and almost always 
 * participates in connection pooling. This implementation works with a 
 * middle-tier transaction manager and almost always with a connection 
 * pooling manager. 
 * 
 * A DataSource object has properties that can be modified when necessary. 
 * For example, if the data source is moved to a different server, the 
 * property for the server can be changed. The benefit is that because the 
 * data source's properties can be changed, any code accessing that 
 * data source does not need to be changed.
 * 
 * A driver that is accessed via a DataSource object does not register 
 * itself with the DriverManager. Rather, a DataSource object is retrieved 
 * though a lookup operation and then used to create a Connection object. 
 * With a basic implementation, the connection obtained through a DataSource 
 * object is identical to a connection obtained through the 
 * DriverManager facility.
 * 
 * Since:
 *     1.4
 * 
 * @author cgarcia
 */
public class IoTDBDataSource extends IoTDBCommonDataSource 
        implements DataSource, Serializable{

    @Override
    public Connection getConnection() throws SQLException {
        if (url == null) {
                throw new SQLException("url is null");
            }

        if (connectionProps == null) {
            if (user == null) {
                throw new SQLException("user is null");
            }

            if (password == null) {
                throw new SQLException("password is null");
            }

            return getConnection(user, password);
        }

        return getConnection(url, connectionProps);
    }
    
    public Connection getConnection(String username,
                                    String password) throws SQLException {

        if (username == null) {
            throw new SQLException("User name not set.");
        }

        if (password == null) {
            throw new SQLException("Password not set.");
        }

        Properties props = new Properties();

        props.setProperty("user", username);
        props.setProperty("password", password);
        props.setProperty("loginTimeout", Integer.toString(loginTimeout));

        return getConnection(url, props);
    }
    

   private Connection getConnection(String url,
                                     Properties props) throws SQLException {

        if (!url.startsWith("jdbc:iotdb:")) {
            url = "jdbc:iotdb:" + url;
        }
     
        Connection connection = null;
        try {
          //Class.forName(driver);
          connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
          e.printStackTrace();
        }
        return connection;        
    }    
    
    
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
