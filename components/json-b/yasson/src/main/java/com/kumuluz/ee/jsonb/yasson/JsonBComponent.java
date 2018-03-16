package com.kumuluz.ee.jsonb.yasson;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.6.0
 */
@EeComponentDef(name = "Yasson", type = EeComponentType.JSON_B)
@EeComponentDependency(value = EeComponentType.JSON_P)
public class JsonBComponent implements Component {

    private Logger log = Logger.getLogger(JsonBComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Yasson");
    }
}
