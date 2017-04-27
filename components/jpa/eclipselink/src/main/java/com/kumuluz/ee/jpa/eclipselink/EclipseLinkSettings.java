package com.kumuluz.ee.jpa.eclipselink;

import com.kumuluz.ee.jpa.common.PersistenceSettings;
import org.eclipse.persistence.config.PersistenceUnitProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class EclipseLinkSettings implements PersistenceSettings {

    private Map<String, String> properties = new HashMap<>();

    private Boolean jtaPresent;

    EclipseLinkSettings(Boolean jtaPresent) {
        this.jtaPresent = jtaPresent;
    }

    @Override
    public Map<String, String> getPersistenceUnitProperties() {

        if (jtaPresent) {
            properties.put(PersistenceUnitProperties.TARGET_SERVER, KumuluzPlatform.class.getName());
        }

        return properties;
    }
}
