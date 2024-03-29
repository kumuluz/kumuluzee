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
import com.kumuluz.ee.common.utils.StringUtils;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class EeConfigFactory {

    // ordered by priority, first entry has the highest priority
    private static final List<String> PORT_ENVIRONMENT_VARIABLE_NAMES = List.of(
            "PORT",
            "FUNCTIONS_CUSTOMHANDLER_PORT"
    );

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

        if (serverCfgOpt.isPresent()) {

            Optional<String> baseUrl = cfg.get("kumuluzee.server.base-url");
            Optional<String> contextPath = cfg.get("kumuluzee.server.context-path");
            Optional<Boolean> dirBrowsing = cfg.getBoolean("kumuluzee.server.dir-browsing");
            Optional<Boolean> etags = cfg.getBoolean("kumuluzee.server.etags");
            Optional<Integer> minThreads = cfg.getInteger("kumuluzee.server.min-threads");
            Optional<Integer> maxThreads = cfg.getInteger("kumuluzee.server.max-threads");
            Optional<Boolean> forceHttps = cfg.getBoolean("kumuluzee.server.force-https");
            Optional<Boolean> showServerInfo = cfg.getBoolean("kumuluzee.server.show-server-info");
            Optional<Boolean> forwardStartupException = cfg.getBoolean("kumuluzee.server.jetty.forward-startup-exception");

            baseUrl.ifPresent(serverBuilder::baseUrl);
            contextPath.ifPresent(serverBuilder::contextPath);
            dirBrowsing.ifPresent(serverBuilder::dirBrowsing);
            etags.ifPresent(serverBuilder::etags);
            minThreads.ifPresent(serverBuilder::minThreads);
            maxThreads.ifPresent(serverBuilder::maxThreads);
            forceHttps.ifPresent(serverBuilder::forceHttps);
            showServerInfo.ifPresent(serverBuilder::showServerInfo);
            forwardStartupException.ifPresent(serverBuilder::forwardStartupException);
        }

        ServerConnectorConfig.Builder httpBuilder =
                createServerConnectorConfigBuilder("kumuluzee.server.http",
                        ServerConnectorConfig.DEFAULT_HTTP_PORT);

        PORT_ENVIRONMENT_VARIABLE_NAMES.stream()
                .map(System::getenv)
                .filter(Objects::nonNull)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .findFirst()
                .ifPresent(httpBuilder::port);

        ServerConnectorConfig.Builder httpsBuilder =
                createServerConnectorConfigBuilder("kumuluzee.server.https",
                        ServerConnectorConfig.DEFAULT_HTTPS_PORT);

        serverBuilder.http(httpBuilder);
        serverBuilder.https(httpsBuilder);

        GzipConfig.Builder gzipBuilder =
                createGzipConfigBuilder("kumuluzee.server.gzip");

        serverBuilder.gzip(gzipBuilder);

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
            Optional<Boolean> runningTests = cfg.getBoolean("kumuluzee.dev.running-tests");

            webappDir.ifPresent(devBuilder::webappDir);
            runningTests.ifPresent(devBuilder::runningTests);

            getConfigList("kumuluzee.dev.scan-libraries").ifPresent(devBuilder::scanLibraries);

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

                jndiName.ifPresent(dsc::jndiName);
                driverClass.ifPresent(dsc::driverClass);
                dataSourceClass.ifPresent(dsc::dataSourceClass);
                conUrl.ifPresent(dsc::connectionUrl);
                user.ifPresent(dsc::username);
                pass.ifPresent(dsc::password);

                Optional<List<String>> pool = cfg.getMapKeys("kumuluzee.datasources[" + i + "].pool");

                if (pool.isPresent()) {

                    DataSourcePoolConfig.Builder dspc = new DataSourcePoolConfig.Builder();

                    Optional<Boolean> autoCommit = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.auto-commit");
                    Optional<Boolean> flushOnClose = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.flush-on-close");
                    Optional<Long> connectionTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.connection-timeout");
                    Optional<Long> idleTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.idle-timeout");
                    Optional<Long> maxLifetime = cfg.getLong("kumuluzee.datasources[" + i + "].pool.max-lifetime");
                    Optional<Integer> minIdle = cfg.getInteger("kumuluzee.datasources[" + i + "].pool.min-idle");
                    Optional<Integer> initialSize = cfg.getInteger("kumuluzee.datasources[" + i + "].pool.initial-size");
                    Optional<Integer> minSize = cfg.getInteger("kumuluzee.datasources[" + i + "].pool.min-size");
                    Optional<Integer> maxSize = cfg.getInteger("kumuluzee.datasources[" + i + "].pool.max-size");
                    Optional<String> poolName = cfg.get("kumuluzee.datasources[" + i + "].pool.name");
                    Optional<Long> initializationFailTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool" +
                            ".initialization-fail-timeout");
                    Optional<Boolean> isolateInternalQueries = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool" +
                            ".isolate-internal-queries");
                    Optional<Boolean> allowPoolSuspension = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.allow-pool-suspension");
                    Optional<Boolean> readOnly = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.read-only");
                    Optional<Boolean> registerMbeans = cfg.getBoolean("kumuluzee.datasources[" + i + "].pool.register-mbeans");
                    Optional<String> connectionInitSql = cfg.get("kumuluzee.datasources[" + i + "].pool.connection-init-sql");
                    Optional<String> connectionValidSql = cfg.get("kumuluzee.datasources[" + i + "].pool.connection-valid-sql");
                    Optional<String> transactionIsolation = cfg.get("kumuluzee.datasources[" + i + "].pool.transaction-isolation");
                    Optional<Long> validationTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.validation-timeout");
                    Optional<Long> leakDetectionThreshold = cfg.getLong("kumuluzee.datasources[" + i + "].pool.leak-detection-threshold");
                    Optional<Long> idleValidationTimeout = cfg.getLong("kumuluzee.datasources[" + i + "].pool.idle-validation-timeout");

                    autoCommit.ifPresent(dspc::autoCommit);
                    flushOnClose.ifPresent(dspc::flushOnClose);
                    connectionTimeout.ifPresent(dspc::connectionTimeout);
                    idleTimeout.ifPresent(dspc::idleTimeout);
                    maxLifetime.ifPresent(dspc::maxLifetime);
                    minIdle.ifPresent(dspc::minIdle);
                    initialSize.ifPresent(dspc::initialSize);
                    minSize.ifPresent(dspc::minSize);
                    maxSize.ifPresent(dspc::maxSize);
                    poolName.ifPresent(dspc::name);
                    initializationFailTimeout.ifPresent(dspc::initializationFailTimeout);
                    isolateInternalQueries.ifPresent(dspc::isolateInternalQueries);
                    allowPoolSuspension.ifPresent(dspc::allowPoolSuspension);
                    readOnly.ifPresent(dspc::readOnly);
                    registerMbeans.ifPresent(dspc::registerMbeans);
                    connectionInitSql.ifPresent(dspc::connectionInitSql);
                    connectionValidSql.ifPresent(dspc::connectionValidSql);
                    transactionIsolation.ifPresent(dspc::transactionIsolation);
                    validationTimeout.ifPresent(dspc::validationTimeout);
                    leakDetectionThreshold.ifPresent(dspc::leakDetectionThreshold);
                    idleValidationTimeout.ifPresent(dspc::idleValidationTimeout);

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

                Optional<List<String>> pool = cfg.getMapKeys("kumuluzee.xa-datasources[" + i + "].pool");

                if (pool.isPresent()) {

                    DataSourcePoolConfig.Builder dspc = new DataSourcePoolConfig.Builder();

                    Optional<Boolean> autoCommit = cfg.getBoolean("kumuluzee.xa-datasources[" + i + "].pool.auto-commit");
                    Optional<Boolean> flushOnClose = cfg.getBoolean("kumuluzee.xa-datasources[" + i + "].pool.flush-on-close");
                    Optional<Long> connectionTimeout = cfg.getLong("kumuluzee.xa-datasources[" + i + "].pool.connection-timeout");
                    Optional<Long> idleTimeout = cfg.getLong("kumuluzee.xa-datasources[" + i + "].pool.idle-timeout");
                    Optional<Long> maxLifetime = cfg.getLong("kumuluzee.xa-datasources[" + i + "].pool.max-lifetime");
                    Optional<Integer> minIdle = cfg.getInteger("kumuluzee.xa-datasources[" + i + "].pool.min-idle");
                    Optional<Integer> initialSize = cfg.getInteger("kumuluzee.xa-datasources[" + i + "].pool.initial-size");
                    Optional<Integer> minSize = cfg.getInteger("kumuluzee.xa-datasources[" + i + "].pool.min-size");
                    Optional<Integer> maxSize = cfg.getInteger("kumuluzee.xa-datasources[" + i + "].pool.max-size");
                    Optional<String> poolName = cfg.get("kumuluzee.xa-datasources[" + i + "].pool.name");
                    Optional<Long> initializationFailTimeout = cfg.getLong("kumuluzee.xa-datasources[" + i + "].pool" +
                            ".initialization-fail-timeout");
                    Optional<Boolean> isolateInternalQueries = cfg.getBoolean("kumuluzee.xa-datasources[" + i + "].pool" +
                            ".isolate-internal-queries");
                    Optional<Boolean> allowPoolSuspension = cfg.getBoolean("kumuluzee.xa-datasources[" + i + "].pool.allow-pool-suspension");
                    Optional<Boolean> readOnly = cfg.getBoolean("kumuluzee.xa-datasources[" + i + "].pool.read-only");
                    Optional<Boolean> registerMbeans = cfg.getBoolean("kumuluzee.xa-datasources[" + i + "].pool.register-mbeans");
                    Optional<String> connectionInitSql = cfg.get("kumuluzee.xa-datasources[" + i + "].pool.connection-init-sql");
                    Optional<String> transactionIsolation = cfg.get("kumuluzee.xa-datasources[" + i + "].pool.transaction-isolation");
                    Optional<Long> validationTimeout = cfg.getLong("kumuluzee.xa-datasources[" + i + "].pool.validation-timeout");
                    Optional<Long> leakDetectionThreshold = cfg.getLong("kumuluzee.xa-datasources[" + i + "].pool.leak-detection-threshold");
                    Optional<Long> idleValidationTimeout = cfg.getLong("kumuluzee.xa-datasources[" + i + "].pool.idle-validation-timeout");

                    autoCommit.ifPresent(dspc::autoCommit);
                    flushOnClose.ifPresent(dspc::flushOnClose);
                    connectionTimeout.ifPresent(dspc::connectionTimeout);
                    idleTimeout.ifPresent(dspc::idleTimeout);
                    maxLifetime.ifPresent(dspc::maxLifetime);
                    minIdle.ifPresent(dspc::minIdle);
                    initialSize.ifPresent(dspc::initialSize);
                    minSize.ifPresent(dspc::minSize);
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
                    idleValidationTimeout.ifPresent(dspc::idleValidationTimeout);

                    xdsc.pool(dspc);
                }

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

        Optional<Integer> mailSessionsSizeOpt = cfg.getListSize("kumuluzee.mail-sessions");

        if (mailSessionsSizeOpt.isPresent()) {

            for (int i = 0; i < mailSessionsSizeOpt.get(); i++) {

                String prefix = "kumuluzee.mail-sessions[" + i + "]";

                MailSessionConfig.Builder mscc = new MailSessionConfig.Builder();

                Optional<String> jndiName = cfg.get(prefix + ".jndi-name");
                Optional<Boolean> debug = cfg.getBoolean(prefix + ".debug");

                jndiName.ifPresent(mscc::jndiName);
                debug.ifPresent(mscc::debug);

                Optional<List<String>> transport = cfg.getMapKeys(prefix + ".transport");
                Optional<List<String>> store = cfg.getMapKeys(prefix + ".store");

                if (transport.isPresent()) {

                    mscc.transport(createMailServiceConfigBuilder(prefix + ".transport"));
                }

                if (store.isPresent()) {

                    mscc.store(createMailServiceConfigBuilder(prefix + ".store"));
                }

                Optional<List<String>> props = cfg.getMapKeys(prefix + ".props");

                if (props.isPresent()) {

                    for (String propName : props.get()) {

                        Optional<String> propValue = cfg.get(prefix + ".props." + propName);

                        propValue.ifPresent(v -> mscc.prop(propName, v));
                    }
                }

                eeConfigBuilder.mailSession(mscc);
            }
        }

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
                eeConfig.getServer().getShowServerInfo() == null ||
                eeConfig.getServer().getForwardStartupException() == null ||
                eeConfig.getServer().getHttp() == null ||
                eeConfig.getServer().getHttp().getHttp2() == null ||
                eeConfig.getServer().getHttp().getProxyForwarding() == null ||
                eeConfig.getServer().getHttp().getRequestHeaderSize() == null ||
                eeConfig.getServer().getHttp().getResponseHeaderSize() == null ||
                eeConfig.getServer().getHttp().getIdleTimeout() == null ||
                (eeConfig.getServer().getHttps() != null &&
                        (eeConfig.getServer().getHttps().getHttp2() == null ||
                                eeConfig.getServer().getHttps().getProxyForwarding() == null ||
                                eeConfig.getServer().getHttps().getRequestHeaderSize() == null ||
                                eeConfig.getServer().getHttps().getResponseHeaderSize() == null ||
                                eeConfig.getServer().getHttps().getIdleTimeout() == null)) ||
                eeConfig.getDatasources().stream().anyMatch(ds ->
                        (ds == null || ds.getPool() == null ||
                                ds.getPool().getAutoCommit() == null ||
                                ds.getPool().getConnectionTimeout() == null ||
                                ds.getPool().getIdleTimeout() == null ||
                                ds.getPool().getMaxLifetime() == null ||
                                ds.getPool().getMaxSize() == null ||
                                ds.getPool().getValidationTimeout() == null ||
                                ds.getPool().getLeakDetectionThreshold() == null)));
    }

    private static ServerConnectorConfig.Builder createServerConnectorConfigBuilder(String prefix, Integer defaultPort) {

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        ServerConnectorConfig.Builder serverConnectorBuilder = new ServerConnectorConfig.Builder();

        serverConnectorBuilder.port(defaultPort);

        Optional<List<String>> serverConnectorCfgOpt = cfg.getMapKeys(prefix);

        if (serverConnectorCfgOpt.isPresent()) {

            Optional<Integer> port = cfg.getInteger(prefix + ".port");
            Optional<String> address = cfg.get(prefix + ".address");
            Optional<Boolean> enabled = cfg.getBoolean(prefix + ".enabled");
            Optional<Boolean> http2 = cfg.getBoolean(prefix + ".http2");
            Optional<Boolean> proxyForwarding = cfg.getBoolean(prefix + ".proxy-forwarding");
            Optional<Integer> requestHeaderSize = cfg.getInteger(prefix + ".request-header-size");
            Optional<Integer> responseHeaderSize = cfg.getInteger(prefix + ".response-header-size");
            Optional<Integer> idleTimeout = cfg.getInteger(prefix + ".idle-timeout");

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

            keystorePath.ifPresent(serverConnectorBuilder::keystorePath);
            keystorePassword.ifPresent(serverConnectorBuilder::keystorePassword);
            keyAlias.ifPresent(serverConnectorBuilder::keyAlias);
            keyPassword.ifPresent(serverConnectorBuilder::keyPassword);
            sslProtocols.ifPresent(p -> serverConnectorBuilder.sslProtocols(Stream.of(p.split(",")).map(String::trim).collect(Collectors
                    .toList())));
            sslCiphers.ifPresent(c -> serverConnectorBuilder.sslCiphers(Stream.of(c.split(",")).map(String::trim).collect(Collectors
                    .toList())));
        }

        return serverConnectorBuilder;
    }

    private static MailServiceConfig.Builder createMailServiceConfigBuilder(String prefix) {

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        MailServiceConfig.Builder mailServiceBuilder = new MailServiceConfig.Builder();

        Optional<String> protocol = cfg.get(prefix + ".protocol");
        Optional<String> host = cfg.get(prefix + ".host");
        Optional<Integer> port = cfg.getInteger(prefix + ".port");
        Optional<Boolean> starttls = cfg.getBoolean(prefix + ".starttls");
        Optional<String> username = cfg.get(prefix + ".username");
        Optional<String> password = cfg.get(prefix + ".password");
        Optional<Long> connectionTimeout = cfg.getLong(prefix + ".connection-timeout");
        Optional<Long> timeout = cfg.getLong(prefix + ".timeout");

        protocol.ifPresent(mailServiceBuilder::protocol);
        host.ifPresent(mailServiceBuilder::host);
        port.ifPresent(mailServiceBuilder::port);
        starttls.ifPresent(mailServiceBuilder::starttls);
        username.ifPresent(mailServiceBuilder::username);
        password.ifPresent(mailServiceBuilder::password);
        connectionTimeout.ifPresent(mailServiceBuilder::connectionTimeout);
        timeout.ifPresent(mailServiceBuilder::timeout);

        return mailServiceBuilder;
    }

    private static GzipConfig.Builder createGzipConfigBuilder(String prefix) {

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        GzipConfig.Builder gzipBuilder = new GzipConfig.Builder();

        Optional<Boolean> enabled = cfg.getBoolean(prefix + ".enabled");
        Optional<Integer> minGzipSize = cfg.getInteger(prefix + ".min-gzip-size");
        Optional<List<String>> includedMethods = getConfigList(prefix + ".included-methods");
        Optional<List<String>> includedMimeTypes = getConfigList(prefix + ".included-mime-types");
        Optional<List<String>> excludedMimeTypes = getConfigList(prefix + ".excluded-mime-types");
        Optional<List<String>> excludedAgentPatterns = getConfigList(prefix + ".excluded-agent-patterns");
        Optional<List<String>> excludedPaths = getConfigList(prefix + ".excluded-paths");
        Optional<List<String>> includedPaths = getConfigList(prefix + ".included-paths");

        enabled.ifPresent(gzipBuilder::enabled);
        minGzipSize.ifPresent(gzipBuilder::minGzipSize);
        includedMethods.ifPresent(gzipBuilder::includedMethods);
        includedMimeTypes.ifPresent(gzipBuilder::includedMimeTypes);
        excludedMimeTypes.ifPresent(gzipBuilder::excludedMimeTypes);
        excludedAgentPatterns.ifPresent(gzipBuilder::excludedAgentPatterns);
        excludedPaths.ifPresent(gzipBuilder::excludedPaths);
        includedPaths.ifPresent(gzipBuilder::includedPaths);

        return gzipBuilder;
    }

    private static Optional<List<String>> getConfigList(String key) {
        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        Optional<Integer> listSize = cfg.getListSize(key);

        if (listSize.isPresent()) {
            List<String> list = new ArrayList<>();

            for (int i = 0; i < listSize.get(); i++) {
                Optional<String> item = cfg.get(key + "[" + i + "]");

                item.ifPresent(list::add);
            }

            if (list.size() > 0) {
                return Optional.of(Collections.unmodifiableList(list));
            }
        }

        return Optional.empty();
    }
}
