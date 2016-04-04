package com.kumuluz.ee.websocket.jetty;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.*;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
@EeComponentDef(name = "Jetty", type = EeComponentType.WEBSOCKET)
@EeComponentDependency(value = EeComponentType.SERVLET, implementations = {"Jetty"})
public class WebSocketComponent implements Component {

    private Logger log = Logger.getLogger(WebSocketComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating WebSocket for Jetty");
    }
}
