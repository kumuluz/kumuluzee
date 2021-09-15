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
import org.eclipse.jetty.alpn.server.ALPNServerConnectionFactory;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http2.HTTP2Cipher;
import org.eclipse.jetty.http2.server.HTTP2CServerConnectionFactory;
import org.eclipse.jetty.http2.server.HTTP2ServerConnectionFactory;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class JettyFactory {

    private static final Logger LOG = Logger.getLogger(JettyFactory.class.getSimpleName());

    private final ServerConfig serverConfig;

    public JettyFactory(ServerConfig serverConfig) {

        this.serverConfig = serverConfig;
    }

    public Server create() {

        Server server = new Server(createThreadPool());

        server.setStopAtShutdown(true);
        server.setConnectors(createConnectors(server));

        return server;
    }

    private ThreadPool createThreadPool() {

        QueuedThreadPool threadPool = new QueuedThreadPool();

        threadPool.setMinThreads(serverConfig.getMinThreads());
        threadPool.setMaxThreads(serverConfig.getMaxThreads());

        LOG.info("Starting KumuluzEE on Jetty with " + serverConfig.getMinThreads() + " minimum " +
                "and " + serverConfig.getMaxThreads() + " maximum threads");

        return threadPool;
    }

    private Connector[] createConnectors(final Server server) {

        ServerConnectorConfig httpConfig = serverConfig.getHttp();
        ServerConnectorConfig httpsConfig = serverConfig.getHttps();

        List<ServerConnector> connectors = new ArrayList<>();

        if (Boolean.FALSE.equals(httpConfig.getEnabled()) && (httpsConfig == null || Boolean.FALSE.equals(httpsConfig.getEnabled()))) {
            throw new IllegalStateException("Both the HTTP and HTTPS connectors can not be disabled. Please enable at least one.");
        }

        if (serverConfig.getForceHttps() && (httpsConfig == null || !Boolean.TRUE.equals(httpsConfig.getEnabled()))) {
            throw new IllegalStateException("You must enable the HTTPS connector in order to force redirects to it (`kumuluzee.server" +
                    ".https.enabled` must be true).");
        }

        if (httpConfig.getEnabled() == null || httpConfig.getEnabled()) {

            HttpConfiguration httpConfiguration = new HttpConfiguration();
            httpConfiguration.setRequestHeaderSize(httpConfig.getRequestHeaderSize());
            httpConfiguration.setResponseHeaderSize(httpConfig.getResponseHeaderSize());
            httpConfiguration.setSendServerVersion(serverConfig.getShowServerInfo());

            if (Boolean.TRUE.equals(httpConfig.getProxyForwarding())) {
                httpConfiguration.addCustomizer(new ForwardedRequestCustomizer());
            }

            if (httpsConfig != null && Boolean.TRUE.equals(httpsConfig.getEnabled())) {
                httpConfiguration.setSecurePort(
                        httpsConfig.getPort() == null ? ServerConnectorConfig.DEFAULT_HTTPS_PORT : httpsConfig.getPort());
            }

            ServerConnector httpConnector;

            HttpConnectionFactory http = new HttpConnectionFactory(httpConfiguration);

            if (httpConfig.getHttp2()) {

                HTTP2CServerConnectionFactory http2c = new HTTP2CServerConnectionFactory(httpConfiguration);

                httpConnector = new ServerConnector(server, http, http2c);
            } else {

                httpConnector = new ServerConnector(server, http);
            }

            httpConnector.setPort(httpConfig.getPort() == null ? ServerConnectorConfig.DEFAULT_HTTP_PORT : httpConfig.getPort());
            httpConnector.setHost(httpConfig.getAddress());

            httpConnector.setIdleTimeout(httpConfig.getIdleTimeout());

            connectors.add(httpConnector);
        }

        if (httpsConfig != null && httpsConfig.getEnabled() != null && httpsConfig.getEnabled()) {

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeystorePath())) {
                throw new IllegalStateException("Cannot create SSL connector; keystore path not specified.");
            }

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeystorePassword())) {
                throw new IllegalStateException("Cannot create SSL connector; keystore password not specified.");
            }

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeyPassword())) {
                throw new IllegalStateException("Cannot create SSL connector; key password not specified.");
            }

            ServerConnector httpsConnector;

            HttpConfiguration httpsConfiguration = new HttpConfiguration();
            httpsConfiguration.setRequestHeaderSize(httpsConfig.getRequestHeaderSize());
            httpsConfiguration.setResponseHeaderSize(httpsConfig.getResponseHeaderSize());
            httpsConfiguration.addCustomizer(new SecureRequestCustomizer());
            httpsConfiguration.setSendServerVersion(serverConfig.getShowServerInfo());

            if (Boolean.TRUE.equals(httpsConfig.getProxyForwarding())) {
                httpsConfiguration.addCustomizer(new ForwardedRequestCustomizer());
            }

            HttpConnectionFactory http = new HttpConnectionFactory(httpsConfiguration);

            SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
            sslContextFactory.setKeyStorePath(httpsConfig.getKeystorePath());
            sslContextFactory.setKeyStorePassword(httpsConfig.getKeystorePassword());

            if (httpsConfig.getKeyPassword() != null) {
                sslContextFactory.setKeyManagerPassword(httpsConfig.getKeyPassword());
            }

            if (StringUtils.isNullOrEmpty(httpsConfig.getKeyAlias())) {
                sslContextFactory.setCertAlias(httpsConfig.getKeyAlias());
            }

            if (httpsConfig.getSslProtocols() != null) {

                sslContextFactory.setIncludeProtocols(httpsConfig.getSslProtocols().toArray(new String[0]));
            }

            if (httpsConfig.getSslCiphers() != null) {

                sslContextFactory.setExcludeCipherSuites();
                sslContextFactory.setIncludeCipherSuites(httpsConfig.getSslCiphers().toArray(new String[0]));
            }

            if (httpsConfig.getHttp2()) {

                sslContextFactory.setCipherComparator(HTTP2Cipher.COMPARATOR);
                sslContextFactory.setUseCipherSuitesOrder(true);

                HTTP2ServerConnectionFactory h2 = new HTTP2ServerConnectionFactory(httpsConfiguration);

                ALPNServerConnectionFactory alpn = new ALPNServerConnectionFactory();
                alpn.setDefaultProtocol(HttpVersion.HTTP_1_1.toString());

                SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, alpn.getProtocol());

                httpsConnector = new ServerConnector(server, ssl, alpn, h2, http);
            } else {

                SslConnectionFactory ssl = new SslConnectionFactory(sslContextFactory, http.getProtocol());

                httpsConnector = new ServerConnector(server, ssl, http);
            }

            httpsConnector.setPort(httpsConfig.getPort() == null ? ServerConnectorConfig.DEFAULT_HTTPS_PORT : httpsConfig.getPort());
            httpsConnector.setHost(httpsConfig.getAddress());

            httpsConnector.setIdleTimeout(httpsConfig.getIdleTimeout());

            connectors.add(httpsConnector);
        }

        String ports = connectors.stream()
                .map(connector ->
                        String.format("%d [%s]", connector.getPort(), String.join(", ", connector.getProtocols())))
                .collect(Collectors.joining(", "));

        LOG.info(String.format("Starting KumuluzEE on port(s): %s", ports));

        return connectors.toArray(new ServerConnector[0]);
    }
}
