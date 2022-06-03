/*
 *  Copyright (c) 2014-2019 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationDecoder;
import com.kumuluz.ee.configuration.ConfigurationListener;
import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.enums.ConfigurationValueType;

import java.util.*;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class ConfigurationUtil {

    private static ConfigurationUtil instance;

    private ConfigurationImpl config;

    protected ConfigurationUtil() {
    }

    private ConfigurationUtil(ConfigurationImpl config) {
        this.config = config;
    }

    public static void initialize(ConfigurationImpl config) {

        if (instance != null) {
            throw new IllegalStateException("The ConfigurationUtil was already initialized.");
        }

        instance = new ConfigurationUtil(config);
    }

    public static ConfigurationUtil getInstance() {

        if (instance == null) {
            throw new IllegalStateException("The ConfigurationUtil was not yet initialized.");
        }

        return instance;
    }

    public Optional<String> get(String key) {

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {

            Optional<String> value = configurationSource.get(key);

            if (value.isPresent()) {

                return Optional.of(ConfigurationInterpolationUtil.interpolateString(
                        ConfigurationDecoderUtils.decodeConfigValueIfEncoded(key, value.get()), this::get));
            }
        }

        return Optional.empty();
    }

    public Optional<String> getRaw(String key) {

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

    public Optional<Long> getLong(String key) {

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {

            Optional<Long> value = configurationSource.getLong(key);

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

    public Optional<List<String>> getList(String key) {

        Optional<Integer> listSize = getListSize(key);

        if (!listSize.isPresent()) {
            return Optional.empty();
        }

        List<String> list = new ArrayList<>();
        for (int i = 0; i < listSize.get(); i++) {
            Optional<String> value = get(key + "[" + i + "]");
            value.ifPresent(list::add);
        }

        return Optional.of(list);

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

    public Optional<ConfigurationValueType> getType(String key) {

        // check if key type is a list or a map
        if (getListSize(key).isPresent()) {
            return Optional.of(ConfigurationValueType.LIST);
        }

        if (getMapKeys(key).isPresent()) {
            return Optional.of(ConfigurationValueType.MAP);
        }

        // get the key value from sources according to priorities and determine its type
        Optional<String> value = get(key);

        if (!value.isPresent()) {
            return Optional.empty();
        }

        if ("true".equals(value.get().toLowerCase()) || "false".equals(value.get().toLowerCase())) {
            return Optional.of(ConfigurationValueType.BOOLEAN);
        }

        try {
            Integer.valueOf(value.get());
            return Optional.of(ConfigurationValueType.INTEGER);
        } catch (NumberFormatException ignored) {
        }

        try {
            Long.valueOf(value.get());
            return Optional.of(ConfigurationValueType.LONG);
        } catch (NumberFormatException ignored) {
        }

        try {
            Float f = Float.valueOf(value.get());
            if (!f.isInfinite()) {
                return Optional.of(ConfigurationValueType.FLOAT);
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            Double.valueOf(value.get());
            return Optional.of(ConfigurationValueType.DOUBLE);
        } catch (NumberFormatException ignored) {
        }

        return Optional.of(ConfigurationValueType.STRING);

    }

    public Optional<List<String>> getMapKeys(String key) {

        Set<String> mapKeys = new HashSet<>();

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {

            Optional<List<String>> value = configurationSource.getMapKeys(key);

            if (value.isPresent()) {

                for (String s : value.get()) {

                    if (!mapKeys.contains(s.replace("-", ""))) {
                        mapKeys.add(s);
                    }
                }
            }
        }

        if (mapKeys.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(new ArrayList<>(mapKeys));
    }

    public void subscribe(String key, ConfigurationListener listener) {

        config.getDispatcher().subscribe(listener);

        for (ConfigurationSource configurationSource : config.getConfigurationSources()) {
            configurationSource.watch(key);
        }
    }

    public void unsubscribe(ConfigurationListener listener) {
        config.getDispatcher().unsubscribe(listener);
    }

    public List<ConfigurationSource> getConfigurationSources() {
        return Collections.unmodifiableList(this.config.getConfigurationSources());
    }

    public ConfigurationDecoder getConfigurationDecoder() {
        return config.getConfigurationDecoder();
    }
}
