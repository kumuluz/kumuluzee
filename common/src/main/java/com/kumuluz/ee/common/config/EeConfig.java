package com.kumuluz.ee.common.config;

import java.util.Optional;

/**
 * @author Tilen
 */
public class EeConfig {

    private ServerConfig serverConfig = new ServerConfig();

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }
}
