package com.kumuluz.ee.common;

import javax.servlet.Servlet;
import java.util.EventListener;
import java.util.Map;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public interface ServletServer extends KumuluzServer {

    void registerServlet(Class<? extends Servlet> servletClass, String mapping);

    void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String> parameters);

    void registerListener(EventListener listener);

    void initWebContext();
}
