package com.kumuluz.ee.jpa.common;

import javax.persistence.EntityManagerFactory;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class PersistenceWrapper {

    private EntityManagerFactory entityManagerFactory;
    private TransactionType transactionType;

    public PersistenceWrapper(EntityManagerFactory entityManagerFactory, TransactionType transactionType) {
        this.entityManagerFactory = entityManagerFactory;
        this.transactionType = transactionType;
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }
}
