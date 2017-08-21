package com.kumuluz.ee.jpa.common.jta;

import com.kumuluz.ee.jpa.common.injection.EntityManagerWrapper;
import com.kumuluz.ee.jpa.common.jta.NonTxEntityManagerHolder;

import javax.persistence.EntityManager;

public class TxScopedEntityManagerWrapper implements EntityManagerWrapper {

    private EntityManager em;
    private NonTxEntityManagerHolder nonTxEmHolder;

    public TxScopedEntityManagerWrapper(EntityManager em, NonTxEntityManagerHolder nonTxEmHolder) {
        this.em = em;
        this.nonTxEmHolder = nonTxEmHolder;
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void close() {

        EntityManager nonTxEm = nonTxEmHolder.getEntityManager();

        if (nonTxEm != null && nonTxEm.isOpen()) {

            nonTxEm.close();

            nonTxEmHolder.setEntityManager(null);
        }
    }
}
