package com.kumuluz.ee.jta.common.exceptions;

public class CannotRetrieveTxException extends IllegalStateException {

    public CannotRetrieveTxException(Throwable cause) {
        super("There was an error retrieving the current threads transaction.", cause);
    }
}
