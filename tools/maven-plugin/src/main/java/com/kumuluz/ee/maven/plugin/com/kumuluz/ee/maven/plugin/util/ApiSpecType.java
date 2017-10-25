package com.kumuluz.ee.maven.plugin.com.kumuluz.ee.maven.plugin.util;

/**
 * ApiSpecType enum.
 *
 * @author Zvone Gazvoda
 * @since 2.5.0
 */
public enum ApiSpecType {

    OPENAPI("openapi-configuration.json"),
    SWAGGER("swagger-configuration.json");

    private final String value;

    private ApiSpecType(final String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
