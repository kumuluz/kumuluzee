package com.kumuluz.ee.common;

import javax.servlet.Servlet;
import java.util.EventListener;
import java.util.Map;

/**
 * @author Tilen
 */
public interface ServletServer extends KumuluzServer {

    void registerServlet(Class<? extends Servlet> servletClass, String mapping);

    void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String> parameters);

    void registerListener(EventListener listener);

    void initWebContext();
}
