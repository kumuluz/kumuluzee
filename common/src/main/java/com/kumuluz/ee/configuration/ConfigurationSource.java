package com.kumuluz.ee.configuration;

import java.util.Optional;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public interface ConfigurationSource {

    void init();

    Optional<String> get(String key);

    Optional<Boolean> getBoolean(String key);

    Optional<Integer> getInteger(String key);

    Optional<Double> getDouble(String key);

    Optional<Float> getFloat(String key);

    void set(String key, String value);

    void set(String key, Boolean value);

    void set(String key, Integer value);

    void set(String key, Double value);

    void set(String key, Float value);
}
