package com.kumuluz.ee.jsf.mojarra;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentType;

import java.util.logging.Logger;

/**
 * @author Tilen
 */
@EeComponentDef(name = "Mojarra", type = EeComponentType.JSF)
public class JsfComponent implements Component {

    private Logger log = Logger.getLogger(JsfComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Mojarra");
    }
}
