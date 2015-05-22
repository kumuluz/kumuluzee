package com.kumuluz.ee.common;

import com.kumuluz.ee.common.config.ServerConfig;

/**
 * @author Tilen
 */
public interface ServletServer extends KumuluzServer {

    void initServer();

    void startServer();

    void stopServer();

    void initWebContext();

    void setServerConfig(ServerConfig serverConfig);

    ServerConfig getServerConfig();
}
