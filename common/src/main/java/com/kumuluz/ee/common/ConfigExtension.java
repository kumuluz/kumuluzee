package com.kumuluz.ee.common;

import com.kumuluz.ee.configuration.ConfigurationSource;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public interface ConfigExtension extends Extension {

    ConfigurationSource getConfigurationSource();
}
