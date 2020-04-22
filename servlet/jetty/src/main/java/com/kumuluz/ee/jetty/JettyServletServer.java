/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.jetty;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.attributes.ClasspathAttributes;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.config.GzipConfig;
import com.kumuluz.ee.common.config.ServerConfig;
import com.kumuluz.ee.common.config.ServerConnectorConfig;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.dependencies.ServerDef;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.servlet.ServletWrapper;
import com.kumuluz.ee.common.utils.ResourceUtils;
import org.eclipse.jetty.plus.jndi.Resource;
import org.eclipse.jetty.plus.jndi.Transaction;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.SecuredRedirectHandler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.Servlet;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
@ServerDef(value = "Jetty", provides = {EeComponentType.SERVLET})
public class JettyServletServer implements ServletServer {

    private Logger log = Logger.getLogger(JettyServletServer.class.getSimpleName());

    private Server server;

    private WebAppContext appContext;

    private ServerConfig serverConfig;

    @Override
    public void initServer() {

        server = createJettyFactory().create();
    }

    @Override
    public void startServer() {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before starting it");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty is already started");

        try {
            server.start();
        } catch (Exception e) {

            log.severe(e.getMessage());

            throw new KumuluzServerException(e.getMessage(), e);
        }
    }

    @Override
    public void stopServer() {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before stopping it");

        if (server.isStopped() || server.isStopping())
            throw new IllegalStateException("Jetty is already stopped");

        try {
            server.stop();
        } catch (Exception e) {

            log.severe(e.getMessage());

            throw new KumuluzServerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void initWebContext(List<String> scanLibraries) {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before adding a web context");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty cannot be started before adding a web context");

        if (EeConfig.getInstance().getDev().getScanLibraries() != null) {
            scanLibraries.addAll(EeConfig.getInstance().getDev().getScanLibraries());
        }

        appContext = new WebAppContext();

        if (ResourceUtils.isRunningInJar()) {
            appContext.setAttribute(JettyAttributes.jarPattern, ClasspathAttributes.jar);

            appContext.setExtraClasspath(JettyJarClasspathUtil.getExtraClasspath(scanLibraries));

            try {
                appContext.setClassLoader(getClass().getClassLoader());
            } catch (Exception e) {
                throw new IllegalStateException("Unable to set custom classloader for Jetty", e);
            }
        } else {
            StringBuilder explodedClasspath = new StringBuilder(ClasspathAttributes.exploded);

            if (ResourceUtils.isRunningTests()) {
                explodedClasspath.append("|").append(ClasspathAttributes.exploded_test);
            }

            for (String lib : scanLibraries) {
                if (lib.endsWith(".jar")) {
                    explodedClasspath.append("|^.*/").append(Pattern.quote(lib)).append("$");
                } else {
                    explodedClasspath.append("|^.*/").append(Pattern.quote(lib)).append("-[^/]+\\.jar$");
                }
            }

            log.fine("Using classpath scanning regex: " + explodedClasspath.toString());
            appContext.setAttribute(JettyAttributes.jarPattern, explodedClasspath.toString());
        }

        appContext.setParentLoaderPriority(true);

        appContext.setResourceBase(ResourceUtils.getProjectWebResources());

        appContext.setContextPath(serverConfig.getContextPath());

        if (serverConfig.getForwardStartupException() != null) {
            appContext.setThrowUnavailableOnStartupException(serverConfig.getForwardStartupException());
        }

        if (!Boolean.TRUE.equals(serverConfig.getDirBrowsing())) {

            appContext.setInitParameter(JettyAttributes.dirBrowsing, "false");
        }

        if (Boolean.TRUE.equals(serverConfig.getEtags())) {
            appContext.setInitParameter(JettyAttributes.etags, "true");
        }
        log.info("Starting KumuluzEE with context root '" + serverConfig.getContextPath() + "'");

        GzipConfig gzipConfig = serverConfig.getGzip();

        if (serverConfig.getForceHttps() || (gzipConfig != null && gzipConfig.getEnabled())) {

            final ArrayList<Handler> handlers = new ArrayList<>();

            // Set the secured redirect handler in case the force https option is selected
            if( serverConfig.getForceHttps()) {
                handlers.add(new SecuredRedirectHandler());
            }

            // Set the gzip handler in case the use gzip option is selected
            if(gzipConfig != null && gzipConfig.getEnabled()) {
                GzipHandler gzipHandler = new GzipHandler();

                if(gzipConfig.getMinGzipSize() != null)
                    gzipHandler.setMinGzipSize(gzipConfig.getMinGzipSize());
                if(gzipConfig.getIncludedMethods() != null)
                    gzipHandler.setIncludedMethods(gzipConfig.getIncludedMethods().toArray(new String[0]));
                if(gzipConfig.getIncludedMimeTypes() != null)
                    gzipHandler.setIncludedMimeTypes(gzipConfig.getIncludedMimeTypes().toArray(new String[0]));
                if(gzipConfig.getExcludedMimeTypes() != null)
                    gzipHandler.setExcludedMimeTypes(gzipConfig.getExcludedMimeTypes().toArray(new String[0]));
                if(gzipConfig.getExcludedAgentPatterns() != null)
                    gzipHandler.setExcludedAgentPatterns(gzipConfig.getExcludedAgentPatterns().toArray(new String[0]));
                if(gzipConfig.getExcludedPaths() != null)
                    gzipHandler.setExcludedPaths(gzipConfig.getExcludedPaths().toArray(new String[0]));
                if(gzipConfig.getIncludedPaths() != null)
                    gzipHandler.setIncludedPaths(gzipConfig.getIncludedPaths().toArray(new String[0]));

                gzipHandler.setHandler(appContext);
                handlers.add(gzipHandler);
            } else {
                handlers.add(appContext);
            }

            HandlerList handlerList = new HandlerList();
            handlerList.setHandlers(handlers.toArray(new Handler[0]));

            server.setHandler(handlerList);
        } else {

            server.setHandler(appContext);
        }
    }

    @Override
    public ServerConfig getServerConfig() {

        return serverConfig;
    }

    @Override
    public void setServerConfig(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    @Override
    public void registerServlet(Class<? extends Servlet> servletClass, String mapping) {

        registerServlet(servletClass, mapping, null, 0);
    }

    @Override
    public void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String>
            parameters) {

        registerServlet(servletClass, mapping, parameters, 0);
    }

    @Override
    public void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String>
            parameters, int initOrder) {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before adding a servlet ");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty cannot be started before adding a servlet");

        @SuppressWarnings("unchecked")
        Class<Servlet> servlet = (Class<Servlet>) servletClass;

        ServletHolder holder = new ServletHolder(servlet);
        holder.setInitOrder(initOrder);

        if (parameters != null) {
            parameters.forEach(holder::setInitParameter);
        }

        appContext.addServlet(holder, mapping);
    }

    @Override
    public void registerListener(EventListener listener) {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before adding a listener");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty cannot be started before adding a listener");

        appContext.addEventListener(listener);
    }

    @Override
    public void registerFilter(Class<? extends Filter> filterClass, String pathSpec) {

        registerFilter(filterClass, pathSpec, EnumSet.of(DispatcherType.REQUEST));
    }

    @Override
    public void registerFilter(Class<? extends Filter> filterClass, String pathSpec, Map<String, String> parameters) {

        registerFilter(filterClass, pathSpec, EnumSet.of(DispatcherType.REQUEST), parameters);
    }

    @Override
    public void registerFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches) {

        registerFilter(filterClass, pathSpec, dispatches, null);
    }

    @Override
    public void registerFilter(Class<? extends Filter> filterClass, String pathSpec, EnumSet<DispatcherType> dispatches, Map<String,
            String> parameters) {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before adding a servlet ");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty cannot be started before adding a servlet");

        FilterHolder holder = new FilterHolder(filterClass);

        if (parameters != null) {

            parameters.forEach(holder::setInitParameter);
        }

        appContext.addFilter(holder, pathSpec, dispatches);
    }

    @Override
    public void registerDataSource(DataSource ds, String jndiName) {

        try {
            Resource resource = new Resource(jndiName, ds);

            appContext.setAttribute(jndiName, resource);
        } catch (NamingException e) {
            throw new IllegalArgumentException("Unable to create naming data source entry with jndi name " + jndiName + "", e);
        }
    }

    @Override
    public List<ServletWrapper> getRegisteredServlets() {

        List<ServletWrapper> servlets = new ArrayList<>();

        Arrays.stream(this.appContext.getServletHandler().getServlets())
                .forEach(s -> servlets.add(new ServletWrapper(s.getName(), s.getContextPath())));

        return servlets;
    }

    @Override
    public void registerResource(Object o, String jndiName) {

        try {
            Resource resource = new Resource(jndiName, o);

            appContext.setAttribute(jndiName, resource);
        } catch (NamingException e) {
            throw new IllegalArgumentException("Unable to create naming resource entry with jndi name " + jndiName + "", e);
        }
    }

    @Override
    public void registerTransactionManager(UserTransaction userTransaction) {
        try {
            new Transaction(userTransaction);
        } catch (NamingException e) {
            throw new IllegalArgumentException("Unable to create transaction manager", e);
        }
    }

    private JettyFactory createJettyFactory() {

        return new JettyFactory(serverConfig);
    }
}
