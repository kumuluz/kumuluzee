package com.kumuluz.ee.common.utils;

import com.kumuluz.ee.common.exceptions.KumuluzServerException;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Tilen
 */
public class EnvUtils {

    public static void getEnv(String var, Consumer<String> consumer) {

        Optional.ofNullable(System.getenv(var))
                .filter(s -> !s.isEmpty())
                .ifPresent(consumer::accept);
    }

    public static void getEnvAsInteger(String var, Consumer<Integer> consumer) {

        try {

            Optional.ofNullable(System.getenv(var))
                    .filter(s -> !s.isEmpty())
                    .ifPresent(s -> consumer.accept(Integer.parseInt(s)));
        } catch (NumberFormatException e) {

            throw new KumuluzServerException(var + "is in the incorrect format", e);
        }
    }
}
