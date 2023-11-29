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
import jakarta.persistence.EntityManager;


/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
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
