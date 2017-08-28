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
import com.kumuluz.ee.common.utils.EnvUtils;
import com.kumuluz.ee.common.utils.StringUtils;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        Optional<String> appName = cfg.get("kumuluzee.name");
        Optional<String> appVersion = cfg.get("kumuluzee.version");
        Optional<Boolean> appDebug = cfg.getBoolean("kumuluzee.debug");

        appName.ifPresent(eeConfigBuilder::name);
        appVersion.ifPresent(eeConfigBuilder::version);
        appDebug.ifPresent(eeConfigBuilder::debug);

        ServerConfig.Builder serverBuilder = new ServerConfig.Builder();

        Optional<List<String>> serverCfgOpt = cfg.getMapKeys("kumuluzee.server");

        EnvUtils.getEnv(LEGACY_CONTEXT_PATH_ENV, serverBuilder::contextPath);
        EnvUtils.getEnvAsInteger(LEGACY_MIN_THREADS_ENV, serverBuilder::minThreads);
        EnvUtils.getEnvAsInteger(LEGACY_MAX_THREADS_ENV, serverBuilder::maxThreads);

        if (serverCfgOpt.isPresent()) {

            Optional<String> baseUrl = cfg.get("kumuluzee.server.base-url");
            Optional<String> contextPath = cfg.get("kumuluzee.server.context-path");
            Optional<Boolean> dirBrowsing = cfg.getBoolean("kumuluzee.server.dir-browsing");
            Optional<Integer> minThreads = cfg.getInteger("kumuluzee.server.min-threads");
            Optional<Integer> maxThreads = cfg.getInteger("kumuluzee.server.max-threads");
            Optional<Boolean> forceHttps = cfg.getBoolean("kumuluzee.server.force-https");

            baseUrl.ifPresent(serverBuilder::baseUrl);
            contextPath.ifPresent(serverBuilder::contextPath);
            dirBrowsing.ifPresent(serverBuilder::dirBrowsing);
            minThreads.ifPresent(serverBuilder::minThreads);
            maxThreads.ifPresent(serverBuilder::maxThreads);
            forceHttps.ifPresent(serverBuilder::forceHttps);
        }

        ServerConnectorConfig.Builder httpBuilder =
                createServerConnectorConfigBuilder("kumuluzee.server.http",
                        ServerConnectorConfig.Builder.DEFAULT_HTTP_PORT);

        EnvUtils.getEnvAsInteger(PORT_ENV, httpBuilder::port);

        ServerConnectorConfig.Builder httpsBuilder =
                createServerConnectorConfigBuilder("kumuluzee.server.https",
                        ServerConnectorConfig.Builder.DEFAULT_HTTPS_PORT);

        serverBuilder.http(httpBuilder);
        serverBuilder.https(httpsBuilder);

        eeConfigBuilder.server(serverBuilder);

        Optional<List<String>> envCfgOpt = cfg.getMapKeys("kumuluzee.env");

        if (envCfgOpt.isPresent()) {

            EnvConfig.Builder envBuilder = new EnvConfig.Builder();

            Optional<String> envName = cfg.get("kumuluzee.env.name");

            envName.ifPresent(envBuilder::name);

            eeConfigBuilder.env(envBuilder);
        }

        Optional<List<String>> loaderCfgOpt = cfg.getMapKeys("kumuluzee.dev");

        if (loaderCfgOpt.isPresent()) {

            DevConfig.Builder devBuilder = new DevConfig.Builder();

            Optional<String> webappDir = cfg.get("kumuluzee.dev.webapp-dir");

            webappDir.ifPresent(devBuilder::webappDir);

            eeConfigBuilder.dev(devBuilder);
        }

        Optional<Integer> dsSizeOpt = cfg.getListSize("kumuluzee.datasources");

        if (dsSizeOpt.isPresent()) {

            for (int i = 0; i < dsSizeOpt.get(); i++) {

                DataSourceConfig.Builder dsc = new DataSourceConfig.Builder();

                Optional<String> jndiName = cfg.get("kumuluzee.datasources[" + i + "].jndi-name");
                Optional<String> driverClass = cfg.get("kumuluzee.datasources[" + i + "].driver-class");
                Optional<String> dataSourceClass = cfg.get("kumuluzee.datasources[" + i + "].datasource-class");
                Optional<String> conUrl = cfg.get("kumuluzee.datasources[" + i + "].connection-url");
                Optional<String> user = cfg.get("kumuluzee.datasources[" + i + "].username");
                Optional<String> pass = cfg.get("kumuluzee.datasources[" + i + "].password");
                Optional<Integer> maxPool = cfg.getInteger("kumuluzee.datasources[" + i + "].max-pool-size");

                jndiName.ifPresent(dsc::jndiName);
                driverClass.ifPresent(dsc::driverClass);
                dataSourceClass.ifPresent(dsc::dataSourceClass);
                conUrl.ifPresent(dsc::connectionUrl);
                user.ifPresent(dsc::username);
                pass.ifPresent(dsc::password);
                maxPool.ifPresent(dsc::maxPoolSize);

                Optional<List<String>> pool = cfg.getMapKeys("kumuluzee.datasources[" + i + "].pool");

                if (pool.isPresent()) {

                    DataSourcePoolConfig.Builder dspc = new DataSourcePoolConfig.Builder();

                    Optional<Boolean> autoCommit = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.auto-commit");
                    Optional<Long> connectionTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.connection-timeout");
                    Optional<Long> idleTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.idle-timeout");
                    Optional<Long> maxLifetime = cfg.getLong("kumuluzee.datasources[" + i + "].pool.max-lifetime");
                    Optional<Integer> minIdle = cfg.getInteger("kumuluzee.datasources[" + i + "].pool.min-idle");
                    Optional<Integer> maxSize = cfg.getInteger("kumuluzee.datasources[" + i + "].pool.max-size");
                    Optional<String> poolName = cfg.get("kumuluzee.datasources[" + i + "].pool.name");
                    Optional<Long> initializationFailTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.initialization-fail-timeout");
                    Optional<Boolean> isolateInternalQueries = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.isolate-internal-queries");
                    Optional<Boolean> allowPoolSuspension = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.allow-pool-suspension");
                    Optional<Boolean> readOnly = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.read-only");
                    Optional<Boolean> registerMbeans = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.register-mbeans");
                    Optional<String> connectionInitSql = cfg.get("kumuluzee.datasources[" + i + "].pool.connection-init-sql");
                    Optional<String> transactionIsolation = cfg.get("kumuluzee.datasources[" + i + "].pool.transaction-isolation");
                    Optional<Long> validationTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.validation-timeout");
                    Optional<Long> leakDetectionThreshold = cfg.getLong("kumuluzee.datasources[" + i + "].pool.leak-detection-threshold");

                    autoCommit.ifPresent(dspc::autoCommit);
                    connectionTimeout.ifPresent(dspc::connectionTimeout);
                    idleTimeout.ifPresent(dspc::idleTimeout);
                    maxLifetime.ifPresent(dspc::maxLifetime);
                    minIdle.ifPresent(dspc::minIdle);
                    maxSize.ifPresent(dspc::maxSize);
                    poolName.ifPresent(dspc::name);
                    initializationFailTimeout.ifPresent(dspc::initializationFailTimeout);
                    isolateInternalQueries.ifPresent(dspc::isolateInternalQueries);
                    allowPoolSuspension.ifPresent(dspc::allowPoolSuspension);
                    readOnly.ifPresent(dspc::readOnly);
                    registerMbeans.ifPresent(dspc::registerMbeans);
                    connectionInitSql.ifPresent(dspc::connectionInitSql);
                    transactionIsolation.ifPresent(dspc::transactionIsolation);
                    validationTimeout.ifPresent(dspc::validationTimeout);
                    leakDetectionThreshold.ifPresent(dspc::leakDetectionThreshold);

                    dsc.pool(dspc);
                }

                Optional<List<String>> props = cfg.getMapKeys("kumuluzee.datasources[" + i + "].props");

                if (props.isPresent()) {

                    for (String propName : props.get()) {

                        Optional<String> propValue = cfg.get("kumuluzee.datasources[" + i + "].props." + propName);

                        propValue.ifPresent(v -> dsc.prop(StringUtils.hyphenCaseToCamelCase(propName), v));
                    }
                }

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
                eeConfig.getVersion() == null ||
                eeConfig.getDebug() == null ||
                eeConfig.getEnv() == null ||
                eeConfig.getDev() == null ||
                eeConfig.getServer() == null ||
                eeConfig.getServer().getContextPath() == null ||
                eeConfig.getServer().getForceHttps() == null ||
                eeConfig.getServer().getMinThreads() == null ||
                eeConfig.getServer().getMaxThreads() == null ||
                eeConfig.getServer().getHttp() == null ||
                eeConfig.getServer().getHttps() == null ||
                eeConfig.getServer().getHttp().getPort() == null ||
                eeConfig.getServer().getHttp().getHttp2() == null ||
                eeConfig.getServer().getHttp().getProxyForwarding() == null ||
                eeConfig.getServer().getHttp().getRequestHeaderSize() == null ||
                eeConfig.getServer().getHttp().getResponseHeaderSize() == null ||
                eeConfig.getServer().getHttp().getIdleTimeout() == null ||
                eeConfig.getServer().getHttp().getSoLingerTime() == null ||
                eeConfig.getServer().getHttps().getPort() == null ||
                eeConfig.getServer().getHttps().getHttp2() == null ||
                eeConfig.getServer().getHttps().getProxyForwarding() == null ||
                eeConfig.getServer().getHttps().getRequestHeaderSize() == null ||
                eeConfig.getServer().getHttps().getResponseHeaderSize() == null ||
                eeConfig.getServer().getHttps().getIdleTimeout() == null ||
                eeConfig.getServer().getHttps().getSoLingerTime() == null ||
                eeConfig.getServer().getHttps().getKeystorePath() == null ||
                eeConfig.getServer().getHttps().getKeystorePassword() == null ||
                eeConfig.getDatasources().stream().anyMatch(ds ->
                        (ds == null || ds.getPool() == null ||
                                ds.getPool().getAutoCommit() == null ||
                                ds.getPool().getConnectionTimeout() == null ||
                                ds.getPool().getIdleTimeout() == null ||
                                ds.getPool().getMaxLifetime() == null ||
                                ds.getPool().getMaxSize() == null ||
                                ds.getPool().getInitializationFailTimeout() == null ||
                                ds.getPool().getIsolateInternalQueries() == null ||
                                ds.getPool().getAllowPoolSuspension() == null ||
                                ds.getPool().getReadOnly() == null ||
                                ds.getPool().getRegisterMbeans() == null ||
                                ds.getPool().getValidationTimeout() == null ||
                                ds.getPool().getLeakDetectionThreshold() == null)));
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
            Optional<Boolean> http2 = cfg.getBoolean(prefix + ".http2");
            Optional<Boolean> proxyForwarding = cfg.getBoolean(prefix + ".proxy-forwarding");
            Optional<Integer> requestHeaderSize = cfg.getInteger(prefix + ".request-header-size");
            Optional<Integer> responseHeaderSize = cfg.getInteger(prefix + ".kresponse-header-size");
            Optional<Integer> idleTimeout = cfg.getInteger(prefix + ".idle-timeout");
            Optional<Integer> soLingerTime = cfg.getInteger(prefix + ".so-linger-time");

            Optional<String> keystorePath = cfg.get(prefix + ".keystore-path");
            Optional<String> keystorePassword = cfg.get(prefix + ".keystore-password");
            Optional<String> keyAlias = cfg.get(prefix + ".key-alias");
            Optional<String> keyPassword = cfg.get(prefix + ".key-password");
            Optional<String> sslProtocols = cfg.get(prefix + ".ssl-protocols");
            Optional<String> sslCiphers = cfg.get(prefix + ".ssl-ciphers");

            port.ifPresent(serverConnectorBuilder::port);
            address.ifPresent(serverConnectorBuilder::address);
            enabled.ifPresent(serverConnectorBuilder::enabled);
            http2.ifPresent(serverConnectorBuilder::http2);
            proxyForwarding.ifPresent(serverConnectorBuilder::proxyForwarding);
            requestHeaderSize.ifPresent(serverConnectorBuilder::requestHeaderSize);
            responseHeaderSize.ifPresent(serverConnectorBuilder::responseHeaderSize);
            idleTimeout.ifPresent(serverConnectorBuilder::idleTimeout);
            soLingerTime.ifPresent(serverConnectorBuilder::soLingerTime);

            keystorePath.ifPresent(serverConnectorBuilder::keystorePath);
            keystorePassword.ifPresent(serverConnectorBuilder::keystorePassword);
            keyAlias.ifPresent(serverConnectorBuilder::keyAlias);
            keyPassword.ifPresent(serverConnectorBuilder::keyPassword);
            sslProtocols.ifPresent(p -> serverConnectorBuilder.sslProtocols(Stream.of(p.split(",")).map(String::trim).collect(Collectors.toList())));
            sslCiphers.ifPresent(c -> serverConnectorBuilder.sslCiphers(Stream.of(c.split(",")).map(String::trim).collect(Collectors.toList())));
        }

        return serverConnectorBuilder;
    }
}
