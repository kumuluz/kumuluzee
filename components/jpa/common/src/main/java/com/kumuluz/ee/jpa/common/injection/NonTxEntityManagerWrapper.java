package com.kumuluz.ee.jpa.common.injection;


import jakarta.persistence.EntityManager;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
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
