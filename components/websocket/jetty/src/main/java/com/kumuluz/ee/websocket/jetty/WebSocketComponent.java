package com.kumuluz.ee.websocket.jetty;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 */
public class WebSocketComponent implements Component {

    private Logger log = Logger.getLogger(WebSocketComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating WebSocket for Jetty");
    }

    @Override
    public String getComponentName() {

        return "WebSocket";
    }

    @Override
    public String getImplementationName() {

        return "Jetty";
    }
}
