package com.kumuluz.ee.common;

import com.kumuluz.ee.common.config.ServerConfig;

/**
 * @author Tilen
 */
public interface ServletServer extends KumuluzServer {

    void initWebContext();
}
