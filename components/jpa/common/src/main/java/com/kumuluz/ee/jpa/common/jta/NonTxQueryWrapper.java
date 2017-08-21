package com.kumuluz.ee.jpa.common.jta;

import javax.persistence.*;
import java.util.*;

public class NonTxQueryWrapper implements Query {

    private Query query;
    private EntityManager em;

    public NonTxQueryWrapper(Query query, EntityManager em) {
        this.query = query;
        this.em = em;
    }

    @Override
    public List getResultList() {

        List resultList = query.getResultList();

        em.clear();

        return resultList;
    }

    @Override
    public Object getSingleResult() {

        Object singleResult = query.getSingleResult();

        em.clear();

        return singleResult;
    }

    @Override
    public int executeUpdate() {
        return query.executeUpdate();
    }

    @Override
    public Query setMaxResults(int maxResult) {
        query.setMaxResults(maxResult);
        return this;
    }

    @Override
    public int getMaxResults() {
        return query.getMaxResults();
    }

    @Override
    public Query setFirstResult(int startPosition) {
        query.setFirstResult(startPosition);
        return this;
    }

    @Override
    public int getFirstResult() {
        return query.getFirstResult();
    }

    @Override
    public Query setHint(String hintName, Object value) {
        query.setHint(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return query.getHints();
    }

    @Override
    public <T> Query setParameter(Parameter<T> param, T value) {
        query.setParameter(param, value);
        return this;
    }

    @Override
    public Query setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public Query setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        query.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public Query setParameter(String name, Object value) {
        query.setParameter(name, value);
        return this;
    }

    @Override
    public Query setParameter(String name, Calendar value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public Query setParameter(String name, Date value, TemporalType temporalType) {
        query.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public Query setParameter(int position, Object value) {
        query.setParameter(position, value);
        return this;
    }

    @Override
    public Query setParameter(int position, Calendar value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Query setParameter(int position, Date value, TemporalType temporalType) {
        query.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return query.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return query.getParameter(name);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return query.getParameter(name, type);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return query.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return query.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return query.isBound(param);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return query.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return query.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return query.getParameterValue(position);
    }

    @Override
    public Query setFlushMode(FlushModeType flushMode) {
        query.setFlushMode(flushMode);
        return this;
    }

    @Override
    public FlushModeType getFlushMode() {
        return query.getFlushMode();
    }

    @Override
    public Query setLockMode(LockModeType lockMode) {
        query.setLockMode(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return query.getLockMode();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return query.unwrap(cls);
    }
}
