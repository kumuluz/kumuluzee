package com.kumuluz.ee.common.exceptions;

/**
 * @author Tilen
 */
public class ServletServerException extends RuntimeException {

    public ServletServerException(String msg) {

        super(msg);
    }

    public ServletServerException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
