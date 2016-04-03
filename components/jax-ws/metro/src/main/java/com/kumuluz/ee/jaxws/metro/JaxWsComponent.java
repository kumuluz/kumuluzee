package com.kumuluz.ee.jaxws.metro;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
@EeComponentDef(name = "Metro", type = EeComponentType.JAX_WS)
public class JaxWsComponent implements Component {

    private Logger log = Logger.getLogger(JaxWsComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {

    }

    @Override
    public void load() {
        log.info("Initiating Metro");
    }
}
