package com.kumuluz.ee.jpa.common.jta;

import javax.persistence.*;
import java.util.*;

public class NonTxStoredProcedureQueryWrapper implements StoredProcedureQuery {

    private StoredProcedureQuery storedProcedureQuery;
    private EntityManager em;

    public NonTxStoredProcedureQueryWrapper(StoredProcedureQuery storedProcedureQuery, EntityManager em) {
        this.storedProcedureQuery = storedProcedureQuery;
        this.em = em;
    }

    @Override
    public StoredProcedureQuery setHint(String hintName, Object value) {
        storedProcedureQuery.setHint(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return storedProcedureQuery.getHints();
    }

    @Override
    public <T> StoredProcedureQuery setParameter(Parameter<T> param, T value) {
        storedProcedureQuery.setParameter(param, value);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        storedProcedureQuery.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        storedProcedureQuery.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(String name, Object value) {
        storedProcedureQuery.setParameter(name, value);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(String name, Calendar value, TemporalType temporalType) {
        storedProcedureQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(String name, Date value, TemporalType temporalType) {
        storedProcedureQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(int position, Object value) {
        storedProcedureQuery.setParameter(position, value);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(int position, Calendar value, TemporalType temporalType) {
        storedProcedureQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public StoredProcedureQuery setParameter(int position, Date value, TemporalType temporalType) {
        storedProcedureQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return storedProcedureQuery.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return storedProcedureQuery.getParameter(name);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return storedProcedureQuery.getParameter(name, type);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return storedProcedureQuery.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return storedProcedureQuery.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return storedProcedureQuery.isBound(param);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return storedProcedureQuery.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return storedProcedureQuery.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return storedProcedureQuery.getParameterValue(position);
    }

    @Override
    public StoredProcedureQuery setFlushMode(FlushModeType flushMode) {
        storedProcedureQuery.setFlushMode(flushMode);
        return this;
    }

    @Override
    public FlushModeType getFlushMode() {
        return storedProcedureQuery.getFlushMode();
    }

    @Override
    public Query setLockMode(LockModeType lockMode) {
        storedProcedureQuery.setLockMode(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return storedProcedureQuery.getLockMode();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return storedProcedureQuery.unwrap(cls);
    }

    @Override
    public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
        storedProcedureQuery.registerStoredProcedureParameter(position, type, mode);
        return this;
    }

    @Override
    public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
        storedProcedureQuery.registerStoredProcedureParameter(parameterName, type, mode);
        return this;
    }

    @Override
    public Object getOutputParameterValue(int position) {
        return storedProcedureQuery.getOutputParameterValue(position);
    }

    @Override
    public Object getOutputParameterValue(String parameterName) {
        return storedProcedureQuery.getOutputParameterValue(parameterName);
    }

    @Override
    public boolean execute() {
        return storedProcedureQuery.execute();
    }

    @Override
    public int executeUpdate() {
        return storedProcedureQuery.executeUpdate();
    }

    @Override
    public Query setMaxResults(int maxResult) {
        storedProcedureQuery.setMaxResults(maxResult);
        return this;
    }

    @Override
    public int getMaxResults() {
        return storedProcedureQuery.getMaxResults();
    }

    @Override
    public Query setFirstResult(int startPosition) {
        storedProcedureQuery.setFirstResult(startPosition);
        return this;
    }

    @Override
    public int getFirstResult() {
        return storedProcedureQuery.getFirstResult();
    }

    @Override
    public List getResultList() {

        List resultList = storedProcedureQuery.getResultList();

        em.clear();

        return resultList;
    }

    @Override
    public Object getSingleResult() {

        Object singleResult = storedProcedureQuery.getSingleResult();

        em.clear();

        return singleResult;
    }

    @Override
    public boolean hasMoreResults() {
        return storedProcedureQuery.hasMoreResults();
    }

    @Override
    public int getUpdateCount() {
        return storedProcedureQuery.getUpdateCount();
    }
}
