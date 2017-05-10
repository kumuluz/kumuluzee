package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.sources.EnvironmentConfigurationSource;
import com.kumuluz.ee.configuration.sources.FileConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class ConfigurationImpl {

    private static final Logger log = Logger.getLogger(ConfigurationImpl.class.getName());

    private ConfigurationDispatcher dispatcher;
    private List<ConfigurationSource> configurationSources = new ArrayList<>();

    public ConfigurationImpl() {
        init();
    }

    private void init() {

        // specify sources
        configurationSources = new ArrayList<>();
        configurationSources.add(new EnvironmentConfigurationSource());
        configurationSources.add(new FileConfigurationSource());

        dispatcher = new ConfigurationDispatcher();

        // initialise sources
        for (ConfigurationSource configurationSource : configurationSources) {
            log.info("Initialising configuration source: " + configurationSource.getClass().getSimpleName());
            configurationSource.init();
        }
    }

    public ConfigurationDispatcher getDispatcher() {
        return dispatcher;
    }

    public List<ConfigurationSource> getConfigurationSources() {
        return configurationSources;
    }
}
