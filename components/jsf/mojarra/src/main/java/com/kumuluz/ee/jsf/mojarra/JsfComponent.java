package com.kumuluz.ee.jsf.mojarra;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
@EeComponentDef(name = "Mojarra", type = EeComponentType.JSF)
@EeComponentDependency(value = EeComponentType.SERVLET)
public class JsfComponent implements Component {

    private Logger log = Logger.getLogger(JsfComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Mojarra");
    }
}
