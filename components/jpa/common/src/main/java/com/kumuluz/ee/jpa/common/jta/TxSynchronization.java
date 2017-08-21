package com.kumuluz.ee.jpa.common.jta;

import javax.persistence.EntityManager;
import javax.transaction.Synchronization;

public class TxSynchronization implements Synchronization {

    private EntityManager em;

    public TxSynchronization(EntityManager em) {
        this.em = em;
    }

    @Override
    public void beforeCompletion() {
    }

    @Override
    public void afterCompletion(int status) {

        if (em != null && em.isOpen()) {

            em.close();
        }
    }
}
