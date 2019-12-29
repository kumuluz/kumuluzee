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

import com.kumuluz.ee.configuration.utils.ConfigurationDispatcher;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Configuration source for YAML files.
 *
 * @author Urban Malc
 * @since 3.6.0
 */
public class YamlConfigurationSource extends FileConfigurationSource {

    private Map<String, Object> config;

    public YamlConfigurationSource(String filename, int defaultOrdinal) {

        super(filename, defaultOrdinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(ConfigurationDispatcher configurationDispatcher) {

        System.out.println("loading " + filename);
        // read yaml file to Map<String, Object>
        Yaml yaml = new Yaml();
        try {

            InputStream file = getInputStream();

            logDeferrer.defer(l -> l.info("Loading configuration from YAML file " + filename));

            Object yamlParsed = yaml.load(file);

            if (yamlParsed instanceof Map) {
                config = (Map<String, Object>) yamlParsed;
            } else {

                logDeferrer.defer(l -> l.info("Configuration YAML is malformed as it contains an array at the " +
                        "root level. Skipping."));
            }

            file.close();
        } catch (IOException e) {
            logDeferrer.defer(l ->
                    l.info("Couldn't successfully process the YAML configuration file." +
                            "All your properties may not be correctly loaded"));
        }

        if (config != null) {
            logDeferrer.defer(l -> l.info("Configuration successfully read."));
        } else {
            logDeferrer.defer(l -> l.warning("Unable to load configuration from file. File " + filename +
                    " is not valid or does not exist."));
        }
    }

    @Override
    public Optional<String> get(String key) {

        // get key value from yaml configuration
        if (config != null) {

            Object value = getValue(key);

            return (value == null) ? Optional.empty() : Optional.of(value.toString());
        }

        return Optional.empty();
    }

    @Override
    public Optional<Integer> getListSize(String key) {

        if (config != null) {

            Object value = getValue(key);

            if (value instanceof List) {
                return Optional.of(((List) value).size());
            }
        }

        return Optional.empty();

    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<String>> getMapKeys(String key) {

        if (config != null) {
            Object o = (key.equals("")) ? config : getValue(key);
            Map<String, Object> map = null;

            if (o instanceof Map) {
                map = (Map<String, Object>) o;
            }

            if (map == null || map.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new ArrayList<>(map.keySet()));

        }

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
        return getInteger(CONFIG_ORDINAL).orElse(defaultOrdinal);
    }

    /**
     * Returns true, if key represents an array.
     *
     * @param key configuration key
     * @return true if the config key represents an array, false otherwise.
     */
    private boolean representsArray(String key) {

        int openingBracket = key.indexOf("[");
        int closingBracket = key.indexOf("]");

        return closingBracket == key.length() - 1 && openingBracket != -1;

    }

    /**
     * Parses configuration map, returns value for given key.
     *
     * @param key configuration key
     * @return Value for given key.
     */
    private Object getValue(String key) {

        // iterate over configuration tree
        String[] splittedKeys = key.split("\\.");
        Object value = config;

        for (int i = 0; i < splittedKeys.length; i++) {

            String splittedKey = splittedKeys[i];

            if (value == null) {
                return null;
            }

            // parse arrays
            if (representsArray(splittedKey)) {

                // value array support
                int arrayIndex;
                int openingBracket = splittedKey.indexOf("[");
                int closingBracket = splittedKey.indexOf("]");

                try {
                    arrayIndex = Integer.parseInt(splittedKey.substring(openingBracket + 1, closingBracket));
                } catch (NumberFormatException e) {

                    if (log != null) {
                        log.severe("Cannot cast array index.");
                    }

                    return null;
                }

                splittedKey = splittedKey.substring(0, openingBracket);

                if (value instanceof Map) {
                    value = ((Map) value).get(splittedKey);
                } else {
                    return null;
                }

                if (value instanceof List) {
                    value = (arrayIndex < ((List) value).size()) ? ((List) value).get(arrayIndex) : null;
                }

            } else {
                if (value instanceof Map) {

                    Object tmpValue = ((Map) value).get(splittedKey);

                    if (tmpValue == null && i != splittedKeys.length - 1) {

                        String postfixKey = Arrays.stream(splittedKeys).skip(i)
                                .collect(Collectors.joining("."));

                        return ((Map) value).get(postfixKey);
                    } else {

                        value = tmpValue;
                    }
                } else {
                    return null;
                }
            }
        }

        return value;
    }
}
