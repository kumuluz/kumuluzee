package com.kumuluz.ee.jaxrs;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;

import java.util.logging.Logger;

/**
 * @author Tilen
 */
public class JaxRsComponent implements Component {

    private Logger log = Logger.getLogger(JaxRsComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Jersey");
    }
}
