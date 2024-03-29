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
import com.kumuluz.ee.logs.LogDeferrer;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class FileConfigurationSource implements ConfigurationSource {

    private enum Mode {
        YAML,
        PROPERTIES
    }

    private Logger log;
    private LogDeferrer<Logger> logDeferrer;

    private String ymlFileName;
    private String yamlFileName;
    private String propertiesFileName;
    private final String microProfilePropertiesFileName;
    private final List<Map<String, Object>> yamlConfigs = new ArrayList<>();
    private final List<Properties> properties = new ArrayList<>();

    private Mode mode;

    public FileConfigurationSource() {

        this.ymlFileName = "config.yml";
        this.yamlFileName = "config.yaml";
        this.propertiesFileName = "config.properties";
        this.microProfilePropertiesFileName = "META-INF/microprofile-config.properties";

        String configurationFileName = System.getProperty("com.kumuluz.ee.configuration.file");

        if (configurationFileName != null && !configurationFileName.isEmpty()) {
            this.ymlFileName = configurationFileName;
            this.yamlFileName = null;
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
    public void init(ConfigurationDispatcher configurationDispatcher) {

        // read yaml file to Map<String, Object>
        Map<String, Object> yamlConfig = loadYamlFile(this.ymlFileName, this.yamlFileName);

        if (yamlConfig != null) {
            this.yamlConfigs.add(yamlConfig);
            this.mode = Mode.YAML;
        } else {
            // parse properties file
            Properties p = loadProperties(propertiesFileName);
            if (p == null) {
                p = loadProperties(microProfilePropertiesFileName);
            }

            if (p != null) {
                this.properties.add(p);
                this.mode = Mode.PROPERTIES;
            }
        }

        if (!yamlConfigs.isEmpty() || !properties.isEmpty()) {
            logDeferrer.defer(l -> l.info("Configuration successfully read."));
        } else {
            logDeferrer.defer(l -> l.info("Unable to load configuration from file. " +
                    "No configuration files were found."));
        }
    }

    @Override
    public void initProfile(String profileName) {

        if (mode == Mode.YAML) {

            Map<String, Object> yamlConfig = loadYamlFile("config-" + profileName + ".yml", "config-" + profileName + ".yaml");
            if (yamlConfig != null) {
                this.yamlConfigs.add(0, yamlConfig);
            }
        } else if (mode == Mode.PROPERTIES) {
            Properties p = loadProperties("config-" + profileName + ".properties");
            if (p == null) {
                p = loadProperties("META-INF/microprofile-config-" + profileName + ".properties");
            }
            if (p != null) {
                this.properties.add(0, p);
            }
        }
    }

    @Override
    public Optional<String> get(String key) {

        // get key value from yaml configuration
        if (mode == Mode.YAML) {

            Object value;
            for (var config : this.yamlConfigs) {
                value = getYamlValue(key, config);

                if (value != null && !(value instanceof Map) && !(value instanceof List)) {
                    return Optional.of(value.toString());
                }
            }

            return Optional.empty();

            // get value from .properties configuration
        } else if (mode == Mode.PROPERTIES) {

            for (Properties properties : this.properties) {
                String value = properties.getProperty(key);
                if (value != null) {
                    return Optional.of(value);
                }
            }

            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public Optional<Integer> getListSize(String key) {

        if (mode == Mode.YAML) {

            Integer maxSize = null;

            for (var config : this.yamlConfigs) {
                Object value = getYamlValue(key, config);

                if (value instanceof List) {
                    if (maxSize == null) {
                        maxSize = ((List<?>) value).size();
                    } else {
                        maxSize = Math.max(((List<?>) value).size(), maxSize);
                    }
                }
            }

            return Optional.ofNullable(maxSize);

        } else if (mode == Mode.PROPERTIES) {

            Integer maxSize = null;

            for (Properties properties : this.properties) {
                Optional<Integer> propertyListSize = ConfigurationSourceUtils.getListSize(key, properties.stringPropertyNames());

                if (propertyListSize.isPresent()) {
                    if (maxSize == null) {
                        maxSize = propertyListSize.get();
                    } else {
                        maxSize = Math.max(propertyListSize.get(), maxSize);
                    }
                }
            }

            return Optional.ofNullable(maxSize);
        }

        return Optional.empty();

    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<List<String>> getMapKeys(String key) {

        if (mode == Mode.YAML) {

            boolean found = false;
            Set<String> mergedKeys = new HashSet<>();

            for (var config : this.yamlConfigs) {
                Object o = (key.equals("")) ? config : getYamlValue(key, config);
                Map<String, Object> map = null;

                if (o instanceof Map) {
                    map = (Map<String, Object>) o;
                }

                if (map != null && !map.isEmpty()) {
                    found = true;
                    mergedKeys.addAll(map.keySet());
                }
            }

            if (found) {
                return Optional.of(new ArrayList<>(mergedKeys));
            } else {
                return Optional.empty();
            }

        } else if (mode == Mode.PROPERTIES) {

            boolean found = false;
            Set<String> mergedKeys = new HashSet<>();

            for (Properties properties : this.properties) {
                Optional<List<String>> propertyMapKeys = ConfigurationSourceUtils.getMapKeys(key,
                        properties.stringPropertyNames());

                if (propertyMapKeys.isPresent()) {
                    found = true;
                    mergedKeys.addAll(propertyMapKeys.get());
                }
            }

            if (found) {
                return Optional.of(new ArrayList<>(mergedKeys));
            } else {
                return Optional.empty();
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
    private Object getYamlValue(String key, Object value) {

        // iterate over configuration tree
        String[] splittedKeys = key.split("\\.");

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
                    value = ((Map<?, ?>) value).get(splittedKey);
                } else {
                    return null;
                }

                if (value instanceof List) {
                    value = (arrayIndex < ((List<?>) value).size()) ? ((List<?>) value).get(arrayIndex) : null;
                }

            } else {
                if (value instanceof Map) {

                    Object tmpValue = ((Map<?, ?>) value).get(splittedKey);

                    if (tmpValue == null && i != splittedKeys.length - 1) {

                        String postfixKey = Arrays.stream(splittedKeys).skip(i)
                                .collect(Collectors.joining("."));

                        return ((Map<?, ?>) value).get(postfixKey);
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

    private Map<String, Object> loadYamlFile(String fileNameYml, String fileNameYaml) {

        InputStream file = null;
        Yaml yaml = new Yaml();
        try {
            file = getClass().getClassLoader().getResourceAsStream(fileNameYml);

            if (file == null && fileNameYaml != null) {
                file = getClass().getClassLoader().getResourceAsStream(fileNameYaml);
            }

            if (file == null) {
                try {
                    file = Files.newInputStream(Paths.get(fileNameYml));
                } catch (IOException ignored) {
                }
            }

            if (file == null && fileNameYaml != null) {
                try {
                    file = Files.newInputStream(Paths.get(fileNameYaml));
                } catch (IOException ignored) {
                }
            }

            if (file != null) {

                logDeferrer.defer(l -> l.info("Loading configuration from YAML file."));

                Object yamlParsed = yaml.load(file);

                if (yamlParsed instanceof Map) {
                    //noinspection unchecked
                    return (Map<String, Object>) yamlParsed;
                } else {

                    logDeferrer.defer(l -> l.info("Configuration YAML is malformed as it contains an array at the " +
                            "root level. Skipping."));
                }
            }

            return null;
        } catch (Exception e) {
            logDeferrer.defer(l ->
                    l.info("Couldn't successfully process the YAML configuration file. " +
                            "All your properties may not be correctly loaded."));

            return null;
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private Properties loadProperties(String fileName) {

        InputStream inputStream = null;
        try {
            inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            if (inputStream == null) {
                try {
                    inputStream = Files.newInputStream(Paths.get(fileName));
                } catch (IOException ignored) {
                }
            }

            if (inputStream != null) {

                logDeferrer.defer(l -> l.info("Loading configuration from .properties file: " + fileName));

                Properties p = new Properties();
                p.load(inputStream);
                return p;
            }
        } catch (Exception e) {
            logDeferrer.defer(l -> l.info("Properties file: " + fileName + " not found."));
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ignored) {
                }
            }
        }

        return null;
    }
}
