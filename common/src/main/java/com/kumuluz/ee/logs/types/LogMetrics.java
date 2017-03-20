/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.types;

/**
 * Kumuluz-logs logger interface
 *
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public class LogMetrics {

    private Long timerStart;

    public LogMetrics() {
        this.timerStart = System.currentTimeMillis();
    }

    public Long getTimeElapsed() {
        return System.currentTimeMillis()-this.timerStart;
    }
}
