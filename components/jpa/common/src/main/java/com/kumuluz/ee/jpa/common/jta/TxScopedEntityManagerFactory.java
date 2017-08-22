/*
 *  Copyright (c) 2014-2017 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
*/
package com.kumuluz.ee.jpa.common.jta;

import com.kumuluz.ee.jpa.common.injection.EntityManagerWrapper;
import com.kumuluz.ee.jta.common.JtaTransactionHolder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.SynchronizationType;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class TxScopedEntityManagerFactory {

    public static EntityManagerWrapper buildEntityManagerWrapper(String unitName, EntityManagerFactory emf, SynchronizationType sync) {

        JtaTransactionHolder jtaHolder = JtaTransactionHolder.getInstance();

        TransactionManager transactionManager = jtaHolder.getTransactionManager();
        TransactionSynchronizationRegistry transactionSynchronizationRegistry = jtaHolder.getTransactionSynchronizationRegistry();

        NonTxEntityManagerHolder emHolder = new NonTxEntityManagerHolder();

        EntityManager em = new TxScopedEntityManager(unitName, emf, sync, transactionManager, transactionSynchronizationRegistry, emHolder);

        return new TxScopedEntityManagerWrapper(em, emHolder);
    }
}
