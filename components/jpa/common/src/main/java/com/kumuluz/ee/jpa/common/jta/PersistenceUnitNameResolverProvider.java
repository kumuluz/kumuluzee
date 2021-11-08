package com.kumuluz.ee.jpa.common.jta;

import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

public class PersistenceUnitNameResolverProvider {

    private static final Logger LOG = Logger.getLogger(PersistenceUnitNameResolverProvider.class.getSimpleName());

    private static PersistenceUnitNameResolver instance;

    public static PersistenceUnitNameResolver getInstance() {
        if (instance == null) {
            final Iterator<PersistenceUnitNameResolver> persistenceUnitNameResolverIt = ServiceLoader.load(PersistenceUnitNameResolver.class).iterator();
            if (persistenceUnitNameResolverIt.hasNext()) {
                instance = persistenceUnitNameResolverIt.next();
                if (persistenceUnitNameResolverIt.hasNext()) {
                    throw new KumuluzServerException("Ambiguous persistence unit name resolver.");
                }
                LOG.info("Persistence unit name resolver found: " + instance.getClass());
            } else {
                LOG.info("No persistence unit name resolver found, setting default one.");
                instance = DEFAULT_RESOLVER;
            }
        }
        return instance;
    }

    private static final PersistenceUnitNameResolver DEFAULT_RESOLVER = (unitName, context) -> unitName;

}
