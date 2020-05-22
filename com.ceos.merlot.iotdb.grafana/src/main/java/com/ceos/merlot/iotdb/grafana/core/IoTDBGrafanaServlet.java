/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ceos.merlot.iotdb.grafana.core;

import java.util.regex.Pattern;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author cgarcia
 */
public class IoTDBGrafanaServlet  extends HttpServlet{
    public static Pattern PATTERN_TS_ROOT = Pattern.compile("^root\\.\\*");
}
