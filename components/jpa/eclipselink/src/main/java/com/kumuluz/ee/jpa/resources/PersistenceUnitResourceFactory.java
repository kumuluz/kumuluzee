package com.kumuluz.ee.jpa.resources;

import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import javax.persistence.EntityManagerFactory;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceUnitResourceFactory implements
        ResourceReferenceFactory<EntityManagerFactory> {

    private EntityManagerFactory emf;

    public PersistenceUnitResourceFactory(EntityManagerFactory emf) {

        this.emf = emf;
    }

    @Override
    public ResourceReference<EntityManagerFactory> createResource() {

        return new PersistenceUnitResource(emf);
    }
}
