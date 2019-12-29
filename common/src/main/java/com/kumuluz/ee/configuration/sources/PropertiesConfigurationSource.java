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
import com.kumuluz.ee.configuration.utils.ConfigurationSourceUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

/**
 * Configuration source for properties files.
 *
 * @author Urban Malc
 * @since 3.6.0
 */
public class PropertiesConfigurationSource extends FileConfigurationSource {

    private Properties properties;

    public PropertiesConfigurationSource(String filename, int defaultOrdinal) {

        super(filename, defaultOrdinal);
    }

    @Override
    public void init(ConfigurationDispatcher configurationDispatcher) {

        try {
            InputStream inputStream = getInputStream();

            logDeferrer.defer(l -> l.info("Loading configuration from .properties file: " + filename));

            properties = new Properties();
            properties.load(inputStream);

            inputStream.close();
        } catch (IOException e) {
            logDeferrer.defer(l -> l.info("Properties file: " + filename + " not found."));
        }

        if (properties != null) {
            logDeferrer.defer(l -> l.info("Configuration successfully read."));
        } else {
            logDeferrer.defer(l -> l.warning("Unable to load configuration from file. File " + filename +
                    " is not valid or does not exist."));
        }
    }

    @Override
    public Optional<String> get(String key) {

        // get value from .properties configuration
        if (properties != null) {

            String value = properties.getProperty(key);
            if (value != null) {
                return Optional.of(value);
            }
        }

        return Optional.empty();
    }

    @Override
    public Optional<Integer> getListSize(String key) {

        if (properties != null) {
            return ConfigurationSourceUtils.getListSize(key, properties.stringPropertyNames());
        }

        return Optional.empty();

    }

    @Override
    public Optional<List<String>> getMapKeys(String key) {

        if (properties != null) {
            return ConfigurationSourceUtils.getMapKeys(key, properties.stringPropertyNames());
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

}
