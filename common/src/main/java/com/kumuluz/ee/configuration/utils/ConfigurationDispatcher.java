package com.kumuluz.ee.configuration.utils;

import com.kumuluz.ee.configuration.ConfigurationListener;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
// Fire configuration events and notify subscription
public class ConfigurationDispatcher {

    private List<ConfigurationListener> subscriptions = new ArrayList<>();

    public void notifyChange(String key, String value) {

    }

    public void subscribe(ConfigurationListener listener) {

    }

    public void unsubscribe(ConfigurationListener listener) {

    }
}
