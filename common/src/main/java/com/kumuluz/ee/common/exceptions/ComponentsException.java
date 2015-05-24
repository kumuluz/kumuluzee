package com.kumuluz.ee.common.exceptions;

/**
 * @author Tilen
 */
public class ComponentsException extends RuntimeException {

    public ComponentsException(String msg) {

        super(msg);
    }

    public ComponentsException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
