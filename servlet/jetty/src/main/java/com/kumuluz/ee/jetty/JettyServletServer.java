package com.kumuluz.ee.jetty;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.attributes.ClasspathAttributes;
import com.kumuluz.ee.common.config.ServerConfig;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.dependencies.ServerDef;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.utils.ResourceUtils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.EventListener;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.Servlet;

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

            throw new KumuluzServerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void stopServer() {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before stopping it");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty is not running stopped");

        try {
            server.stop();
        } catch (Exception e) {

            log.severe(e.getMessage());

            throw new KumuluzServerException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void initWebContext() {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before adding a web " +
                    "context");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty cannot be started before adding a web context");

        appContext = new WebAppContext();

        appContext.setAttribute(JettyAttributes.jarPattern, ClasspathAttributes.exploded);

        appContext.setParentLoaderPriority(true);

        appContext.setResourceBase(ResourceUtils.getProjectWebResources());

        appContext.setContextPath(serverConfig.getContextPath());

        log.info("Starting KumuluzEE with context root '" + serverConfig.getContextPath() + "'");

        server.setHandler(appContext);
    }

    @Override
    public void setServerConfig(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    @Override
    public ServerConfig getServerConfig() {

        return serverConfig;
    }

    @Override
    public void registerServlet(Class<? extends Servlet> servletClass, String mapping) {

        registerServlet(servletClass, mapping, null);
    }

    @Override
    public void registerServlet(Class<? extends Servlet> servletClass, String mapping, Map<String, String>
            parameters) {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before adding a servlet ");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty cannot be started before adding a servlet");

        @SuppressWarnings("unchecked")
        Class<Servlet> servlet = (Class<Servlet>) servletClass;

        ServletHolder holder = new ServletHolder(servlet);
        holder.setInitOrder(0);

        if (parameters != null) {

            parameters.forEach(holder::setInitParameter);
        }

        appContext.addServlet(holder, mapping);
    }

    @Override
    public void registerListener(EventListener listener) {

        if (server == null)
            throw new IllegalStateException("Jetty has to be initialized before adding a servlet ");

        if (server.isStarted() || server.isStarting())
            throw new IllegalStateException("Jetty cannot be started before adding a servlet");

        appContext.addEventListener(listener);
    }

    private JettyFactory createJettyFactory() {

        return new JettyFactory(serverConfig);
    }
}
