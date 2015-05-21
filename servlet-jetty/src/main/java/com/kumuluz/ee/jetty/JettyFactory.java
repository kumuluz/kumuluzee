package com.kumuluz.ee.jetty;

import com.kumuluz.ee.common.config.ServerConfig;

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

/**
 * @author Tilen
 */
public class JettyFactory {

    private ServerConfig serverConfig;

    public JettyFactory() {
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
        threadPool.setMinThreads(5);
        threadPool.setMaxThreads(100);
        return threadPool;
    }

    protected Connector createConnector(final Server server) {

        ServerConnector connector = new ServerConnector(
                server, new HttpConnectionFactory(new HttpConfiguration()));
        connector.setPort(8080);
        connector.setIdleTimeout(60 * 60 * 1000);
        connector.setSoLingerTime(-1);
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
