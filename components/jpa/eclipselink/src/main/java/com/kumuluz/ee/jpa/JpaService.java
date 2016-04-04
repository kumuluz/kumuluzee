package com.kumuluz.ee.jpa;

import com.kumuluz.ee.jpa.resources.PersistenceContextResourceFactory;
import com.kumuluz.ee.jpa.resources.PersistenceUnitHolder;
import com.kumuluz.ee.jpa.resources.PersistenceUnitResourceFactory;

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

    @Override
    public ResourceReferenceFactory<EntityManager> registerPersistenceContextInjectionPoint
            (InjectionPoint injectionPoint) {

        PersistenceContext pc = injectionPoint.getAnnotated().getAnnotation(PersistenceContext
                .class);

        EntityManagerFactory factory = PersistenceUnitHolder.getInstance()
                .getEntityManagerFactory(pc.unitName());

        return new PersistenceContextResourceFactory(factory);
    }

    @Override
    public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint
            (InjectionPoint injectionPoint) {

        PersistenceUnit pu = injectionPoint.getAnnotated().getAnnotation(PersistenceUnit.class);

        EntityManagerFactory factory = PersistenceUnitHolder.getInstance()
                .getEntityManagerFactory(pu.unitName());

        return new PersistenceUnitResourceFactory(factory);
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
}
