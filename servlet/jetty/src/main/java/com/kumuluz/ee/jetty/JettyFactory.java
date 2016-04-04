package com.kumuluz.ee.jetty;

import com.kumuluz.ee.common.config.ServerConfig;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
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

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
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

        threadPool.setMinThreads(serverConfig.getMinThreads());
        threadPool.setMaxThreads(serverConfig.getMaxThreads());

        log.info("Starting KumuluzEE on Jetty with " + serverConfig.getMinThreads() + " minimum " +
                "and " + serverConfig.getMaxThreads() + " maximum threads");

        return threadPool;
    }

    protected Connector createConnector(final Server server) {

        ServerConnector connector = new ServerConnector(
                server, new HttpConnectionFactory(new HttpConfiguration()));

        connector.setPort(serverConfig.getPort());

        connector.setIdleTimeout(serverConfig.getIdleTimeout());
        connector.setSoLingerTime(serverConfig.getSoLingerTime());

        log.info("Starting KumuluzEE on port " + serverConfig.getPort());

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
        classList.add(EnvConfiguration.class.getName());
        classList.add(PlusConfiguration.class.getName());

        return classList;
    }
}
