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
import com.kumuluz.ee.logs.LogDeferrer;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class FileConfigurationSource implements ConfigurationSource {

    private Logger log;
    private LogDeferrer<Logger> logDeferrer;

    private String ymlFileName;
    private String yamlFileName;
    private String propertiesFileName;
    private String microProfilePropertiesFileName;
    private Map<String, Object> config;
    private Properties properties;

    public FileConfigurationSource() {

        this.ymlFileName = "config.yml";
        this.yamlFileName = "config.yaml";
        this.propertiesFileName = "config.properties";
        this.microProfilePropertiesFileName = "META-INF/microprofile-config.properties";

        String configurationFileName = System.getProperty("com.kumuluz.ee.configuration.file");

        if (configurationFileName != null && !configurationFileName.isEmpty()) {
            this.ymlFileName = configurationFileName;
            this.yamlFileName = configurationFileName;
            this.propertiesFileName = configurationFileName;
        }

        this.logDeferrer = new LogDeferrer<>();

        this.logDeferrer.init(() -> Logger.getLogger(FileConfigurationSource.class.getName()));
    }

    public void postInit() {

        logDeferrer.execute();
        logDeferrer = null;

        log = Logger.getLogger(FileConfigurationSource.class.getName());
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(ConfigurationDispatcher configurationDispatcher) {

        // read yaml file to Map<String, Object>
        InputStream file;
        Yaml yaml = new Yaml();
        try {
            file = getClass().getClassLoader().getResourceAsStream(ymlFileName);

            if (file == null) {
                file = getClass().getClassLoader().getResourceAsStream(yamlFileName);
            }

            if (file == null) {
                try {
                    file = Files.newInputStream(Paths.get(ymlFileName));
                } catch (IOException ignored) {
                }
            }

            if (file == null) {
                try {
                    file = Files.newInputStream(Paths.get(yamlFileName));
                } catch (IOException ignored) {
                }
            }

            if (file != null) {

                logDeferrer.defer(l -> l.info("Loading configuration from YAML file."));

                Object yamlParsed = yaml.load(file);

                if (yamlParsed instanceof Map) {
                    config = (Map<String, Object>) yamlParsed;
                } else {

                    logDeferrer.defer(l -> l.info("Configuration YAML is malformed as it contains an array at the " +
                            "root level. Skipping."));
                }

                file.close();
            }
        } catch (IOException e) {
            logDeferrer.defer(l ->
                    l.info("Couldn't successfully process the YAML configuration file." +
                            "All your properties may not be correctly loaded"));
        }

        // parse properties file
        if (config == null) {
            loadProperties(propertiesFileName);
            if (properties == null) {
                loadProperties(microProfilePropertiesFileName);
            }
        }

        if (config != null || properties != null) {
            logDeferrer.defer(l -> l.info("Configuration successfully read."));
        } else {
            logDeferrer.defer(l -> l.info("Unable to load configuration from file. No configuration files were found" +
                    "."));
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

        if (config != null) {
            Object value = getValue(key);

            if (value instanceof List) {
                return Optional.of(((List) value).size());
            }
        } else if (properties != null) {
            Integer maxIndex = -1;

            for (String propertyName : properties.stringPropertyNames()) {
                if (propertyName.startsWith(key + "[")) {
                    int openingIndex = key.length() + 1;
                    int closingIndex = propertyName.indexOf("]", openingIndex + 1);
                    try {
                        Integer idx = Integer.parseInt(propertyName.substring(openingIndex, closingIndex));
                        maxIndex = Math.max(maxIndex, idx);
                    } catch (NumberFormatException e) {
                        log.severe("Cannot cast array index for key: " + propertyName);
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
    @SuppressWarnings("unchecked")
    public Optional<List<String>> getMapKeys(String key) {

        if (config != null) {
            Object o = getValue(key);
            Map<String, Object> map = null;

            if (o instanceof Map) {
                map = (Map<String, Object>) o;
            }

            if (map == null || map.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(new ArrayList<>(map.keySet()));

        } else if (properties != null) {
            Set<String> mapKeys = new HashSet<>();
            for (String propertyName : properties.stringPropertyNames()) {
                String mapKey = "";

                if(propertyName.startsWith(key)) {
                    int index = key.length() + 1;
                    if(index < propertyName.length() && propertyName.charAt(index-1) == '.') {
                        mapKey = propertyName.substring(index);
                    }
                }

                if(!mapKey.isEmpty()) {
                    int endIndex = mapKey.indexOf(".");
                    if (endIndex > 0) {
                        mapKey = mapKey.substring(0, endIndex);
                    }

                    int bracketIndex = mapKey.indexOf("[");
                    if (bracketIndex > 0) {
                        mapKey = mapKey.substring(0, bracketIndex);
                    }

                    mapKeys.add(mapKey);
                }
            }

            if (mapKeys.size() == 0) {
                return Optional.empty();
            } else {
                return Optional.of(new ArrayList<>(mapKeys));
            }
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
        return getInteger(CONFIG_ORDINAL).orElse(100);
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
                    value = ((Map) value).get(splittedKey);
                } else {
                    return null;
                }
            }
        }

        return value;
    }

    private void loadProperties(String fileName) {

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            if (inputStream == null) {
                try {
                    inputStream = Files.newInputStream(Paths.get(fileName));
                } catch (IOException ignored) {
                }
            }

            if (inputStream != null) {

                logDeferrer.defer(l -> l.info("Loading configuration from .properties file: " + propertiesFileName));

                properties = new Properties();
                properties.load(inputStream);

                inputStream.close();
            }
        } catch (IOException e) {
            logDeferrer.defer(l -> l.info("Properties file: " + fileName + " not found."));
        }
    }
}
