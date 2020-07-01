/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.jdbc.command;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.karaf.shell.api.action.Action;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.Option;
import org.apache.karaf.shell.api.action.lifecycle.Reference;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.apache.karaf.shell.support.table.Row;
import org.apache.karaf.shell.support.table.ShellTable;
import org.osgi.framework.BundleContext;

/**
 *
 * @author cgarcia
 */
@Command(scope = "iotdb", name = "execute", description = "Command for test.")
@Service
public class IoTDBJdbcExecuteCommand  implements Action {
    
    @Reference
    BundleContext bundleContext;
    
    @Reference(filter = "(osgi.jndi.service.name=iotdb)")
    DataSource default_iotdb;
    
    @Argument(index = 0, name = "ds", description = "The device unit identifier.", required = false, multiValued = false)
    String ds;
  
    @Option(name = "-s", aliases = "--sentence", description = "Instruction to execute.", required = true, multiValued = false)
    String request = null;  
    
    
    @Override
    public Object execute() throws Exception {
        executeSentence(default_iotdb, request);
        return null;
    }
    
    private void executeSentence(DataSource ds, String sentence) {
        try {
            Connection connection = ds.getConnection();
            Statement statement = connection.createStatement();
            if (connection != null){
                statement.execute(sentence);
                outputResult(statement.getResultSet());                
            }
        } catch (SQLException ex) {
            Logger.getLogger(IoTDBJdbcExecuteCommand.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    private static void outputResult(ResultSet resultSet) throws SQLException {
        if (resultSet != null) {
          ShellTable table = new ShellTable();
          final ResultSetMetaData metaData = resultSet.getMetaData();
          final int columnCount = metaData.getColumnCount();
          String[] strColumns = new String[columnCount];
          for (int i = 0; i < columnCount; i++) {
            table.column(metaData.getColumnLabel(i + 1));
          }
          System.out.println();
          while (resultSet.next()) {
            Row row = table.addRow();          
            ArrayList<Object> rowData = new ArrayList<>();       
            for (int i = 1; i <= columnCount ; i++) {
              rowData.add(resultSet.getString(i));
            }
            row.addContent(rowData);

          }
          table.print(System.out);
          System.out.println("");
        }
    }    
}
