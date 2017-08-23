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

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeConfig {

    public static class Builder {

        private String name;
        private String version = "1.0.0";

        private EnvConfig.Builder env = new EnvConfig.Builder();
        private DevConfig.Builder dev = new DevConfig.Builder();
        private ServerConfig.Builder server = new ServerConfig.Builder();
        private List<DataSourceConfig.Builder> datasources = new ArrayList<>();
        private List<XaDataSourceConfig.Builder> xaDatasources = new ArrayList<>();

        private PersistenceConfig.Builder persistenceConfig = new PersistenceConfig.Builder();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder version(String version) {
            this.version = version;
            return this;
        }

        public Builder env(EnvConfig.Builder env) {
            this.env = env;
            return this;
        }

        public Builder dev(DevConfig.Builder dev) {
            this.dev = dev;
            return this;
        }

        public Builder server(ServerConfig.Builder server) {
            this.server = server;
            return this;
        }

        public Builder datasource(DataSourceConfig.Builder datasource) {
            this.datasources.add(datasource);
            return this;
        }

        public Builder xaDatasource(XaDataSourceConfig.Builder xaDatasource) {
            this.xaDatasources.add(xaDatasource);
            return this;
        }

        public Builder persistenceConfig(PersistenceConfig.Builder persistenceConfig) {
            this.persistenceConfig = persistenceConfig;
            return this;
        }

        public EeConfig build() {

            List<DataSourceConfig> constructedDatasources =
                    datasources.stream().map(DataSourceConfig.Builder::build).collect(Collectors.toList());

            List<XaDataSourceConfig> constructedXaDatasources =
                    xaDatasources.stream().map(XaDataSourceConfig.Builder::build).collect(Collectors.toList());

            EeConfig eeConfig = new EeConfig();
            eeConfig.name = name;
            eeConfig.version = version;
            eeConfig.env = env.build();
            eeConfig.dev = dev.build();
            eeConfig.server = server.build();
            eeConfig.datasources = Collections.unmodifiableList(constructedDatasources);
            eeConfig.xaDatasources = Collections.unmodifiableList(constructedXaDatasources);

            eeConfig.persistenceConfig = persistenceConfig.build();

            return eeConfig;
        }
    }

    private static EeConfig instance;

    private String name;
    private String version;

    private EnvConfig env;
    private DevConfig dev;
    private ServerConfig server;
    private List<DataSourceConfig> datasources;
    private List<XaDataSourceConfig> xaDatasources;

    private PersistenceConfig persistenceConfig;

    private EeConfig() {
    }

    public static void initialize(EeConfig eeConfig) {

        if (instance != null) {
            throw new IllegalStateException("The EeConfig was already initialized.");
        }

        instance = eeConfig;
    }

    public static EeConfig getInstance() {

        if (instance == null) {
            throw new IllegalStateException("The EeConfig was not yet initialized.");
        }

        return instance;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public EnvConfig getEnv() {
        return env;
    }

    public DevConfig getDev() {
        return dev;
    }

    public ServerConfig getServer() {
        return server;
    }

    public List<DataSourceConfig> getDatasources() {
        return datasources;
    }

    public List<XaDataSourceConfig> getXaDatasources() {
        return xaDatasources;
    }

    @Deprecated
    public PersistenceConfig getPersistenceConfig() {
        return persistenceConfig;
    }
}
