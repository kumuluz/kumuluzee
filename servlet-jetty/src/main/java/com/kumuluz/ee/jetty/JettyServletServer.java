package com.kumuluz.ee.jetty;

import com.kumuluz.ee.common.ServletServer;
import com.kumuluz.ee.common.attributes.ClasspathAttributes;
import com.kumuluz.ee.common.config.ServerConfig;
import com.kumuluz.ee.common.exceptions.ServletServerException;
import com.kumuluz.ee.common.utils.ResourceUtils;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class JettyServletServer implements ServletServer {

    Logger log = Logger.getLogger(JettyServletServer.class.getSimpleName());

    private Server server;

    private ServerConfig serverConfig;

    public JettyServletServer() {
    }

    @Override
    public void initServer() {

        server = createJettyFactory().create();

        log.info(getServerName() + " initiated");
    }

    @Override
    public void startServer() {

        if (server == null) {

            String msg = "Jetty has to be initialized before starting it";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        if (server.isStarted() || server.isStarting()) {

            String msg = "Jetty is already started";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        try {
            server.start();
        } catch (Exception e) {

            log.severe(e.getMessage());

            throw new ServletServerException(e.getMessage(), e.getCause());
        }

        log.info(getServerName() + " started");
    }

    @Override
    public void stopServer() {

        if (server == null) {

            String msg = "Jetty has to be initialized before stopping it";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        if (server.isStarted() || server.isStarting()) {

            String msg = "Jetty is already stopped";

            log.severe(msg);

            throw new IllegalStateException(msg);
        }

        try {
            server.stop();
        } catch (Exception e) {

            log.severe(e.getMessage());

            throw new ServletServerException(e.getMessage(), e.getCause());
        }

        log.info(getServerName() + " stopped");
    }

    @Override
    public void initWebContext() {

        WebAppContext appContext = new WebAppContext();

        appContext.setAttribute(JettyAttributes.jarPattern, ClasspathAttributes.exploded);

        appContext.setParentLoaderPriority(true);

        appContext.setResourceBase(ResourceUtils.getProjectWebResources());

        String contextPath = Optional.ofNullable(System.getenv(ServerConfig.CONTEXT_PATH_ENV))
                .filter(s -> !s.isEmpty())
                .orElse(serverConfig.getContextPath());

        appContext.setContextPath(contextPath);

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
    public String getServerName() {

        return "Jetty";
    }

    private JettyFactory createJettyFactory() {

        return new JettyFactory(serverConfig);
    }
}
