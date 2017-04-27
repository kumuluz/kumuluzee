package com.kumuluz.ee.jpa.common.injection;

import com.kumuluz.ee.jpa.common.PersistenceUnitHolder;
import com.kumuluz.ee.jpa.common.PersistenceWrapper;
import com.kumuluz.ee.jpa.common.exceptions.NoDefaultPersistenceUnit;
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

        PersistenceUnitHolder holder = PersistenceUnitHolder.getInstance();

        PersistenceContext pc = injectionPoint.getAnnotated().getAnnotation(PersistenceContext
                .class);
        String unitName = pc.unitName();

        if (unitName.isEmpty()) {

            unitName = holder.getDefaultUnitName();

            if (unitName.isEmpty()) {
                throw new NoDefaultPersistenceUnit();
            }
        }

        PersistenceWrapper wrapper = holder.getEntityManagerFactory(unitName);

        return new PersistenceContextResourceFactory(wrapper.getEntityManagerFactory(),
                wrapper.getTransactionType(), pc.synchronization());
    }

    @Override
    public ResourceReferenceFactory<EntityManagerFactory> registerPersistenceUnitInjectionPoint
            (InjectionPoint injectionPoint) {

        PersistenceUnitHolder holder = PersistenceUnitHolder.getInstance();

        PersistenceUnit pu = injectionPoint.getAnnotated().getAnnotation(PersistenceUnit.class);
        String unitName = pu.unitName();

        if (unitName.isEmpty()) {

            unitName = holder.getDefaultUnitName();

            if (unitName.isEmpty()) {
                throw new NoDefaultPersistenceUnit();
            }
        }

        PersistenceWrapper wrapper = holder.getEntityManagerFactory(unitName);

        return new PersistenceUnitResourceFactory(wrapper.getEntityManagerFactory());
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
