package com.kumuluz.ee.jpa.common.resources;

import com.kumuluz.ee.common.config.PersistenceConfig;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceUnitHolder {

    private List<PersistenceConfig> configs;

    private final Map<String, EntityManagerFactory> factories = new HashMap<>();

    private EntityManagerFactory defaultEntityMangerFactory;

    private static final PersistenceUnitHolder instance = new PersistenceUnitHolder();

    public synchronized EntityManagerFactory getEntityManagerFactory(String unitName) {

        if (unitName == null || "".equals(unitName)) {
            return defaultEntityManagerFactory();
        }
        return findNamedUnitEntityManagerFactory(unitName);
    }

    private EntityManagerFactory findNamedUnitEntityManagerFactory(String unitName) {
        EntityManagerFactory factory = factories.get(unitName);

        if (factory == null) {

            Properties properties = new Properties();

            Optional<PersistenceConfig> config = configs.stream()
                    .filter(c -> unitName.equals(c.getUnitName()))
                    .findFirst();

            config.ifPresent(c -> {

                Optional.ofNullable(c.getUrl()).ifPresent(u -> properties.put("javax.persistence.jdbc.url", u));
                Optional.ofNullable(c.getDriver()).ifPresent(d -> properties.put("javax.persistence.jdbc.driver", d));
                Optional.ofNullable(c.getUsername()).ifPresent(u -> properties.put("javax.persistence.jdbc.user", u));
                Optional.ofNullable(c.getPassword()).ifPresent(p -> properties.put("javax.persistence.jdbc.password", p));
            });

            factory = Persistence.createEntityManagerFactory(unitName, properties);

            factories.put(unitName, factory);
        }

        return factory;
    }

    public void setConfigs(List<PersistenceConfig> configs) {

        this.configs = configs;
    }

    public static PersistenceUnitHolder getInstance() {

        return instance;
    }

    private EntityManagerFactory defaultEntityManagerFactory() {
        if (defaultEntityMangerFactory == null) {
            Properties properties = new Properties();

            Optional<PersistenceConfig> config = configs.stream()
                    .findFirst();

            config.ifPresent(c -> {

                Optional.ofNullable(c.getUrl()).ifPresent(u -> properties.put("javax.persistence.jdbc.url", u));
                Optional.ofNullable(c.getDriver()).ifPresent(d -> properties.put("javax.persistence.jdbc.driver", d));
                Optional.ofNullable(c.getUsername()).ifPresent(u -> properties.put("javax.persistence.jdbc.user", u));
                Optional.ofNullable(c.getPassword()).ifPresent(p -> properties.put("javax.persistence.jdbc.password", p));
                
                defaultEntityMangerFactory = Persistence.createEntityManagerFactory("default", properties);
            });

        }
        return defaultEntityMangerFactory;

    }
}
