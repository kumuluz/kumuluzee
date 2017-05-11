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
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class FileConfigurationSource implements ConfigurationSource {

    private static final Logger log = Logger.getLogger(FileConfigurationSource.class.getName());
    private static FileConfigurationSource instance;

    private String ymlFileName;
    private String yamlFileName;
    private String propertiesFileName;
    private Map<String, Object> config;
    private Properties properties;

    public FileConfigurationSource() {
        this.ymlFileName = "config.yml";
        this.yamlFileName = "config.yaml";
        this.propertiesFileName = "config.properties";
    }

    public static FileConfigurationSource getInstance() {
        if (instance == null) {
            instance = new FileConfigurationSource();
        }
        return instance;
    }

    @Override
    public void init() {

        // read yaml file to Map<String, Object>
        InputStream file;
        Yaml yaml = new Yaml();
        try {
            file = getClass().getClassLoader().getResourceAsStream(ymlFileName);

            if (file == null) {
                file = getClass().getClassLoader().getResourceAsStream(yamlFileName);
            }

            if (file != null) {
                log.info("Loading configuration from YAML file.");

                Object yamlParsed = yaml.load(file);

                if (yamlParsed instanceof Map) {
                    config = (Map<String, Object>) yamlParsed;
                } else {
                    log.info("Configuration YAML is malformed as it contains an array at the root level. Skipping.");
                }

                file.close();
            }
        } catch (IOException e) {
            log.info("Couldn't successfully process the YAML configuration file." +
                    "All your properties may not be correctly loaded");
        }

        // parse yaml file to Map<String, Object>
        if (config == null) {

            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesFileName);

                if (inputStream != null) {

                    log.info("Loading configuration from .properties file " + propertiesFileName);

                    properties = new Properties();
                    properties.load(inputStream);
                }

                inputStream.close();
            } catch (Exception e) {
                log.info("Properties file not found.");
            }

        }
        if (config != null || properties != null) {
            log.info("Configuration successfully read.");
        } else {
            log.info("Unable to load configuration from file. No configuration files were found.");
        }
    }

    @Override
    public Optional<String> get(String key) {

        // get key value from yaml configuration
        if (config != null) {

            Object value = getValue(key);

            return (value == null) ? Optional.empty() : Optional.of(value.toString());

            // get value from .properties configuration
        } else if (properties != null) {

            String value = properties.getProperty(key);
            if (value != null) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
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

        Object value = getValue(key);

        if (value instanceof List) {
            return Optional.of(((List) value).size());
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

    /**
     * Returns true, if key represents an array.
     *
     * @param key configuration key
     * @return
     */
    private boolean representsArray(String key) {

        int openingBracket = key.indexOf("[");
        int closingBracket = key.indexOf("]");

        if (closingBracket == key.length() - 1 && openingBracket != -1) {
            return true;
        }

        return false;
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
        for (String splittedKey : splittedKeys) {

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
                    log.severe("Cannot cast array index.");
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
                    value = ((Map) value).get(splittedKey);
                } else {
                    return null;
                }
            }
        }

        return value;

    }
}
