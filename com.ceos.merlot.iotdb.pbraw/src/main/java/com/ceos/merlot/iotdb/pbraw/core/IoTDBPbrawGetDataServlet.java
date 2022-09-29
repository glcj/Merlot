/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.pbraw.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.karaf.jdbc.JdbcService;

/**
 *
 * @author cgarcia
 */
public class IoTDBPbrawGetDataServlet extends HttpServlet{
    
    private final JdbcService jdbcService;

    public IoTDBPbrawGetDataServlet(JdbcService jdbcService) {
        this.jdbcService = jdbcService;
    }
        
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader br = req.getReader();
        StringBuilder sb = new StringBuilder();
        String strKey = null;
        String strValue = null;
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        System.out.println("IoTDBPbrawGetDataAServlet: \r\n" + sb.toString()); 
        Enumeration<String> keys = req.getParameterNames();
        
        while (keys.hasMoreElements()) {
            strKey = keys.nextElement();
            System.out.println("Key: " + strKey + " Value: " + req.getParameter(strKey) );            
        }            
        resp.setStatus(200);
        resp.setContentType("text/plain");
        try(PrintWriter out = resp.getWriter()) {
            out.println("UNO");
            out.println("DOS");
            out.println("TRES");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Post: IoTDBPbrawRetrievalServlet");
        doGet(req, resp);
    }
    
}
