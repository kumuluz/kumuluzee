package com.kumuluz.ee.jpa.common;

import com.kumuluz.ee.common.config.PersistenceConfig;
import com.kumuluz.ee.jpa.common.utils.PersistenceUtils;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.*;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceUnitHolder {

    private PersistenceSettings providerProperties;
    private List<PersistenceConfig> configs;

    private String defaultUnitName;

    private Map<String, PersistenceWrapper> factories = new HashMap<>();

    private static final PersistenceUnitHolder instance = new PersistenceUnitHolder();

    private PersistenceUnitHolder() {

        defaultUnitName = PersistenceUtils.getDefaultUnitName().orElse("");
    }

    public static PersistenceUnitHolder getInstance() {
        return instance;
    }

    public synchronized PersistenceWrapper getEntityManagerFactory(String unitName) {

        PersistenceWrapper wrapper = factories.get(unitName);

        if (wrapper == null) {

            Properties properties = new Properties();

            if (providerProperties != null && providerProperties.getPersistenceUnitProperties() != null) {
                properties.putAll(providerProperties.getPersistenceUnitProperties());
            }

            Optional<PersistenceConfig> config = configs.stream()
                    .filter(c -> unitName.equals(c.getUnitName()))
                    .findFirst();

            config.ifPresent(c -> {

                Optional.ofNullable(c.getUrl()).ifPresent(u -> properties.put("javax.persistence.jdbc.url", u));
                Optional.ofNullable(c.getUsername()).ifPresent(u -> properties.put("javax.persistence.jdbc.user", u));
                Optional.ofNullable(c.getPassword()).ifPresent(p -> properties.put("javax.persistence.jdbc.password", p));
            });

            EntityManagerFactory factory = Persistence.createEntityManagerFactory(unitName, properties);
            TransactionType transactionType = PersistenceUtils.getEntityManagerFactoryTransactionType(factory);

            wrapper = new PersistenceWrapper(factory, transactionType);

            factories.put(unitName, wrapper);
        }

        return wrapper;
    }

    public String getDefaultUnitName() {
        return defaultUnitName;
    }

    public void setConfigs(List<PersistenceConfig> configs) {
        this.configs = configs;
    }

    public void setProviderProperties(PersistenceSettings providerProperties) {
        this.providerProperties = providerProperties;
    }
}
