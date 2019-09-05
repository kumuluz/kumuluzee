/*
 *  Copyright (c) 2014-2019 Kumuluz and/or its affiliates
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
package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationDecoder;
import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.sources.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class ConfigurationImpl {

    private static final String[] YAML_FILE_LOCATIONS = {"config.yml", "config.yaml"};
    private static final String[] PROPERTIES_FILE_LOCATIONS = {"META-INF/microprofile-config.properties",
            "config.properties"};

    private Logger utilLogger;
    private ConfigurationDispatcher dispatcher;
    private List<ConfigurationSource> configurationSources;
    private ConfigurationDecoder configurationDecoder;

    private List<FileConfigurationSource> fileConfigurationSources;

    public ConfigurationImpl() {
        init();
    }

    private void init() {

        EnvironmentConfigurationSource environmentConfigurationSource = new EnvironmentConfigurationSource();
        SystemPropertyConfigurationSource systemPropertyConfigurationSource = new SystemPropertyConfigurationSource();
        fileConfigurationSources = getFileConfigurationSources();

        // specify sources
        configurationSources = new ArrayList<>();
        configurationSources.add(environmentConfigurationSource);
        configurationSources.add(systemPropertyConfigurationSource);
        configurationSources.addAll(fileConfigurationSources);

        dispatcher = new ConfigurationDispatcher();

        // initialise sources
        for (ConfigurationSource configurationSource : configurationSources) {

            configurationSource.init(dispatcher);
        }

        // initialise configuration decoder
        List<ConfigurationDecoder> configurationDecoders = new ArrayList<>();
        ServiceLoader.load(ConfigurationDecoder.class).forEach(configurationDecoders::add);
        if (configurationDecoders.size() > 1) {
            throw new IllegalStateException(
                    "There is more than one service provider defined for the ConfigurationDecoder interface.");
        } else if (configurationDecoders.size() == 1) {
            configurationDecoder = configurationDecoders.get(0);
        }
    }

    private List<FileConfigurationSource> getFileConfigurationSources() {

        List<FileConfigurationSource> configurationSources = new LinkedList<>();
        int nextOrdinal = 100;

        // properties should be first so that microprofile properties gets ordinal 100 as per specification
        for (String propertiesFile : PROPERTIES_FILE_LOCATIONS) {
            if (resourceExists(propertiesFile)) {
                configurationSources.add(new PropertiesConfigurationSource(propertiesFile, nextOrdinal));
                nextOrdinal--;
            }
        }

        for (String yamlFile : YAML_FILE_LOCATIONS) {
            if (resourceExists(yamlFile)) {
                configurationSources.add(new YamlConfigurationSource(yamlFile, nextOrdinal));
                nextOrdinal--;
            }
        }

        // legacy additional file from system property
        String legacyConfigurationFileName = System.getProperty("com.kumuluz.ee.configuration.file");
        if (legacyConfigurationFileName != null && !legacyConfigurationFileName.isEmpty()) {
            configurationSources.add(new PropertiesConfigurationSource(legacyConfigurationFileName, 101));
            configurationSources.add(new YamlConfigurationSource(legacyConfigurationFileName, 102));
        }

        return configurationSources;
    }

    public void addFileConfigurationSource(FileConfigurationSource fileConfigurationSource) {
        this.configurationSources.add(fileConfigurationSource);
        // add to file configuration sources so the postInit logic gets properly executed
        this.fileConfigurationSources.add(fileConfigurationSource);
    }

    private boolean resourceExists(String locator) {
        return this.getClass().getClassLoader().getResource(locator) != null || Files.exists(Paths.get(locator));
    }

    public void postInit() {

        fileConfigurationSources.forEach(FileConfigurationSource::postInit);

        utilLogger = Logger.getLogger(ConfigurationUtil.class.getName());
    }

    public Boolean isUtilLoggerAvailable() {
        return utilLogger != null;
    }

    public Logger getUtilLogger() {
        return utilLogger;
    }

    public ConfigurationDispatcher getDispatcher() {
        return dispatcher;
    }

    public List<ConfigurationSource> getConfigurationSources() {
        return configurationSources;
    }

    public ConfigurationDecoder getConfigurationDecoder() {
        return configurationDecoder;
    }
}
