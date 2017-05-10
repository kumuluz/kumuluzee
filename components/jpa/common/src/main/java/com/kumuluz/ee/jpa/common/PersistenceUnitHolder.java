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
