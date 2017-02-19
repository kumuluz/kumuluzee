package com.kumuluz.ee.jpa.common.resources;

import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import javax.persistence.EntityManager;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceContextResourceFactory implements ResourceReferenceFactory<EntityManager> {

    private final EntityManager em;

    public PersistenceContextResourceFactory(javax.persistence.EntityManager em) {
        this.em = em;
    }

    @Override
    public ResourceReference<EntityManager> createResource() {

        return new PersistenceContextResource(em);
    }
}
