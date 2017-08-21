package com.kumuluz.ee.jpa.common.injection;

import javax.persistence.EntityManager;

public interface EntityManagerWrapper {

    EntityManager getEntityManager();

    void close();
}
