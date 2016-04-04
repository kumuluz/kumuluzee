package com.kumuluz.ee.jpa.resources;

import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceContextResourceFactory implements ResourceReferenceFactory<EntityManager> {

    private EntityManagerFactory emf;

    public PersistenceContextResourceFactory(javax.persistence.EntityManagerFactory emf) {

        this.emf = emf;
    }

    @Override
    public ResourceReference<EntityManager> createResource() {

        return new PersistenceContextResource(emf.createEntityManager());
    }
}
