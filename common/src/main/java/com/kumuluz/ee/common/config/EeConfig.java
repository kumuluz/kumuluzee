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
import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import java.util.*;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeConfig {

    private String version;

    private ServerConfig serverConfig = new ServerConfig();
    private List<PersistenceConfig> persistenceConfigs = new ArrayList<>();
    private List<DataSourceConfig> datasources = new ArrayList<>();

    private List<EeComponentWrapper> eeComponents = new ArrayList<>();

    public EeConfig() {
        this.version = ResourceBundle.getBundle("version").getString("version");

        persistenceConfigs.add(new PersistenceConfig());

        ConfigurationUtil cfg = ConfigurationUtil.getInstance();

        Optional<Integer> dsSizeOpt = cfg.getListSize("kumuluzee.datasources");

        if (dsSizeOpt.isPresent()) {
            Integer dsSize = dsSizeOpt.get();

            for (int i = 0; i < dsSize; i++) {

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

    public List<EeComponentWrapper> getEeComponents() {
        return eeComponents;
    }
}
