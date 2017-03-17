package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.sources.EnvironmentConfigurationSource;
import com.kumuluz.ee.configuration.sources.FileConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class ConfigurationUtil {

    private static final Logger log = Logger.getLogger(ConfigurationUtil.class.getName());
    private static ConfigurationUtil instance;
    private List<ConfigurationSource> configurationSources;

    protected ConfigurationUtil() {
    }

    public static ConfigurationUtil getInstance() {
        if (instance == null) {
            instance = new ConfigurationUtil();
            instance.init();
        }
        return instance;
    }

    private void init() {

        // specify sources
        configurationSources = new ArrayList<>();
        configurationSources.add(new EnvironmentConfigurationSource());
        configurationSources.add(new FileConfigurationSource());

        // initialise sources
        for (ConfigurationSource configurationSource : configurationSources) {
            log.info("Initialising configuration source: " + configurationSource.getClass().getSimpleName());
            configurationSource.init();
        }
    }

    public Optional<String> get(String key) {

        for (ConfigurationSource configurationSource : configurationSources) {
            Optional<String> value = configurationSource.get(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Boolean> getBoolean(String key) {

        for (ConfigurationSource configurationSource : configurationSources) {
            Optional<Boolean> value = configurationSource.getBoolean(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Integer> getInteger(String key) {

        for (ConfigurationSource configurationSource : configurationSources) {
            Optional<Integer> value = configurationSource.getInteger(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Double> getDouble(String key) {

        for (ConfigurationSource configurationSource : configurationSources) {
            Optional<Double> value = configurationSource.getDouble(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Float> getFloat(String key) {

        for (ConfigurationSource configurationSource : configurationSources) {
            Optional<Float> value = configurationSource.getFloat(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public void set(String key, String value) {
        configurationSources.get(0).set(key, value);
    }

    public void set(String key, Boolean value) {
        configurationSources.get(0).set(key, value);
    }

    public void set(String key, Integer value) {
        configurationSources.get(0).set(key, value);
    }

    public void set(String key, Double value) {
        configurationSources.get(0).set(key, value);
    }

    public void set(String key, Float value) {
        configurationSources.get(0).set(key, value);
    }
}
