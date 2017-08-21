package com.kumuluz.ee.jpa.common.jta;

import javax.persistence.EntityManager;

public class NonTxEntityManagerHolder {

    private EntityManager em;

    public EntityManager getEntityManager() {
        return em;
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }
}
