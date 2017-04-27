package com.kumuluz.ee.jpa.common;

import java.util.Map;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public interface PersistenceSettings {

    Map<String, String> getPersistenceUnitProperties();
}
