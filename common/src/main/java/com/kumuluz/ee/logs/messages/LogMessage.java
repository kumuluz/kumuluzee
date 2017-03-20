/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.messages;

import java.util.Map;

/**
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public interface LogMessage {

    Map<String, String> getFields();
    String getMessage();
}
