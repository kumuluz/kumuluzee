package com.kumuluz.ee.common.exceptions;

/**
 * @author Tilen
 */
public class KumuluzServerException extends RuntimeException {

    public KumuluzServerException(String msg) {

        super(msg);
    }

    public KumuluzServerException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
