/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.configuration.sources;

import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.utils.ConfigurationDispatcher;
import com.kumuluz.ee.configuration.utils.ConfigurationSourceUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author Urban Malc
 * @since 2.4.0
 */
public class SystemPropertyConfigurationSource implements ConfigurationSource {

    @Override
    public void init(ConfigurationDispatcher configurationDispatcher) {
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(System.getProperty(key));
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {

        Optional<String> value = get(key);

        return value.map(Boolean::valueOf);
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
    public Optional<Long> getLong(String key) {

        Optional<String> value = get(key);

        if (value.isPresent()) {

            try {
                return Optional.of(Long.valueOf(value.get()));
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
    public Optional<Integer> getListSize(String key) {

        return ConfigurationSourceUtils.getListSize(key, System.getProperties().stringPropertyNames());
    }

    @Override
    public Optional<List<String>> getMapKeys(String key) {

        return ConfigurationSourceUtils.getMapKeys(key, System.getProperties().stringPropertyNames());
    }

    @Override
    public void watch(String key) {
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

    @Override
    public Integer getOrdinal() {
        return getInteger(CONFIG_ORDINAL).orElse(400);
    }
}
