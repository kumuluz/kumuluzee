package com.kumuluz.ee.jaxrs;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.dependencies.EeComponentDef;
import com.kumuluz.ee.common.dependencies.EeComponentDependency;
import com.kumuluz.ee.common.dependencies.EeComponentType;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
@EeComponentDef(name = "Jersey", type = EeComponentType.JAX_RS)
@EeComponentDependency(value = EeComponentType.SERVLET)
public class JaxRsComponent implements Component {

    private Logger log = Logger.getLogger(JaxRsComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Jersey");
    }
}
