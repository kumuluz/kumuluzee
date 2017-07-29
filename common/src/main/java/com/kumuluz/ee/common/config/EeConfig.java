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

import com.kumuluz.ee.common.wrapper.EeComponentWrapper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeConfig {

    public static class Builder {

        private ServerConfig.Builder server = new ServerConfig.Builder();
        private List<DataSourceConfig.Builder> datasources = new ArrayList<>();
        private List<XaDataSourceConfig.Builder> xaDatasources = new ArrayList<>();

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

        public EeConfig build() {

            List<DataSourceConfig> constructedDatasources =
                    datasources.stream().map(DataSourceConfig.Builder::build).collect(Collectors.toList());

            List<XaDataSourceConfig> constructedXaDatasources =
                    xaDatasources.stream().map(XaDataSourceConfig.Builder::build).collect(Collectors.toList());

            EeConfig eeConfig = new EeConfig();
            eeConfig.server = server.build();
            eeConfig.datasources = Collections.unmodifiableList(constructedDatasources);
            eeConfig.xaDatasources = Collections.unmodifiableList(constructedXaDatasources);

            return eeConfig;
        }
    }

    private ServerConfig server = new ServerConfig();
    private List<DataSourceConfig> datasources = new ArrayList<>();
    private List<XaDataSourceConfig> xaDatasources = new ArrayList<>();

    private List<PersistenceConfig> persistenceConfigs = new ArrayList<>();

    private String version;
    private List<EeComponentWrapper> eeComponents = new ArrayList<>();

    public EeConfig() {
        this.version = ResourceBundle.getBundle("version").getString("version");

        persistenceConfigs.add(new PersistenceConfig());
    }

    public String getVersion() {
        return version;
    }

    public ServerConfig getServer() {
        return server;
    }

    @Deprecated
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
