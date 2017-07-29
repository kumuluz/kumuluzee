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
import com.kumuluz.ee.common.config.ServerConnectorConfig;
import com.kumuluz.ee.common.utils.StringUtils;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.log.JavaUtilLog;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class JettyFactory {

    private Logger log = Logger.getLogger(JettyFactory.class.getSimpleName());

    private ServerConfig serverConfig;

    public JettyFactory(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    public Server create() {

        Log.setLog(new JavaUtilLog());

        Server server = new Server(createThreadPool());

        server.addBean(createClassList());
        server.setStopAtShutdown(true);
        server.setConnectors(createConnectors(server));

        return server;
    }

    private ThreadPool createThreadPool() {

        QueuedThreadPool threadPool = new QueuedThreadPool();

        threadPool.setMinThreads(serverConfig.getMinThreads());
        threadPool.setMaxThreads(serverConfig.getMaxThreads());

        log.info("Starting KumuluzEE on Jetty with " + serverConfig.getMinThreads() + " minimum " +
                "and " + serverConfig.getMaxThreads() + " maximum threads");

        return threadPool;
    }

    private Connector[] createConnectors(final Server server) {

        ServerConnectorConfig httpConfig = serverConfig.getHttp();
        ServerConnectorConfig httpsConfig = serverConfig.getHttps();

        List<ServerConnector> connectors = new ArrayList<>();

        if (httpConfig.getEnabled() == null || httpConfig.getEnabled()) {

            HttpConfiguration httpConfiguration = new HttpConfiguration();
            httpConfiguration.setRequestHeaderSize(httpConfig.getRequestHeaderSize());
            httpConfiguration.setResponseHeaderSize(httpConfig.getResponseHeaderSize());

            ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));

            httpConnector.setPort(httpConfig.getPort());
            httpConnector.setHost(httpConfig.getAddress());

            httpConnector.setIdleTimeout(httpConfig.getIdleTimeout());
            httpConnector.setSoLingerTime(httpConfig.getSoLingerTime());

            connectors.add(httpConnector);
        }

        if (httpsConfig.getEnabled() != null && httpsConfig.getEnabled()) {

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeystorePath())) {
                throw new IllegalStateException("Cannot create SSL connector; keystore path not specified.");
            }

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeystorePassword())) {
                throw new IllegalStateException("Cannot create SSL connector; keystore password not specified.");
            }

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeyPassword())) {
                throw new IllegalStateException("Cannot create SSL connector; key password not specified.");
            }

            HttpConfiguration httpsConfiguration = new HttpConfiguration();
            httpsConfiguration.setRequestHeaderSize(httpsConfig.getRequestHeaderSize());
            httpsConfiguration.setResponseHeaderSize(httpsConfig.getResponseHeaderSize());
            httpsConfiguration.addCustomizer(new SecureRequestCustomizer());

            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(httpsConfig.getKeystorePath());
            sslContextFactory.setKeyStorePassword(httpsConfig.getKeystorePassword());
            sslContextFactory.setKeyManagerPassword(httpsConfig.getKeyPassword());

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeyAlias())) {
                sslContextFactory.setCertAlias(httpsConfig.getKeyAlias());
            }

            ServerConnector httpsConnector = new ServerConnector(
                    server,
                    new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.toString()),
                    new HttpConnectionFactory(httpsConfiguration)
            );

            httpsConnector.setPort(httpsConfig.getPort());
            httpsConnector.setHost(httpsConfig.getAddress());

            httpsConnector.setIdleTimeout(httpsConfig.getIdleTimeout());
            httpsConnector.setSoLingerTime(httpsConfig.getSoLingerTime());

            connectors.add(httpsConnector);
        }

        String ports = connectors.stream()
                .map(connector ->
                        String.format("%d [%s]", connector.getPort(), String.join(", ", connector.getProtocols())))
                .collect(Collectors.joining(", "));

        log.info(String.format("Starting KumuluzEE on port(s): %s", ports));

        return connectors.toArray(new ServerConnector[connectors.size()]);
    }

    private Configuration.ClassList createClassList() {

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
