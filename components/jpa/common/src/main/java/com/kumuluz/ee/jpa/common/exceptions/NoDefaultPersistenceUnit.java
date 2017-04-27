package com.kumuluz.ee.jpa.common.exceptions;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public class NoDefaultPersistenceUnit extends RuntimeException {

    public NoDefaultPersistenceUnit() {
        super("Cannot determine a default persistence unit. Either the configuration does not exist or " +
                "there are more then one configurations or units defined");
    }
}
