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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class EnvironmentConfigurationSource implements ConfigurationSource {

    @Override
    public void init(ConfigurationDispatcher configurationDispatcher) {
    }

    @Override
    public Optional<String> get(String key) {

        String value = null;

        for (String possibleName : getPossibleEnvNames(key)) {
            value = System.getenv(possibleName);

            if (value != null) {
                break;
            }
        }

        return Optional.ofNullable(value);
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

        for (String possibleKeyName : getPossibleEnvNames(key)) {
            Integer maxIndex = -1;

            for (String envName : System.getenv().keySet()) {

                if (envName.startsWith(possibleKeyName)) {

                    int openingIndex = possibleKeyName.length();
                    int closingIndex = envName.indexOf("_", openingIndex + 1);

                    if (closingIndex < 0) {
                        closingIndex = envName.length();
                    }

                    try {
                        Integer idx = Integer.parseInt(envName.substring(openingIndex, closingIndex));
                        maxIndex = Math.max(maxIndex, idx);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            if (maxIndex != -1) {
                return Optional.of(maxIndex + 1);
            }

            // retry for legacy key names
            for (String envName : System.getenv().keySet()) {

                if (envName.startsWith(possibleKeyName)) {

                    int openingIndex = possibleKeyName.length() + 1;
                    int closingIndex = envName.indexOf("]", openingIndex + 1);

                    if (closingIndex < 0) {
                        closingIndex = envName.length() - 1;
                    }

                    try {
                        Integer idx = Integer.parseInt(envName.substring(openingIndex, closingIndex));
                        maxIndex = Math.max(maxIndex, idx);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }

            if (maxIndex != -1) {
                return Optional.of(maxIndex + 1);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<List<String>> getMapKeys(String key) {
        return Optional.empty();
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
        return getInteger(CONFIG_ORDINAL).orElse(300);
    }

    private List<String> getPossibleEnvNames(String key) {
        List<String> possibleNames = new LinkedList<>();

        // legacy 1: removes characters '[]-' and replaces dots with '_', to uppercase
        possibleNames.add(parseKeyNameForEnvironmentVariables(key));
        // legacy 2: replaces dots with '_', to uppercase
        possibleNames.add(parseKeyNameForEnvironmentVariablesLegacy(key));
        // MP Config 1.3: raw key
        possibleNames.add(key);
        // MP Config 1.3: replaces non alpha-numeric characters with '_'
        possibleNames.add(replaceNonAlphaNum(key));
        // MP Config 1.3: replaces non alpha-numeric characters with '_', to uppercase
        possibleNames.add(replaceNonAlphaNum(key).toUpperCase());

        return possibleNames;
    }

    private String replaceNonAlphaNum(String s) {
        return s.replaceAll("[^a-zA-Z0-9]", "_");
    }

    private String parseKeyNameForEnvironmentVariables(String key) {

        return key.toUpperCase().replaceAll("\\[", "").replaceAll("]", "")
                .replaceAll("-", "").replaceAll("\\.", "_");

    }

    private String parseKeyNameForEnvironmentVariablesLegacy(String key) {

        return key.toUpperCase().replaceAll("\\.", "_");
    }
}
