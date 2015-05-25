package com.kumuluz.ee.common;

import java.util.EventListener;
import java.util.Map;

import javax.servlet.Servlet;

/**
 * @author Tilen
 */
public interface KumuluzServer {

    void registerServlet(Class<? extends Servlet> servletClass, String mapping);

    void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String> parameters);

    void registerListener(EventListener listener);

    String getServerName();
}
