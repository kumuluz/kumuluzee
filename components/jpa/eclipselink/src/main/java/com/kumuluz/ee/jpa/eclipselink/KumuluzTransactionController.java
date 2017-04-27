package com.kumuluz.ee.jpa.eclipselink;

import com.kumuluz.ee.jta.common.JtaTransactionHolder;
import org.eclipse.persistence.transaction.JTATransactionController;

import javax.transaction.TransactionManager;

/**
 * @author Marcos Koch Salvador
 * @since 2.3.0
 */
public class KumuluzTransactionController extends JTATransactionController {

    @Override
    protected TransactionManager acquireTransactionManager() throws Exception {
        return JtaTransactionHolder.getInstance().getTransactionManager();
    }
}
