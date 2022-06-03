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

import java.util.*;

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
    public Optional<Integer> getListSize(String key) {

        for (String possibleKeyName : getPossibleEnvNames(key)) {
            int maxIndex = -1;

            for (String envName : System.getenv().keySet()) {

                if (envName.startsWith(possibleKeyName)) {

                    int openingIndex = possibleKeyName.length();
                    int closingIndex = envName.indexOf("_", openingIndex + 1);

                    if (closingIndex < 0) {
                        closingIndex = envName.length();
                    }

                    try {
                        int idx = Integer.parseInt(envName.substring(openingIndex, closingIndex));
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
                        closingIndex = envName.indexOf("_", openingIndex + 1);
                    }

                    if (closingIndex < 0) {
                        closingIndex = envName.length() - 1;
                    }

                    if (openingIndex >= closingIndex) {
                        continue;
                    }

                    try {
                        int idx = Integer.parseInt(envName.substring(openingIndex, closingIndex));
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

        Set<String> mapKeys = new HashSet<>();
        Set<String> envKeys = new HashSet<>(System.getenv().keySet());

        List<String> possibleKeyNames = (key.equals("")) ? Collections.singletonList("") : getPossibleEnvNames(key);
        for (String possibleKeyName : possibleKeyNames) {
            Set<String> toRemove = new HashSet<>();
            for (String envKey : envKeys) {
                if (!possibleKeyName.equals("") && possibleKeyName.length() + 1 > envKey.length()) {
                    continue;
                }
                if (envKey.startsWith(possibleKeyName)) {
                    int separatorIdx;
                    int startIdx;
                    if (possibleKeyName.equals("")) {
                        int dotIdx = envKey.indexOf('.');
                        int underscoreIdx = envKey.indexOf('_');

                        if (dotIdx > 0 && underscoreIdx > 0) {
                            // both defined, pick earliest
                            separatorIdx = Math.min(dotIdx, underscoreIdx);
                        } else {
                            // at least one is -1
                            separatorIdx = Math.max(dotIdx, underscoreIdx);
                        }
                        startIdx = 0;
                    } else {
                        char separator = envKey.charAt(possibleKeyName.length());

                        if (separator != '.' && separator != '_') {
                            continue;
                        }

                        startIdx = possibleKeyName.length() + 1;

                        separatorIdx = envKey.indexOf(separator, startIdx);
                    }

                    if (separatorIdx < 0) {
                        // no separators left, use full key
                        separatorIdx = envKey.length();
                    }

                    String mapKey = envKey.substring(startIdx, separatorIdx);

                    if (!mapKey.isEmpty()) {
                        int bracketIndex = mapKey.indexOf("[");
                        if (bracketIndex > 0) {
                            // list bracket present, cut it off
                            mapKey = mapKey.substring(0, bracketIndex);
                        }

                        mapKeys.add(mapKey.toLowerCase());
                        toRemove.add(envKey);
                    }
                }
            }
            envKeys.removeAll(toRemove);
        }

        if (mapKeys.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(new ArrayList<>(mapKeys));
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

    @Override
    public Integer getOrdinal() {
        return getInteger(CONFIG_ORDINAL).orElse(300);
    }

    private List<String> getPossibleEnvNames(String key) {
        List<String> possibleNames = new LinkedList<>();

        // MP Config 1.3: raw key
        possibleNames.add(key);
        // MP Config 1.3: replaces non alpha-numeric characters with '_'
        possibleNames.add(replaceNonAlphaNum(key));
        // MP Config 1.3: replaces non alpha-numeric characters with '_', to uppercase
        possibleNames.add(replaceNonAlphaNum(key).toUpperCase());
        // legacy 1: removes characters '[]-' and replaces dots with '_', to uppercase
        possibleNames.add(parseKeyNameForEnvironmentVariables(key));
        // legacy 2: replaces dots with '_', to uppercase
        possibleNames.add(parseKeyNameForEnvironmentVariablesLegacy(key));

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
