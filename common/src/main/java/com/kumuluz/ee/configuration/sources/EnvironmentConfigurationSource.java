package com.kumuluz.ee.configuration.sources;

import com.kumuluz.ee.configuration.ConfigurationSource;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class EnvironmentConfigurationSource implements ConfigurationSource {

    private static final Logger log = Logger.getLogger(EnvironmentConfigurationSource.class.getName());

    private static EnvironmentConfigurationSource instance;

    @Override
    public void init() {
    }

    @Override
    public Optional<String> get(String key) {

        String value = System.getenv(parseKeyNameForEnvironmentVariables(key));
        return (value == null) ? Optional.empty() : Optional.of(value);

    }

    @Override
    public Optional<Boolean> getBoolean(String key) {

        Optional<String> value = get(key);

        if (value.isPresent()) {
            return Optional.of(Boolean.valueOf(value.get()));
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Integer> getInteger(String key) {

        Optional<String> value = get(key);

        if (value.isPresent()) {
            try {
                return Optional.of(Integer.valueOf(value.get()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Double> getDouble(String key) {

        Optional<String> value = get(key);

        if (value.isPresent()) {
            try {
                return Optional.of(Double.valueOf(value.get()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Float> getFloat(String key) {

        Optional<String> value = get(key);

        if (value.isPresent()) {
            try {
                return Optional.of(Float.valueOf(value.get()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }

        } else {
            return Optional.empty();
        }
    }

    @Override
    public void set(String key, String value) {

    }

    @Override
    public void set(String key, Boolean value) {

    }

    @Override
    public void set(String key, Integer value) {

    }

    @Override
    public void set(String key, Double value) {

    }

    @Override
    public void set(String key, Float value) {

    }

    private String parseKeyNameForEnvironmentVariables(String key) {

        return key.toUpperCase().replaceAll("\\.", "_");
    }
}
