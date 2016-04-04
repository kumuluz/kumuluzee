package com.kumuluz.ee.jpa.resources;

import org.jboss.weld.injection.spi.ResourceReference;

import javax.persistence.EntityManagerFactory;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceUnitResource implements ResourceReference<EntityManagerFactory> {

    private EntityManagerFactory emf;

    public PersistenceUnitResource(EntityManagerFactory emf) {

        this.emf = emf;
    }

    @Override
    public EntityManagerFactory getInstance() {

        return emf;
    }

    @Override
    public void release() {

    }
}
