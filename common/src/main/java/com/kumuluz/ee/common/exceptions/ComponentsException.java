package com.kumuluz.ee.common.exceptions;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class ComponentsException extends RuntimeException {

    public ComponentsException(String msg) {

        super(msg);
    }

    public ComponentsException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
