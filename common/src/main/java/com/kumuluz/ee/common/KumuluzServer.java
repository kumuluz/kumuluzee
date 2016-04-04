package com.kumuluz.ee.common;

import com.kumuluz.ee.common.config.ServerConfig;

import java.util.EventListener;
import java.util.Map;

import javax.servlet.Servlet;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public interface KumuluzServer {

    void initServer();

    void startServer();

    void stopServer();

    void setServerConfig(ServerConfig serverConfig);

    ServerConfig getServerConfig();
}
