package com.kumuluz.ee.beanvalidation;

import com.kumuluz.ee.common.Component;
import com.kumuluz.ee.common.KumuluzServer;
import com.kumuluz.ee.common.config.EeConfig;
import org.kohsuke.MetaInfServices;

import java.util.logging.Logger;

/**
 * @author Tilen
 */
@MetaInfServices
public class BeanValidationComponent implements Component {

    private Logger log = Logger.getLogger(BeanValidationComponent.class.getSimpleName());

    @Override
    public void init(KumuluzServer server, EeConfig eeConfig) {
    }

    @Override
    public void load() {

        log.info("Initiating Hibernate Validator");
    }

    @Override
    public String getComponentName() {

        return "Bean Validation";
    }

    @Override
    public String getImplementationName() {

        return "Hibernate Validator";
    }
}
