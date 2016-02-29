package com.kumuluz.ee.websocket.tyrus;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 */
public class WebsocketComponent implements Component {

    private Logger log = Logger.getLogger(WebsocketComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Tyrus");
    }

    @Override
    public String getComponentName() {
        return "Websocket";
    }

    @Override
    public String getImplementationName() {
        return "Tyrus";
    }
}
