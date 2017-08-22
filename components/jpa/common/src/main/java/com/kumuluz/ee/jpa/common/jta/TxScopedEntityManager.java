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

import com.kumuluz.ee.jta.common.utils.TxUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.metamodel.Metamodel;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import java.util.List;
import java.util.Map;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class TxScopedEntityManager implements EntityManager {

    private String unitName;
    private EntityManagerFactory emf;
    private SynchronizationType sync;
    private TransactionManager transactionManager;
    private TransactionSynchronizationRegistry transactionSynchronizationRegistry;
    private NonTxEntityManagerHolder nonTxEmHolder;

    public TxScopedEntityManager(String unitName, EntityManagerFactory emf, SynchronizationType sync, TransactionManager transactionManager, TransactionSynchronizationRegistry transactionSynchronizationRegistry, NonTxEntityManagerHolder nonTxEmHolder) {
        this.unitName = unitName;
        this.emf = emf;
        this.sync = sync;
        this.transactionManager = transactionManager;
        this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
        this.nonTxEmHolder = nonTxEmHolder;
    }

    @Override
    public void persist(Object entity) {

        validateActiveTransaction();

        getEntityManager().persist(entity);
    }

    @Override
    public <T> T merge(T entity) {

        validateActiveTransaction();

        return getEntityManager().merge(entity);
    }

    @Override
    public void remove(Object entity) {

        validateActiveTransaction();

        getEntityManager().remove(entity);
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {

        EntityManager em = getEntityManager();

        T result = em.find(entityClass, primaryKey);

        detachLoadedNonTxEntities(em);

        return result;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {

        EntityManager em = getEntityManager();

        T result = em.find(entityClass, primaryKey, properties);

        detachLoadedNonTxEntities(em);

        return result;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {

        EntityManager em = getEntityManager();

        T result = em.find(entityClass, primaryKey, lockMode);

        detachLoadedNonTxEntities(em);

        return result;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {

        EntityManager em = getEntityManager();

        T result = em.find(entityClass, primaryKey, lockMode, properties);

        detachLoadedNonTxEntities(em);

        return result;
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {

        EntityManager em = getEntityManager();

        T result = em.getReference(entityClass, primaryKey);

        detachLoadedNonTxEntities(em);

        return result;
    }

    @Override
    public void flush() {

        getEntityManager().flush();
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {

        getEntityManager().setFlushMode(flushMode);
    }

    @Override
    public FlushModeType getFlushMode() {

        return getEntityManager().getFlushMode();
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {

        getEntityManager().lock(entity, lockMode);
    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {

        getEntityManager().lock(entity, lockMode, properties);
    }

    @Override
    public void refresh(Object entity) {

        validateActiveTransaction();

        getEntityManager().refresh(entity);
    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {

        validateActiveTransaction();

        getEntityManager().refresh(entity, properties);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {

        validateActiveTransaction();

        getEntityManager().refresh(entity, lockMode);
    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {

        validateActiveTransaction();

        getEntityManager().refresh(entity, lockMode, properties);
    }

    @Override
    public void clear() {

        getEntityManager().clear();
    }

    @Override
    public void detach(Object entity) {

        getEntityManager().detach(entity);
    }

    @Override
    public boolean contains(Object entity) {

        return getEntityManager().contains(entity);
    }

    @Override
    public LockModeType getLockMode(Object entity) {

        return getEntityManager().getLockMode(entity);
    }

    @Override
    public void setProperty(String propertyName, Object value) {

        getEntityManager().setProperty(propertyName, value);
    }

    @Override
    public Map<String, Object> getProperties() {

        return getEntityManager().getProperties();
    }

    @Override
    public Query createQuery(String qlString) {

        EntityManager em = getEntityManager();

        Query query = em.createQuery(qlString);

        return detachLoadedNonTxQueryEntities(query, em);
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {

        EntityManager em = getEntityManager();

        TypedQuery<T> typedQuery = em.createQuery(criteriaQuery);

        return detachLoadedNonTxTypedQueryEntities(typedQuery, em);
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {

        return getEntityManager().createQuery(updateQuery);
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {

        return getEntityManager().createQuery(deleteQuery);
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {

        EntityManager em = getEntityManager();

        TypedQuery<T> typedQuery = em.createQuery(qlString, resultClass);

        return detachLoadedNonTxTypedQueryEntities(typedQuery, em);
    }

    @Override
    public Query createNamedQuery(String name) {

        EntityManager em = getEntityManager();

        Query query = em.createNamedQuery(name);

        return detachLoadedNonTxQueryEntities(query, em);
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {

        EntityManager em = getEntityManager();

        TypedQuery<T> typedQuery = em.createNamedQuery(name, resultClass);

        return detachLoadedNonTxTypedQueryEntities(typedQuery, em);
    }

    @Override
    public Query createNativeQuery(String sqlString) {

        EntityManager em = getEntityManager();

        Query query = em.createNativeQuery(sqlString);

        return detachLoadedNonTxQueryEntities(query, em);
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {

        EntityManager em = getEntityManager();

        Query query = em.createNativeQuery(sqlString, resultClass);

        return detachLoadedNonTxQueryEntities(query, em);
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {

        EntityManager em = getEntityManager();

        Query query = em.createNativeQuery(sqlString, resultSetMapping);

        return detachLoadedNonTxQueryEntities(query, em);
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {

        EntityManager em = getEntityManager();

        StoredProcedureQuery storedProcedureQuery = em.createNamedStoredProcedureQuery(name);

        return detachLoadedNonTxStoredProcedureQueryEntities(storedProcedureQuery, em);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {

        EntityManager em = getEntityManager();

        StoredProcedureQuery storedProcedureQuery = em.createStoredProcedureQuery(procedureName);

        return detachLoadedNonTxStoredProcedureQueryEntities(storedProcedureQuery, em);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {

        EntityManager em = getEntityManager();

        StoredProcedureQuery storedProcedureQuery = em.createStoredProcedureQuery(procedureName, resultClasses);

        return detachLoadedNonTxStoredProcedureQueryEntities(storedProcedureQuery, em);
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {

        EntityManager em = getEntityManager();

        StoredProcedureQuery storedProcedureQuery = em.createStoredProcedureQuery(procedureName, resultSetMappings);

        return detachLoadedNonTxStoredProcedureQueryEntities(storedProcedureQuery, em);
    }

    @Override
    public void joinTransaction() {

        getEntityManager().joinTransaction();
    }

    @Override
    public boolean isJoinedToTransaction() {

        return getEntityManager().isJoinedToTransaction();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {

        return getEntityManager().unwrap(cls);
    }

    @Override
    public Object getDelegate() {

        return getEntityManager().getDelegate();
    }

    @Override
    public void close() {

        throw new IllegalStateException("Cannot closed a transaction managed entity manager.");
    }

    @Override
    public boolean isOpen() {

        return getEntityManager().isOpen();
    }

    @Override
    public EntityTransaction getTransaction() {

        return getEntityManager().getTransaction();
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {

        return getEntityManager().getEntityManagerFactory();
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {

        return getEntityManager().getCriteriaBuilder();
    }

    @Override
    public Metamodel getMetamodel() {

        return getEntityManager().getMetamodel();
    }

    @Override
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {

        return getEntityManager().createEntityGraph(rootType);
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {

        return getEntityManager().createEntityGraph(graphName);
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {

        return getEntityManager().getEntityGraph(graphName);
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {

        return getEntityManager().getEntityGraphs(entityClass);
    }

    //// Private logic methods

    private EntityManager getEntityManager() {

        EntityManager em;

        if (TxUtils.isActive(transactionManager)) {

            em = (EntityManager) transactionSynchronizationRegistry.getResource(unitName);

            if (em == null) {

                em = createEntityManager();

                transactionSynchronizationRegistry.registerInterposedSynchronization(new TxSynchronization(em));
                transactionSynchronizationRegistry.putResource(unitName, em);
            } else {

                validateCompatibleSyncTypes(em);
            }
        } else {

            em = nonTxEmHolder.getEntityManager();

            if (em == null) {

                em = createEntityManager();

                nonTxEmHolder.setEntityManager(em);
            }
        }

        return em;
    }

    private EntityManager createEntityManager() {

        if (sync.equals(SynchronizationType.UNSYNCHRONIZED)) {

            return new SyncEntityManagerWrapper(emf.createEntityManager(sync), sync);
        }

        return emf.createEntityManager();
    }

    private void detachLoadedNonTxEntities(EntityManager em) {

        if (!TxUtils.isActive(transactionManager)) {

            em.clear();
        }
    }

    private Query detachLoadedNonTxQueryEntities(Query query, EntityManager em) {

        if (!TxUtils.isActive(transactionManager)) {

            return new NonTxQueryWrapper(query, em);
        }

        return query;
    }

    private <T> TypedQuery<T> detachLoadedNonTxTypedQueryEntities(TypedQuery<T> query, EntityManager em) {

        if (!TxUtils.isActive(transactionManager)) {

            return new NonTxTypedQueryWrapper<>(query, em);
        }

        return query;
    }

    private StoredProcedureQuery detachLoadedNonTxStoredProcedureQueryEntities(StoredProcedureQuery procedureQuery, EntityManager em) {

        if (!TxUtils.isActive(transactionManager)) {

            return new NonTxStoredProcedureQueryWrapper(procedureQuery, em);
        }

        return procedureQuery;
    }

    private void validateActiveTransaction() {

        if (!TxUtils.isActive(transactionManager)) {

            throw new TransactionRequiredException("An active transaction is required in order to perform this function.");
        }
    }

    private void validateCompatibleSyncTypes(EntityManager existingEm) {

        if (existingEm instanceof SyncEntityManagerWrapper &&
                ((SyncEntityManagerWrapper) existingEm).getSynchronizationType().equals(sync)) {

            throw new IllegalStateException("Incompatible SynchronizationType for the same PersistenceContext across " +
                    "multiple injection points. The SynchronizationType can not be SYNCHRONIZED if a " +
                    "previous one that is UNSYCHRONIZED already exists.");
        }
    }
}
