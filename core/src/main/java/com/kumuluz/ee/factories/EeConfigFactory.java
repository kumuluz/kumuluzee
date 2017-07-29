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
package com.kumuluz.ee.factories;

import com.kumuluz.ee.common.config.*;
import com.kumuluz.ee.common.exceptions.KumuluzServerException;
import com.kumuluz.ee.common.utils.EnvUtils;
import com.kumuluz.ee.common.utils.StringUtils;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import java.util.List;
import java.util.Optional;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class EeConfigFactory {

    private static final String PORT_ENV = "PORT";

    private static final String LEGACY_MIN_THREADS_ENV = "MIN_THREADS";
    private static final String LEGACY_MAX_THREADS_ENV = "MAX_THREADS";
    private static final String LEGACY_REQUEST_HEADER_SIZE_ENV = "REQUEST_HEADER_SIZE";
    private static final String LEGACY_RESPONSE_HEADER_SIZE_ENV = "RESPONSE_HEADER_SIZE";
    private static final String LEGACY_CONTEXT_PATH_ENV = "CONTEXT_PATH";

    private static final String LEGACY_DB_UNIT_ENV = "DATABASE_UNIT";
    private static final String LEGACY_DB_URL_ENV = "DATABASE_URL";
    private static final String LEGACY_DB_USER_ENV = "DATABASE_USER";
    private static final String LEGACY_DB_PASS_ENV = "DATABASE_PASS";

    public static EeConfig buildEeConfig() {

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        EeConfig.Builder eeConfigBuilder = new EeConfig.Builder();

        ServerConfig.Builder serverBuilder = new ServerConfig.Builder();

        Optional<List<String>> serverCfgOpt = cfg.getMapKeys("kumuluzee.server");

        EnvUtils.getEnv(LEGACY_CONTEXT_PATH_ENV, serverBuilder::contextPath);
        EnvUtils.getEnvAsInteger(LEGACY_MIN_THREADS_ENV, serverBuilder::minThreads);
        EnvUtils.getEnvAsInteger(LEGACY_MAX_THREADS_ENV, serverBuilder::maxThreads);

        if (serverCfgOpt.isPresent()) {

            Optional<String> contextPath = cfg.get("kumuluzee.server.context-path");
            Optional<Integer> minThreads = cfg.getInteger("kumuluzee.server.min-threads");
            Optional<Integer> maxThreads = cfg.getInteger("kumuluzee.server.max-threads");
            Optional<Boolean> forceHttps = cfg.getBoolean("kumuluzee.server.force-https");

            contextPath.ifPresent(serverBuilder::contextPath);
            minThreads.ifPresent(serverBuilder::minThreads);
            maxThreads.ifPresent(serverBuilder::maxThreads);
            forceHttps.ifPresent(serverBuilder::forceHttps);

            ServerConnectorConfig.Builder httpBuilder =
                    createServerConnectorConfigBuilder("kumuluzee.server.http",
                            ServerConnectorConfig.Builder.DEFAULT_HTTP_PORT);

            EnvUtils.getEnvAsInteger(PORT_ENV, httpBuilder::port);

            serverBuilder.http(httpBuilder);
            serverBuilder.https(createServerConnectorConfigBuilder("kumuluzee.server.https",
                    ServerConnectorConfig.Builder.DEFAULT_HTTPS_PORT));
        }

        eeConfigBuilder.server(serverBuilder);

        Optional<Integer> dsSizeOpt = cfg.getListSize("kumuluzee.datasources");

        if (dsSizeOpt.isPresent()) {

            for (int i = 0; i < dsSizeOpt.get(); i++) {

                DataSourceConfig.Builder dsc = new DataSourceConfig.Builder();

                Optional<String> jndiName = cfg.get("kumuluzee.datasources[" + i + "].jndi-name");
                Optional<String> driverClass = cfg.get("kumuluzee.datasources[" + i + "].driver-class");
                Optional<String> conUrl = cfg.get("kumuluzee.datasources[" + i + "].connection-url");
                Optional<String> user = cfg.get("kumuluzee.datasources[" + i + "].username");
                Optional<String> pass = cfg.get("kumuluzee.datasources[" + i + "].password");
                Optional<Integer> maxPool = cfg.getInteger("kumuluzee.datasources[" + i + "].max-pool-size");

                jndiName.ifPresent(dsc::jndiName);
                driverClass.ifPresent(dsc::driverClass);
                conUrl.ifPresent(dsc::connectionUrl);
                user.ifPresent(dsc::username);
                pass.ifPresent(dsc::password);
                maxPool.ifPresent(dsc::maxPoolSize);

                eeConfigBuilder.datasource(dsc);
            }
        }

        Optional<Integer> xDsSizeOpt = cfg.getListSize("kumuluzee.xa-datasources");

        if (xDsSizeOpt.isPresent()) {

            for (int i = 0; i < xDsSizeOpt.get(); i++) {

                XaDataSourceConfig.Builder xdsc = new XaDataSourceConfig.Builder();

                Optional<String> jndiName = cfg.get("kumuluzee.xa-datasources[" + i + "].jndi-name");
                Optional<String> xaDatasourceClass = cfg.get("kumuluzee.xa-datasources[" + i + "].xa-datasource-class");
                Optional<String> user = cfg.get("kumuluzee.xa-datasources[" + i + "].username");
                Optional<String> pass = cfg.get("kumuluzee.xa-datasources[" + i + "].password");

                jndiName.ifPresent(xdsc::jndiName);
                xaDatasourceClass.ifPresent(xdsc::xaDatasourceClass);
                user.ifPresent(xdsc::username);
                pass.ifPresent(xdsc::password);

                Optional<List<String>> props = cfg.getMapKeys("kumuluzee.xa-datasources[" + i + "].props");

                if (props.isPresent()) {

                    for (String propName : props.get()) {

                        Optional<String> propValue = cfg.get("kumuluzee.xa-datasources[" + i + "].props." + propName);

                        propValue.ifPresent(v -> xdsc.prop(StringUtils.hyphenCaseToCamelCase(propName), v));
                    }
                }

                eeConfigBuilder.xaDatasource(xdsc);
            }
        }

        PersistenceConfig.Builder persistenceBuilder = new PersistenceConfig.Builder();

        EnvUtils.getEnv(LEGACY_DB_UNIT_ENV, persistenceBuilder::unitName);
        EnvUtils.getEnv(LEGACY_DB_URL_ENV, persistenceBuilder::url);
        EnvUtils.getEnv(LEGACY_DB_USER_ENV, persistenceBuilder::username);
        EnvUtils.getEnv(LEGACY_DB_PASS_ENV, persistenceBuilder::password);

        eeConfigBuilder.persistenceConfig(persistenceBuilder);

        return eeConfigBuilder.build();
    }

    public static Boolean isEeConfigValid(EeConfig eeConfig) {

        return !(eeConfig == null ||
                eeConfig.getServer() == null ||
                eeConfig.getServer().getContextPath() == null ||
                eeConfig.getServer().getForceHttps() == null ||
                eeConfig.getServer().getMinThreads() == null ||
                eeConfig.getServer().getMaxThreads() == null ||
                eeConfig.getServer().getHttp() == null ||
                eeConfig.getServer().getHttps() == null ||
                eeConfig.getServer().getHttp().getPort() == null ||
                eeConfig.getServer().getHttp().getProxyForwarding() == null ||
                eeConfig.getServer().getHttp().getRequestHeaderSize() == null ||
                eeConfig.getServer().getHttp().getResponseHeaderSize() == null ||
                eeConfig.getServer().getHttp().getIdleTimeout() == null ||
                eeConfig.getServer().getHttp().getSoLingerTime() == null ||
                eeConfig.getServer().getHttps().getPort() == null ||
                eeConfig.getServer().getHttps().getProxyForwarding() == null ||
                eeConfig.getServer().getHttps().getRequestHeaderSize() == null ||
                eeConfig.getServer().getHttps().getResponseHeaderSize() == null ||
                eeConfig.getServer().getHttps().getIdleTimeout() == null ||
                eeConfig.getServer().getHttps().getSoLingerTime() == null ||
                eeConfig.getServer().getHttps().getKeystorePath() == null ||
                eeConfig.getServer().getHttps().getKeystorePassword() == null);
    }

    private static ServerConnectorConfig.Builder createServerConnectorConfigBuilder(String prefix, Integer defaultPort) {

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        ServerConnectorConfig.Builder serverConnectorBuilder = new ServerConnectorConfig.Builder();

        serverConnectorBuilder.port(defaultPort);

        EnvUtils.getEnvAsInteger(LEGACY_REQUEST_HEADER_SIZE_ENV, serverConnectorBuilder::requestHeaderSize);
        EnvUtils.getEnvAsInteger(LEGACY_RESPONSE_HEADER_SIZE_ENV, serverConnectorBuilder::responseHeaderSize);

        Optional<List<String>> serverConnectorCfgOpt = cfg.getMapKeys(prefix);

        if (serverConnectorCfgOpt.isPresent()) {

            Optional<Integer> port = cfg.getInteger(prefix + ".port");
            Optional<String> address = cfg.get(prefix + ".address");
            Optional<Boolean> enabled = cfg.getBoolean(prefix + ".enabled");
            Optional<Boolean> proxyForwarding = cfg.getBoolean(prefix + ".proxy-forwarding");
            Optional<Integer> requestHeaderSize = cfg.getInteger(prefix + ".request-header-size");
            Optional<Integer> responseHeaderSize = cfg.getInteger(prefix + ".kresponse-header-size");
            Optional<Integer> idleTimeout = cfg.getInteger(prefix + ".idle-timeout");
            Optional<Integer> soLingerTime = cfg.getInteger(prefix + ".so-linger-time");

            Optional<String> keystorePath = cfg.get(prefix + ".keystore-path");
            Optional<String> keystorePassword = cfg.get(prefix + ".keystore-password");
            Optional<String> keyAlias = cfg.get(prefix + ".key-alias");
            Optional<String> keyPassword = cfg.get(prefix + ".key-password");

            port.ifPresent(serverConnectorBuilder::port);
            address.ifPresent(serverConnectorBuilder::address);
            enabled.ifPresent(serverConnectorBuilder::enabled);
            proxyForwarding.ifPresent(serverConnectorBuilder::proxyForwarding);
            requestHeaderSize.ifPresent(serverConnectorBuilder::requestHeaderSize);
            responseHeaderSize.ifPresent(serverConnectorBuilder::responseHeaderSize);
            idleTimeout.ifPresent(serverConnectorBuilder::idleTimeout);
            soLingerTime.ifPresent(serverConnectorBuilder::soLingerTime);

            keystorePath.ifPresent(serverConnectorBuilder::keystorePath);
            keystorePassword.ifPresent(serverConnectorBuilder::keystorePassword);
            keyAlias.ifPresent(serverConnectorBuilder::keyAlias);
            keyPassword.ifPresent(serverConnectorBuilder::keyPassword);
        }

        return serverConnectorBuilder;
    }
}
