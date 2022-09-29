/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.xmlrpc.impl;

import com.ceos.merlot.iotdb.xmlrpc.api.IoTDBXmlRpcHandler;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;


/**
 *
 * @author cgarcia
 */
public class IoTDBXmlRpcHandlerImpl implements IoTDBXmlRpcHandler {
    
    //private JdbcService jdbcService;
    private volatile int i = 0;
    private DataSource datasource;

    public IoTDBXmlRpcHandlerImpl(DataSource datasource) {
        //this.jdbcService = jdbcService;
        this.datasource = datasource;
    }
          

    public synchronized String execute(String method) throws Exception {
        String res = null;
        if ("archiver.info".equalsIgnoreCase(method)){
            res = doInfo();
        } else if ("archiver.archives".equalsIgnoreCase(method)){
            res = doArchives();
        } else if ("archiver.names".equalsIgnoreCase(method)){
            res = doNames();
        } else if ("archiver.values".equalsIgnoreCase(method)){
            res = doValues();
        }
        return res;
    }
    
    /*
    * TODO: 
    * XML-RPC Request: 
    * <?xml version="1.0"?>
    * <methodCall>
    *  <methodName>archiver.info</methodName>
    *  <params></params>
    * </methodCall>
    * 
    * XML-RPC Response: 
    * <?xml version="1.0"?>
    * <methodResponse><params><param><value>
    */
    private String doInfo(){        
        final StringBuilder buf = new StringBuilder();
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append("<methodResponse>\n");
        buf.append(" <methodName>archiver.info</methodName>\n");
        buf.append(" <params>\n");
        buf.append("  <param><value><struct>\n");
        
        buf.append("  <member>\n");
        buf.append("   <name>ver</name>\n");
        buf.append("   <value><i4>1</i4></value>\n");
        buf.append("  </member>\n");
        
        buf.append("  <member>\n");
        buf.append("   <name>desc</name>\n");
        buf.append("   <value><string>Merlot IoTDB Connector</string></value>\n");
        buf.append("  </member>\n");   
        
        buf.append("  <member>\n");
        buf.append("   <name>how</name>\n");
        buf.append("   <value><array><data>\n");
	buf.append("	<value><string>raw</string></value>\n");
	buf.append("	<value><string>spreadsheet</string></value>\n");
	buf.append("	<value><string>averaged</string></value>\n");
	buf.append("	<value><string>plot binning</string></value>\n");
	buf.append("	<value><string>linear</string></value>\n");
	buf.append("   </data></array></value>\n");	
        buf.append("  </member>\n");  
        
        buf.append("  <member>\n");
        buf.append("   <name>stat</name>\n");
        buf.append("   <value><array><data>\n");
	buf.append("	<value><string>NO ALARM</string></value>\n");
	buf.append("	<value><string>READ ALARM</string></value>\n");
	buf.append("	<value><string>WRITE ALARM</string></value>\n");
	buf.append("	<value><string>HIHI ALARM</string></value>\n");
	buf.append("	<value><string>LOLO ALARM</string></value>\n");
        buf.append("	<value><string>LOW ALARM</string></value>\n");
        buf.append("	<value><string>STATE ALARM</string></value>\n");
        buf.append("	<value><string>UDF ALARM</string></value>\n");
	buf.append("   </data></array></value>\n");	
        buf.append("  </member>\n");          

        buf.append("  <member>\n");
        buf.append("   <name>sevr</name>\n");  
        buf.append("   <value><array><data>\n"); 
     
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>0</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>NO ALARM</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>1</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>1</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");    

        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>1</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>MINOR</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>1</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>1</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");    
        
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>2</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>MAJOR</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>1</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>1</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");    
        
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>3</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>INVALID</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>1</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>1</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");   
        
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>3968</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>EST_REPEAT</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>1</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>0</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");  
        
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>3856</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>REPEAT</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>1</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>0</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");         
        
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>3904</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>DISCONNECT</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>0</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>1</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");             

        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>3872</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>ARCHIVE_OFF</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>0</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>1</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");  
        
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>num</name><value><i4>3848</i4></value></member>\n");
	buf.append("     <member><name>sevr</name><value><string>ARCHIVE_DISABLE</string></value></member>\n");
	buf.append("     <member><name>has_value</name><value><boolean>0</boolean></value></member>\n");
	buf.append("     <member><name>txt_stat</name><value><boolean>1</boolean></value></member> \n");       
	buf.append("    </struct></value>\n");          
        
	buf.append("   </data></array></value>\n");        
        buf.append("  </member>\n");          

        buf.append("  </struct></value></param>\n");        
        buf.append(" </params>\n");
        buf.append("</methodResponse>\n");        
        
        return buf.toString();
    }
    
    /*
    *
    */    
    private String doArchives(){
        final StringBuilder buf = new StringBuilder();        
        buf.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buf.append("<methodResponse>\n");
        buf.append(" <methodName>archiver.info</methodName>\n");
        buf.append(" <params>\n");
        buf.append("  <param><value><array><data>\n");
/*
        buf.append("    <value><struct>\n"); 
	buf.append("     <member><name>key</name><value><i4>1</i4></value></member>\n");
	buf.append("     <member><name>name</name><value><string>grupo raiz</string></value></member>\n");
	buf.append("     <member><name>path</name><value><string>root.</string></value></member>\n");      
	buf.append("    </struct></value>\n");              
  */      

        
        Statement statement;
        try {
            
            statement = datasource.getConnection().createStatement();
            statement.execute("SHOW STORAGE GROUP");
            
            ResultSet resultSet = statement.getResultSet();
            ResultSetMetaData metaData = statement.getResultSet().getMetaData();            
            int columnCount = metaData.getColumnCount();
            int i = 1;
            while (resultSet.next()){
                buf.append("    <value><struct>\n"); 
                buf.append("     <member><name>key</name><value><i4>").append(i).append("</i4></value></member>\n");
                buf.append("     <member><name>name</name><value><string>grupo ").append(i).append("</string></value></member>\n");
                buf.append("     <member><name>path</name><value><string>").append(resultSet.getString(1)).append("</string></value></member>\n");      
                buf.append("    </struct></value>\n"); 
                i++;
             }            
            
            outputResult(statement.getResultSet()); 

        } catch (SQLException ex) {
            Logger.getLogger(IoTDBXmlRpcHandlerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }    
        
        buf.append("  </data></array></value></param>\n");        
        buf.append(" </params>\n");
        buf.append("</methodResponse>\n");           
        
        return buf.toString();
    }
    
    /*
    *
    */    
    private String doNames(){
    
        return null;
    }    
    
    /*
    *
    */    
    private String doValues(){
    
        return null;
    }      
    
 private static void outputResult(ResultSet resultSet) throws SQLException {
    if (resultSet != null) {
      System.out.println("--------------------------");
      final ResultSetMetaData metaData = resultSet.getMetaData();
      final int columnCount = metaData.getColumnCount();
      for (int i = 0; i < columnCount; i++) {
        System.out.print(metaData.getColumnLabel(i + 1) + " *");
      }
      System.out.println();
      while (resultSet.next()) {
        for (int i = 1; ; i++) {
          System.out.print(resultSet.getString(i));
          if (i < columnCount) {
            System.out.print(", ");
          } else {
            System.out.println();
            break;
          }
        }
      }
      System.out.println("--------------------------\n");
    }
 }    

}
