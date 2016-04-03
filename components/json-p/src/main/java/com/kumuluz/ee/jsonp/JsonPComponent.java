package com.kumuluz.ee.jsonp;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;

import java.util.logging.Logger;

/**
 * @author Tilen
 */
@EeComponentDef(EeComponentType.JSON_P)
public class JsonPComponent implements Component {

    private Logger log = Logger.getLogger(JsonPComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating JSONP");
    }

    @Override
    public String getImplementationName() {

        return "JSONP";
    }
}
