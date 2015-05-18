package com.kumuluz.ee.common;

/**
 * @author Tilen
 */
public interface ServletServer {

    void initServer();

    void startServer();

    void stopServer();

    void initWebContext();

    String getServerName();
}
