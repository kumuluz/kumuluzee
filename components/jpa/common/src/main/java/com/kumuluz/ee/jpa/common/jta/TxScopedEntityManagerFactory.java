package com.kumuluz.ee.jpa.common.jta;

import com.kumuluz.ee.jpa.common.injection.EntityManagerWrapper;
import com.kumuluz.ee.jta.common.JtaTransactionHolder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

public class TxScopedEntityManagerFactory {

    public static EntityManagerWrapper buildEntityManagerWrapper(String unitName, EntityManagerFactory emf, SynchronizationType sync) {

        JtaTransactionHolder jtaHolder = JtaTransactionHolder.getInstance();

        TransactionManager transactionManager = jtaHolder.getTransactionManager();
        TransactionSynchronizationRegistry transactionSynchronizationRegistry = jtaHolder.getTransactionSynchronizationRegistry();

        NonTxEntityManagerHolder emHolder = new NonTxEntityManagerHolder();

        EntityManager em = new TxScopedEntityManager(unitName, emf, sync, transactionManager, transactionSynchronizationRegistry, emHolder);

        return new TxScopedEntityManagerWrapper(em, emHolder);
    }
}
