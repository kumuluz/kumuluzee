package com.kumuluz.ee.jta.common.eclipselink;

import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public class KumuluzPlatform extends ServerPlatformBase {

    public KumuluzPlatform(DatabaseSession newDatabaseSession) {
        super(newDatabaseSession);
    }

    @Override
    public Class<?> getExternalTransactionControllerClass() {
        return KumuluzTransactionController.class;
    }

}
