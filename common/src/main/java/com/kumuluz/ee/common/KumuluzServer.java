package com.kumuluz.ee.common;

import java.util.Map;

import javax.servlet.Servlet;

/**
 * @author Tilen
 */
public interface KumuluzServer {

    void registerServlet(Class<? extends Servlet> servletClass, String mapping);

    void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String> parameters);

    String getServerName();
}
