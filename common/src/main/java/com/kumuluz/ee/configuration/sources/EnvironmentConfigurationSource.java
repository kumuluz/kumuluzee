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

import java.util.Optional;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class EnvironmentConfigurationSource implements ConfigurationSource {

    private static final Logger log = Logger.getLogger(EnvironmentConfigurationSource.class.getName());
    private static EnvironmentConfigurationSource instance;

    public static EnvironmentConfigurationSource getInstance() {
        if (instance == null) {
            instance = new EnvironmentConfigurationSource();
        }
        return instance;
    }

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
    public Optional<Integer> getListSize(String key) {

        int listSize = -1;
        int index = -1;
        String value;

        do {
            listSize += 1;
            index += 1;
            value = System.getenv(parseKeyNameForEnvironmentVariables(key + "[" + index + "]"));
        } while (value != null);

        if (listSize > 0) {
            return Optional.of(listSize);
        } else {
            return Optional.empty();
        }
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

    private String parseKeyNameForEnvironmentVariables(String key) {

        return key.toUpperCase().replaceAll("\\.", "_");

    }
}
