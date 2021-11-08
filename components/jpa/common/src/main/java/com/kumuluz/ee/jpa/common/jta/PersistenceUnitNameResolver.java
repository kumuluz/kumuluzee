package com.kumuluz.ee.jpa.common.jta;

public interface PersistenceUnitNameResolver {

    /**
     * Resolve persistence unit name regarding transactional addon context.
     * @param unitName original unit name found
     * @param context transactional addon context, can be null if no transaction or no addon
     * @return a persistence unit name
     */
    String resolve(String unitName, TransactionalAddonContext context);

}
