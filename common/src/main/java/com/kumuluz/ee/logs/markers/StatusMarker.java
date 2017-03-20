/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.markers;

/**
 * @author Rok Povse
 * @author Marko Skrjanec
 */
public enum StatusMarker implements Marker {
    ENTRY("ENTRY"), EXIT("EXIT"), INVOKE("INVOKE"), RESPOND("RESPOND");

    private String marker;

    private StatusMarker(String marker) {
        this.marker = marker;
    }

    public String toString() {
        return marker;
    }
}
