package com.kumuluz.ee.common;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

import java.util.List;
import java.util.Optional;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public interface Extension {

    void init(KumuluzServerWrapper server, EeConfig eeConfig);

    void load();

    <T> Optional<T> getProperty(Class<T> tClass);

    <T> Optional<T> getProperty(Class<T> tClass, String propertyName);

    <T> Optional<List<T>> getProperties(Class<T> tClass);

    <T> Optional<List<T>> getProperties(Class<T> tClass, String propertyName);
}
