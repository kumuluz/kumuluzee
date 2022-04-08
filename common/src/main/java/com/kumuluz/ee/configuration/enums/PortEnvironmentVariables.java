package com.kumuluz.ee.configuration.enums;

import java.util.Collections;
import java.util.stream.Stream;

public enum PortEnvironmentVariables {

    /*
     * The values enumerated on top will have the highest priority, i.e. if multiple env variables are
     * defined, the one enumerated in this file on top will be the one to be used
     */
    AZURE_SERVERLESS_PORT_ENV_VAR("FUNCTIONS_CUSTOMHANDLER_PORT"),
    SHORT_PORT_ENV("PORT");

    private final String envVarName;

    PortEnvironmentVariables(String envVarName) {
        this.envVarName = envVarName;
    }

    public static Stream<String> stream() {
        return Stream.of(PortEnvironmentVariables.values())
                .sorted(Collections.reverseOrder())
                .map(var -> var.envVarName);
    }
}
