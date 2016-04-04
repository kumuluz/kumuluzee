package com.kumuluz.ee.jsonp;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
@EeComponentDef(name = "JSONP", type = EeComponentType.JSON_P)
public class JsonPComponent implements Component {

    private Logger log = Logger.getLogger(JsonPComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating JSONP");
    }
}
