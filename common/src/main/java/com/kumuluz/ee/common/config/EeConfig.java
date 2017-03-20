package com.kumuluz.ee.common.config;

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

    public void setVersion(String version) {
        this.version = version;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public List<PersistenceConfig> getPersistenceConfigs() {
        return persistenceConfigs;
    }

    public List<DataSourceConfig> getDatasources() {
        return datasources;
    }
}
