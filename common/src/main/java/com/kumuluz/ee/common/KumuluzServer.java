package com.kumuluz.ee.common;

import java.util.Map;

/**
 * @author Tilen
 */
public interface KumuluzServer {

    void registerServlet(Class<?> servletClass, String mapping);

    void registerServlet(Class<?> servletClass, String mapping, Map<String, String> parameters);

    String getServerName();
}
