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
package com.kumuluz.ee.common.config;

import com.kumuluz.ee.common.utils.StringUtils;
import com.kumuluz.ee.common.wrapper.EeComponentWrapper;
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeConfig {

    private String version;

    private ServerConfig serverConfig = new ServerConfig();
    private List<PersistenceConfig> persistenceConfigs = new ArrayList<>();
    private List<DataSourceConfig> datasources = new ArrayList<>();
    private List<XaDataSourceConfig> xaDatasources = new ArrayList<>();

    private List<EeComponentWrapper> eeComponents = new ArrayList<>();

    public void init() {
        this.version = ResourceBundle.getBundle("version").getString("version");

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        Optional<List<String>> serverCfgOpt = cfg.getMapKeys("kumuluzee.server");
        if (serverCfgOpt.isPresent()) {
            serverConfig = new ServerConfig();

            Optional<Integer> port = cfg.getInteger("kumuluzee.server.port");
            Optional<Integer> minThreads = cfg.getInteger("kumuluzee.server.min-threads");
            Optional<Integer> maxThreads = cfg.getInteger("kumuluzee.server.max-threads");
            Optional<Integer> requestHeaderSize = cfg.getInteger("kumuluzee.server.request-header-size");
            Optional<Integer> responseHeaderSize = cfg.getInteger("kumuluzee.server.response-header-size");
            Optional<String> contextPath = cfg.get("kumuluzee.server.context-path");

            Optional<Integer> sslPort = cfg.getInteger("kumuluzee.server.ssl-port");
            Optional<String> keystorePath = cfg.get("kumuluzee.server.keystore-path");
            Optional<String> keystorePassword = cfg.get("kumuluzee.server.keystore-password");
            Optional<String> keymanagerPassword = cfg.get("kumuluzee.server.keymanager-password");
            Optional<Boolean> enableSSL = cfg.getBoolean("kumuluzee.server.enable-ssl");
            Optional<Boolean> forceSSL = cfg.getBoolean("kumuluzee.server.force-ssl");

            port.ifPresent(serverConfig::setPort);
            minThreads.ifPresent(serverConfig::setMinThreads);
            maxThreads.ifPresent(serverConfig::setMaxThreads);
            requestHeaderSize.ifPresent(serverConfig::setRequestHeaderSize);
            responseHeaderSize.ifPresent(serverConfig::setResponseHeaderSize);
            contextPath.ifPresent(serverConfig::setContextPath);

            sslPort.ifPresent(serverConfig::setSSLPort);
            keystorePath.ifPresent(serverConfig::setKeystorePath);
            keystorePassword.ifPresent(serverConfig::setKeystorePassword);
            keymanagerPassword.ifPresent(serverConfig::setKeyManagerPassword);
            enableSSL.ifPresent(serverConfig::setEnableSSL);
            forceSSL.ifPresent(serverConfig::setForceSSL);
        }

        persistenceConfigs.add(new PersistenceConfig());

        Optional<Integer> dsSizeOpt = cfg.getListSize("kumuluzee.datasources");

        if (dsSizeOpt.isPresent()) {

            for (int i = 0; i < dsSizeOpt.get(); i++) {

                DataSourceConfig dsc = new DataSourceConfig();

                Optional<String> jndiName = cfg.get("kumuluzee.datasources[" + i + "].jndi-name");
                Optional<String> driverClass = cfg.get("kumuluzee.datasources[" + i + "].driver-class");
                Optional<String> conUrl = cfg.get("kumuluzee.datasources[" + i + "].connection-url");
                Optional<String> user = cfg.get("kumuluzee.datasources[" + i + "].username");
                Optional<String> pass = cfg.get("kumuluzee.datasources[" + i + "].password");
                Optional<Integer> maxPool = cfg.getInteger("kumuluzee.datasources[" + i + "].max-pool-size");

                jndiName.ifPresent(dsc::setJndiName);
                driverClass.ifPresent(dsc::setDriverClass);
                conUrl.ifPresent(dsc::setConnectionUrl);
                user.ifPresent(dsc::setUsername);
                pass.ifPresent(dsc::setPassword);
                maxPool.ifPresent(dsc::setMaxPoolSize);

                datasources.add(dsc);
            }
        }

        Optional<Integer> xDsSizeOpt = cfg.getListSize("kumuluzee.xa-datasources");

        if (xDsSizeOpt.isPresent()) {

            for (int i = 0; i < xDsSizeOpt.get(); i++) {

                XaDataSourceConfig xdsc = new XaDataSourceConfig();

                Optional<String> jndiName = cfg.get("kumuluzee.xa-datasources[" + i + "].jndi-name");
                Optional<String> xaDatasourceClass = cfg.get("kumuluzee.xa-datasources[" + i + "].xa-datasource-class");
                Optional<String> user = cfg.get("kumuluzee.xa-datasources[" + i + "].username");
                Optional<String> pass = cfg.get("kumuluzee.xa-datasources[" + i + "].password");

                jndiName.ifPresent(xdsc::setJndiName);
                xaDatasourceClass.ifPresent(xdsc::setXaDatasourceClass);
                user.ifPresent(xdsc::setUsername);
                pass.ifPresent(xdsc::setPassword);

                Optional<List<String>> props = cfg.getMapKeys("kumuluzee.xa-datasources[" + i + "].props");

                if (props.isPresent()) {

                    for (String propName : props.get()) {

                        Optional<String> propValue = cfg.get("kumuluzee.xa-datasources[" + i + "].props." + propName);

                        propValue.ifPresent(v -> xdsc.getProps().put(StringUtils.hyphenCaseToCamelCase(propName), v));
                    }
                }

                xaDatasources.add(xdsc);
            }
        }
    }

    public String getVersion() {
        return version;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public List<PersistenceConfig> getPersistenceConfigs() {
        return persistenceConfigs;
    }

    public List<DataSourceConfig> getDatasources() {
        return datasources;
    }

    public List<XaDataSourceConfig> getXaDatasources() {
        return xaDatasources;
    }

    public List<EeComponentWrapper> getEeComponents() {
        return eeComponents;
    }
}
