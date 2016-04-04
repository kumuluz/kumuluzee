package com.kumuluz.ee.common.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class EeConfig {

    private ServerConfig serverConfig = new ServerConfig();

    private List<PersistenceConfig> persistenceConfigs = new ArrayList<>();

    public EeConfig() {

        persistenceConfigs.add(new PersistenceConfig());
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
}
