package com.kumuluz.ee.common;

/**
 * @author Tilen
 */
public interface KumuluzServer {

    void registerServlet(Class<?> servletClass, String mapping);

    String getServerName();
}
