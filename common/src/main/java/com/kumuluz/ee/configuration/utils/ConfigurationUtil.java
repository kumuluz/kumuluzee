package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationListener;
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

    private static ConfigurationUtil instance;

    private ConfigurationImpl config;

    private ConfigurationUtil(ConfigurationImpl config) {
        this.config = config;
    }

    public static void initialize(ConfigurationImpl config) {

        if (instance != null) {
            throw new IllegalStateException("");
        }

        instance = new ConfigurationUtil(config);
    }

    public static ConfigurationUtil getInstance() {

        if (instance == null) {
            throw new IllegalStateException("");
        }

        return instance;
    }

    public Optional<String> get(String key) {

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {
            Optional<String> value = configurationSource.get(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Boolean> getBoolean(String key) {

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {
            Optional<Boolean> value = configurationSource.getBoolean(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Integer> getInteger(String key) {

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {
            Optional<Integer> value = configurationSource.getInteger(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Double> getDouble(String key) {

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {
            Optional<Double> value = configurationSource.getDouble(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Float> getFloat(String key) {

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {
            Optional<Float> value = configurationSource.getFloat(key);
            if (value.isPresent()) {
                return value;
            }
        }
        return Optional.empty();
    }

    public Optional<Integer> getListSize(String key) {
        int listSize = -1;

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {
            Optional<Integer> currentListSize = configurationSource.getListSize(key);
            if (currentListSize.isPresent() && currentListSize.get() > listSize) {
                listSize = currentListSize.get();
            }
        }
        if (listSize == -1) {
            return Optional.empty();
        } else {
            return Optional.of(listSize);
        }
    }

    public void set(String key, String value) {
        config.getConfigurationSources().get(0).set(key, value);
    }

    public void set(String key, Boolean value) {
        config.getConfigurationSources().get(0).set(key, value);
    }

    public void set(String key, Integer value) {
        config.getConfigurationSources().get(0).set(key, value);
    }

    public void set(String key, Double value) {
        config.getConfigurationSources().get(0).set(key, value);
    }

    public void set(String key, Float value) {
        config.getConfigurationSources().get(0).set(key, value);
    }

    public void subscribe(ConfigurationListener listener) {
        config.getDispatcher().subscribe(listener);
    }

    public void unsubscribe(ConfigurationListener listener) {
        config.getDispatcher().unsubscribe(listener);
    }
}
