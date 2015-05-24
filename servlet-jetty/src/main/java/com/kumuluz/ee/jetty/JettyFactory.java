package com.kumuluz.ee.jetty;

import com.kumuluz.ee.common.config.ServerConfig;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.JettyWebXmlConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class JettyFactory {

    private Logger log = Logger.getLogger(JettyFactory.class.getSimpleName());

    private ServerConfig serverConfig;

    public JettyFactory() {

        this.serverConfig = new ServerConfig();
    }

    public JettyFactory(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    public Server create() {

        Server server = new Server(createThreadPool());

        server.addBean(createClassList());
        server.setStopAtShutdown(true);
        server.setConnectors(new Connector[]{
                createConnector(server)
        });

        return server;
    }

    protected ThreadPool createThreadPool() {

        QueuedThreadPool threadPool = new QueuedThreadPool();

        String minThreads = Optional.ofNullable(System.getenv(ServerConfig.MIN_THREADS_ENV))
                .filter(s -> !s.isEmpty())
                .orElse(serverConfig.getMinThreads().toString());

        String maxThreads = Optional.ofNullable(System.getenv(ServerConfig.MAX_THREADS_ENV))
                .filter(s -> !s.isEmpty())
                .orElse(serverConfig.getMaxThreads().toString());

        try {

            threadPool.setMinThreads(Integer.parseInt(minThreads));
            threadPool.setMaxThreads(Integer.parseInt(maxThreads));
        } catch (NumberFormatException e) {

            throw new KumuluzServerException("Number of threads are in the incorrect format", e);
        }

        log.info("Starting KumuluzEE on Jetty with " + minThreads + " minimum and " + maxThreads + " maximum threads");

        return threadPool;
    }

    protected Connector createConnector(final Server server) {

        ServerConnector connector = new ServerConnector(
                server, new HttpConnectionFactory(new HttpConfiguration()));

        String port = Optional.ofNullable(System.getenv(ServerConfig.PORT_ENV))
                .filter(s -> !s.isEmpty())
                .orElse(serverConfig.getPort().toString());

        try {

            connector.setPort(Integer.parseInt(port));
        } catch (NumberFormatException e) {

            throw new KumuluzServerException("Port is in the incorrect format", e);
        }

        connector.setIdleTimeout(serverConfig.getIdleTimeout());
        connector.setSoLingerTime(serverConfig.getSoLingerTime());

        log.info("Starting KumuluzEE on port " + port);

        return connector;
    }

    protected Configuration.ClassList createClassList() {

        Configuration.ClassList classList = new Configuration.ClassList(new String[0]);

        classList.add(AnnotationConfiguration.class.getName());
        classList.add(WebInfConfiguration.class.getName());
        classList.add(WebXmlConfiguration.class.getName());
        classList.add(MetaInfConfiguration.class.getName());
        classList.add(FragmentConfiguration.class.getName());
        classList.add(JettyWebXmlConfiguration.class.getName());

        return classList;
    }
}
