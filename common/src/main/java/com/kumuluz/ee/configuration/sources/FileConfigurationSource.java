package com.kumuluz.ee.configuration.sources;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.kumuluz.ee.configuration.ConfigurationSource;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
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

    private String yamlFileName;
    private String propertiesFileName;
    private Map<String, Object> config;
    private Properties properties;

    public FileConfigurationSource() {
        this.yamlFileName = "config.yml";
        this.propertiesFileName = "config.properties";
    }

    @Override
    public void init() {

        YamlReader reader = null;
        try {
            URL file = getClass().getClassLoader().getResource(yamlFileName);
            if (file != null) {

                log.info("Loading configuration from yaml file " + yamlFileName);

                reader = new YamlReader(new FileReader(file.getFile()));
                config = (Map<String, Object>) reader.read();
            }
        } catch (FileNotFoundException e) {
            log.info("Yaml configuration file was not found.");
        } catch (YamlException e) {
            log.severe("Malformed yaml configuration file.");
        }

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
                log.severe("Properties file not found.");
            }

        }
        if (config != null || properties != null) {
            log.info("Configuration successfully read.");
        } else {
            log.severe("Unable to load configuration from file. No configuration files were found.");
        }
    }

    @Override
    public Optional<String> get(String key) {

        // get value from yaml configuration
        if (config != null) {

            // value array support
            int arrayIndex = 0;
            int openingBracket = key.indexOf("[");
            int closingBracket = key.indexOf("]");
            if (closingBracket == key.length() - 1 && openingBracket != -1) {

                try {
                    arrayIndex = Integer.parseInt(key.substring(openingBracket + 1, closingBracket));
                } catch (NumberFormatException e) {
                    log.severe("Cannot cast array index.");
                    return Optional.empty();
                }

                key = key.substring(0, openingBracket);
            }

            // find key in Map
            String[] splittedKey = key.split("\\.");
            Object value = config;
            for (String s : splittedKey) {
                if (value == null) {
                    return Optional.empty();
                }
                value = ((Map) value).get(s);
            }

            // return value
            if (value instanceof String) {
                return Optional.of((String) value);
            } else if (value instanceof List) {
                return (arrayIndex < ((List) value).size()) ? Optional.of((String) ((List) value).get(arrayIndex)) :
                        Optional.empty();
            }

            // get value from yaml configuration
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

}
