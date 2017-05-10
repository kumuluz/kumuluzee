package com.kumuluz.ee.configuration;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
@FunctionalInterface
public interface ConfigurationListener {

    void onChange(String key, String value);
}
