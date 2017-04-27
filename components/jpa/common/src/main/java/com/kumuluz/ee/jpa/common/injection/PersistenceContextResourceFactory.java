package com.kumuluz.ee.jpa.common.injection;

import com.kumuluz.ee.jpa.common.TransactionType;
import org.jboss.weld.injection.spi.ResourceReference;
import org.jboss.weld.injection.spi.ResourceReferenceFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class PersistenceContextResourceFactory implements ResourceReferenceFactory<EntityManager> {

    private EntityManagerFactory emf;
    private SynchronizationType sync;
    private TransactionType transactionType;

    public PersistenceContextResourceFactory(EntityManagerFactory emf, TransactionType transactionType, SynchronizationType sync) {

        this.emf = emf;
        this.sync = sync;
        this.transactionType = transactionType;
    }

    @Override
    public ResourceReference<EntityManager> createResource() {

        EntityManager em;

        if (transactionType == TransactionType.JTA) {
            em = (EntityManager) TransactionalEntityManagerProxy.newInstance(emf.createEntityManager(sync));
        } else {
            em = emf.createEntityManager();
        }

        return new PersistenceContextResource(em);
    }
}