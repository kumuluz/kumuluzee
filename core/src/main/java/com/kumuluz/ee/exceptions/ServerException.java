package com.kumuluz.ee.exceptions;

/**
 * @author Tilen
 */
public class ServerException extends RuntimeException {

    public ServerException(String msg) {

        super(msg);
    }

    public ServerException(String msg, Throwable cause) {

        super(msg, cause);
    }
}
