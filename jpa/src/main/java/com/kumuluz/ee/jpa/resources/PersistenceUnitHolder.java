package com.kumuluz.ee.jpa.resources;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * @author Tilen
 */
public class PersistenceUnitHolder {

    private Map<String, EntityManagerFactory> factories = new HashMap<>();

    private static final PersistenceUnitHolder instance = new PersistenceUnitHolder();

    public synchronized EntityManagerFactory getEntityManagerFactory(String unitName) {

        EntityManagerFactory factory = factories.get(unitName);

        if (factory == null) {

            factory = Persistence.createEntityManagerFactory(unitName);

            factories.put(unitName, factory);
        }

        return factory;
    }

    public static PersistenceUnitHolder getInstance() {

        return instance;
    }
}
