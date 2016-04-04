package com.kumuluz.ee.beanvalidation;

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
@EeComponentDef(name = "Hibernate Validator", type = EeComponentType.BEAN_VALIDATION)
public class BeanValidationComponent implements Component {

    private Logger log = Logger.getLogger(BeanValidationComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServerWrapper server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Hibernate Validator");
    }
}
