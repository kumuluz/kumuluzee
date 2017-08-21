package com.kumuluz.ee.jpa.common.injection;

import javax.persistence.EntityManager;

public class NonTxEntityManagerWrapper implements EntityManagerWrapper {

    private EntityManager em;

    public NonTxEntityManagerWrapper(EntityManager em) {
        this.em = em;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void close() {

        if (em.isOpen()) {
            em.close();
        }
    }
}
