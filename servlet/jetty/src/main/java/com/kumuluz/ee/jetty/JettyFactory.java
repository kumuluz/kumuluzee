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

import com.kumuluz.ee.common.config.ServerConfig;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.log.Log;
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

        Log.setLog(new JavaUtilLog());

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

        HttpConfiguration configuration = new HttpConfiguration();
        configuration.setRequestHeaderSize(serverConfig.getRequestHeaderSize());
        configuration.setResponseHeaderSize(serverConfig.getResponseHeaderSize());

        ServerConnector connector = new ServerConnector(
                server, new HttpConnectionFactory(configuration));

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
