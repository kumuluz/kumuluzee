package com.kumuluz.ee.configuration.cdi.producers;

import com.kumuluz.ee.common.config.EeConfig;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class EeConfigProducer {

    @Produces
    @ApplicationScoped
    public EeConfig getConfigurationUtil() {
        return EeConfig.getInstance();
    }
}
