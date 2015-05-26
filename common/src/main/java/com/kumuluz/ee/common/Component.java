package com.kumuluz.ee.common;

import com.kumuluz.ee.common.config.EeConfig;

/**
 * @author Tilen
 */
public interface Component {

    void init(KumuluzServer server, EeConfig eeConfig);

    void load();

    String getComponentName();

    String getImplementationName();
}
