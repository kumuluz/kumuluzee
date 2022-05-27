package com.kumuluz.ee.configuration.utils;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;

/**
 * Utility for performing configuration value interpolation. Independent from the actual config resolver, since it can
 * be used in kumuluzee-config-mp. The interpolation (syntax, default values, etc.) is performed according to the
 * MicroProfile Config specification.
 *
 * @author Urban Malc
 * @since 4.1.0
 */
public class ConfigurationInterpolationUtil {

    public static String interpolateString(String value, Function<String, Optional<String>> configResolver) {

        int numberOfReplacements = 0;

        InterpolationLocation interpolationLocation = getInnermostInterpolation(value);
        while (interpolationLocation != null) {

            if (numberOfReplacements > 10) {
                throw new IllegalArgumentException("Reached maximum limit of interpolations when interpolating value: " + value);
            }

            String interpolationKey = value.substring(interpolationLocation.startIndex + 2,
                    interpolationLocation.endIndex);
            String defaultValue = null;

            if (interpolationKey.contains(":")) {
                String[] tokens = interpolationKey.split(":", 2);
                interpolationKey = tokens[0];
                defaultValue = tokens[1];
            }

            String finalDefaultValue = defaultValue;
            Optional<String> replacement = configResolver
                    .apply(interpolationKey)
                    .or(() -> Optional.ofNullable(finalDefaultValue));

            if (replacement.isEmpty()) {
                throw new NoSuchElementException("Could not resolve interpolation: " + interpolationKey);
            }

            value = value.substring(0, interpolationLocation.startIndex) + replacement.get() +
                    value.substring(interpolationLocation.endIndex + 1);

            numberOfReplacements++;

            interpolationLocation = getInnermostInterpolation(value);
        }

        return value.replaceAll("\\\\\\$\\{", Matcher.quoteReplacement("${")); // replace \${ with ${
    }

    private static InterpolationLocation getInnermostInterpolation(String value) {

        Integer startIndex = null;

        for (int i = 0; i < value.length(); i++) {

            if (value.charAt(i) == '}' && startIndex != null) {
                return new InterpolationLocation(startIndex, i);
            }

            if (i < value.length() - 1 &&
                    value.charAt(i) == '$' && value.charAt(i + 1) == '{' &&
                    (i == 0 || value.charAt(i - 1) != '\\')) {

                startIndex = i;
            }
        }

        return null;
    }

    private static class InterpolationLocation {

        private final int startIndex;
        private final int endIndex;

        public InterpolationLocation(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }
    }
}
