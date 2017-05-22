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
package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationSource;
import com.kumuluz.ee.configuration.sources.EnvironmentConfigurationSource;
import com.kumuluz.ee.configuration.sources.FileConfigurationSource;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Tilen Faganel
 * @since 2.1.0
 */
public class ConfigurationImpl {

    private static final Logger log = Logger.getLogger(ConfigurationImpl.class.getName());

    private ConfigurationDispatcher dispatcher;
    private List<ConfigurationSource> configurationSources = new ArrayList<>();

    public ConfigurationImpl() {
        init();
    }

    private void init() {

        // specify sources
        configurationSources = new ArrayList<>();
        configurationSources.add(new EnvironmentConfigurationSource());
        configurationSources.add(new FileConfigurationSource());

        dispatcher = new ConfigurationDispatcher();

        // initialise sources
        for (ConfigurationSource configurationSource : configurationSources) {
            log.info("Initializing configuration source: " + configurationSource.getClass().getSimpleName());
            configurationSource.init(dispatcher );
        }
    }

    public ConfigurationDispatcher getDispatcher() {
        return dispatcher;
    }

    public List<ConfigurationSource> getConfigurationSources() {
        return configurationSources;
    }
}
