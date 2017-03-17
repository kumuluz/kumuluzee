package com.kumuluz.ee.configuration.cdi.producers;

import com.kumuluz.ee.configuration.utils.ConfigurationUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class ConfigurationUtilProducer {

    @Produces
    @ApplicationScoped
    public ConfigurationUtil getConfigurationUtil() {
        return ConfigurationUtil.getInstance();
    }
}
