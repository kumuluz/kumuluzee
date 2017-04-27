package com.kumuluz.ee.jpa.common;

import com.kumuluz.ee.jpa.common.resources.PersistenceContextResourceFactory;
import com.kumuluz.ee.jpa.common.resources.PersistenceUnitHolder;
import com.kumuluz.ee.jpa.common.resources.PersistenceUnitResourceFactory;
import java.util.Map;
import java.util.TreeMap;
import org.jboss.weld.injection.spi.JpaInjectionServices;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import javax.annotation.Priority;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
@Priority(1)
public class JpaService implements JpaInjectionServices {

    private ResourceReferenceFactory<EntityManager> defaultPersistenceContextResourceFactory;
    private ResourceReferenceFactory<EntityManagerFactory> defaultPersistenceUnitResourceFactory;

    private final Map<String, PersistenceContextResourceFactory> pcResourceFactories = new TreeMap<>();
    private final Map<String, PersistenceUnitResourceFactory> puResourceFactories = new TreeMap<>();

    @Override
    public ResourceReferenceFactory<EntityManager> registerPersistenceContextInjectionPoint(InjectionPoint injectionPoint) {

        PersistenceContext pc = injectionPoint.getAnnotated().getAnnotation(PersistenceContext.class);

        if ("".equals(pc.unitName())) {
            if (defaultPersistenceContextResourceFactory == null) {
                defaultPersistenceContextResourceFactory = new PersistenceContextResourceFactory(PersistenceUnitHolder.getInstance()
                        .getEntityManagerFactory(pc.unitName()).createEntityManager());
            }
            return defaultPersistenceContextResourceFactory;
        }
        return persistenceContextResourceFactory(pc.unitName());
    }

    @Override
    public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint(InjectionPoint injectionPoint) {

        PersistenceUnit pu = injectionPoint.getAnnotated().getAnnotation(PersistenceUnit.class);

        if ("".equals(pu.unitName())) {
            if (defaultPersistenceUnitResourceFactory == null) {
                defaultPersistenceUnitResourceFactory = new PersistenceUnitResourceFactory(PersistenceUnitHolder.getInstance()
                        .getEntityManagerFactory(pu.unitName()));
            }
            return defaultPersistenceUnitResourceFactory;
        }
        return persistenceUnitResourceFactory(pu.unitName());
    }

    @Override
    public EntityManager resolvePersistenceContext(InjectionPoint injectionPoint) {

        throw new UnsupportedOperationException();
    }

    @Override
    public EntityManagerFactory resolvePersistenceUnit(InjectionPoint injectionPoint) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void cleanup() {
    }

    private ResourceReferenceFactory<EntityManager> persistenceContextResourceFactory(final String unitName) {
        if (pcResourceFactories.containsKey(unitName)) {
            return pcResourceFactories.get(unitName);
        }
        final EntityManagerFactory factory = PersistenceUnitHolder.getInstance()
                .getEntityManagerFactory(unitName);
        final PersistenceContextResourceFactory pcrfactory = new PersistenceContextResourceFactory(factory.createEntityManager());
        pcResourceFactories.put(unitName, pcrfactory);
        return pcrfactory;
    }

    private ResourceReferenceFactory<EntityManagerFactory> persistenceUnitResourceFactory(final String unitName) {
        if (puResourceFactories.containsKey(unitName)) {
            return puResourceFactories.get(unitName);
        }
        final EntityManagerFactory factory = PersistenceUnitHolder.getInstance()
                .getEntityManagerFactory(unitName);
        final PersistenceUnitResourceFactory purfactory = new PersistenceUnitResourceFactory(factory);
        puResourceFactories.put(unitName, purfactory);
        return purfactory;
    }
}
