/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.xmlrpc.core;

import com.ceos.merlot.iotdb.xmlrpc.api.IoTDBXmlRpcHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author cgarcia
 */
public class IoTDBXmlRpcServlet extends HttpServlet  {

    private IoTDBXmlRpcHandler  handler = null;
    /*
    private XmlRpcHandler  xmlhandler = null;
*/
    
    public IoTDBXmlRpcServlet(IoTDBXmlRpcHandler  handler) {
        super();
       this.handler = handler;
       // xmlrpc.addHandler("archiver", xmlhandler);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String strResp  = null;
        try {
            resp.setStatus(200);
            String key = req.getParameter("key");
            Enumeration<String> params = req.getParameterNames();
            String documentType = req.getContentType();
            String xmlRequest = null;

            //Phoebus request
            if ("application/x-www-form-urlencoded".equalsIgnoreCase(documentType)){
                xmlRequest = "<?xml version " + req.getParameter("<?xml version");                
            } else //CSS Request
                if ("text/xml".equalsIgnoreCase(documentType)) {
                byte bytes[] = req.getInputStream().readAllBytes();
                xmlRequest = new String(bytes, StandardCharsets.UTF_8);
            } else {
                
            }
            
            System.out.println("XML-RPC Request: " + xmlRequest);

            //System.out.println("Parts: " + strParts);         
            resp.setContentType("text/xml");
            if (xmlRequest.contains("archiver.info")) {
                strResp = handler.execute("archiver.info");
            } else if (xmlRequest.contains("archiver.archives")) {
                strResp = handler.execute("archiver.archives");
            }
            System.out.println("XML-RPC Response: " + strResp);
            resp.getOutputStream().print(strResp);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getLocalizedMessage());;
        }
    }    
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp); //To change body of generated methods, choose Tools | Templates.
    }





    
   
}
