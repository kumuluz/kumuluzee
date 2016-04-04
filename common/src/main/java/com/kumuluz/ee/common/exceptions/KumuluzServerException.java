package com.kumuluz.ee.common.exceptions;

/**
 * @author Tilen Faganel
 * @since 1.0.0
 */
public class KumuluzServerException extends RuntimeException {

    public KumuluzServerException(String msg) {

        super(msg);
    }

    public KumuluzServerException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
