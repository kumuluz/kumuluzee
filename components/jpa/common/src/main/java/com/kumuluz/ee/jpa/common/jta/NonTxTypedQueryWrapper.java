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

import jakarta.persistence.*;

import java.util.*;

/**
 * @author Tilen Faganel
 * @since 2.4.0
 */
public class NonTxTypedQueryWrapper<X> implements TypedQuery<X> {

    private TypedQuery<X> typedQuery;
    private EntityManager em;

    public NonTxTypedQueryWrapper(TypedQuery<X> typedQuery, EntityManager em) {
        this.typedQuery = typedQuery;
        this.em = em;
    }

    @Override
    public List<X> getResultList() {

        List<X> resultList = typedQuery.getResultList();

        em.clear();

        return resultList;
    }

    @Override
    public X getSingleResult() {

        X singleResult = typedQuery.getSingleResult();

        em.clear();

        return singleResult;
    }

    @Override
    public int executeUpdate() {
        return typedQuery.executeUpdate();
    }

    @Override
    public TypedQuery<X> setMaxResults(int maxResult) {
        typedQuery.setMaxResults(maxResult);
        return this;
    }

    @Override
    public int getMaxResults() {
        return typedQuery.getMaxResults();
    }

    @Override
    public TypedQuery<X> setFirstResult(int startPosition) {
        typedQuery.setFirstResult(startPosition);
        return this;
    }

    @Override
    public int getFirstResult() {
        return typedQuery.getFirstResult();
    }

    @Override
    public TypedQuery<X> setHint(String hintName, Object value) {
        typedQuery.setHint(hintName, value);
        return this;
    }

    @Override
    public Map<String, Object> getHints() {
        return typedQuery.getHints();
    }

    @Override
    public <T> TypedQuery<X> setParameter(Parameter<T> param, T value) {
        typedQuery.setParameter(param, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        typedQuery.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        typedQuery.setParameter(param, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Object value) {
        typedQuery.setParameter(name, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType) {
        typedQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType) {
        typedQuery.setParameter(name, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Object value) {
        typedQuery.setParameter(position, value);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Calendar value, TemporalType temporalType) {
        typedQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public TypedQuery<X> setParameter(int position, Date value, TemporalType temporalType) {
        typedQuery.setParameter(position, value, temporalType);
        return this;
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return typedQuery.getParameters();
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return typedQuery.getParameter(name);
    }

    @Override
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        return typedQuery.getParameter(name, type);
    }

    @Override
    public Parameter<?> getParameter(int position) {
        return typedQuery.getParameter(position);
    }

    @Override
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        return typedQuery.getParameter(position, type);
    }

    @Override
    public boolean isBound(Parameter<?> param) {
        return typedQuery.isBound(param);
    }

    @Override
    public <T> T getParameterValue(Parameter<T> param) {
        return typedQuery.getParameterValue(param);
    }

    @Override
    public Object getParameterValue(String name) {
        return typedQuery.getParameterValue(name);
    }

    @Override
    public Object getParameterValue(int position) {
        return typedQuery.getParameterValue(position);
    }

    @Override
    public TypedQuery<X> setFlushMode(FlushModeType flushMode) {
        typedQuery.setFlushMode(flushMode);
        return this;
    }

    @Override
    public FlushModeType getFlushMode() {
        return typedQuery.getFlushMode();
    }

    @Override
    public TypedQuery<X> setLockMode(LockModeType lockMode) {
        typedQuery.setLockMode(lockMode);
        return this;
    }

    @Override
    public LockModeType getLockMode() {
        return typedQuery.getLockMode();
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return typedQuery.unwrap(cls);
    }
}
