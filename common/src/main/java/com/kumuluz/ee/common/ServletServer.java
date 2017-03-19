package com.kumuluz.ee.common;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.sql.DataSource;
import java.util.EnumSet;
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

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec);

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec, Map<String, String> parameters);

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches);

    void registerFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches, Map<String, String> parameters);

    void registerDataSource(DataSource ds, String jndiName);

    void initWebContext();
}
