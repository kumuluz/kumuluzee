package com.kumuluz.ee.common;

import com.kumuluz.ee.common.config.EeConfig;
import com.kumuluz.ee.common.wrapper.KumuluzServerWrapper;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public interface Component {

    void init(KumuluzServerWrapper server, EeConfig eeConfig);

    void load();
}
