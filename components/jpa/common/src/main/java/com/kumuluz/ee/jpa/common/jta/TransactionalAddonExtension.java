package com.kumuluz.ee.jpa.common.jta;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

public class TransactionalAddonExtension implements Extension {

    public static final String TRANSACTIONAL_ADDON = "-transactional-addon";

    public void register(@Observes BeforeBeanDiscovery bbd, BeanManager bm) {
        bbd.addAnnotatedType(bm.createAnnotatedType(TransactionalAddonContext.class), TransactionalAddonContext.class.getSimpleName() + TRANSACTIONAL_ADDON);
        bbd.addAnnotatedType(bm.createAnnotatedType(TransactionalAddonInterceptor.class), TransactionalAddonInterceptor.class.getName() + TRANSACTIONAL_ADDON);
    }

}
