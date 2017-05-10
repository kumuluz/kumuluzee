package com.kumuluz.ee.common.dependencies;

/**
 * @author Tilen Faganel
 * @since 2.3.0
 */
public enum EeExtensionType {

    CONFIG("Config"),
    DISCOVERY("Discovery");

    private final String name;

    EeExtensionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
