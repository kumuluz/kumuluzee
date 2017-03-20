/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.messages;

import java.util.Map;

/**
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public class SimpleLogMessage implements MethodCallLogMessage, MethodCallExitLogMessage, ResourceInvokeLogMessage,
        ResourceInvokeEndLogMessage {

    private String message;

    private Map<String, String> fields;

    @Override
    public Map<String, String> getFields() {
        return fields;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }
}
