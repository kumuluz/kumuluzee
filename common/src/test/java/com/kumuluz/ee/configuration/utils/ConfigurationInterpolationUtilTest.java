package com.kumuluz.ee.configuration.utils;

import org.junit.Test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Tests for {@link ConfigurationInterpolationUtil}. More tests of interpolation (expressions) are in
 * kumuluzee-config-mp.
 *
 * @author Urban Malc
 * @since 4.1.0
 */
public class ConfigurationInterpolationUtilTest {

    private static final String TEST_VALUE = "TEST_VALUE";


    private Optional<String> identityResolver(String s) {
        return Optional.ofNullable(s);
    }

    private Optional<String> testValueResolver(String s) {
        return Optional.of(TEST_VALUE);
    }

    private Optional<String> emptyResolver(String s) {
        return Optional.empty();
    }

    private Function<String, Optional<String>> createMapResolver(Map<String, String> entries) {
        return s -> Optional.ofNullable(entries.get(s));
    }

    @Test
    public void noInterpolationTest() {

        assertEquals("test", ConfigurationInterpolationUtil.interpolateString("test", this::emptyResolver));
        assertEquals("", ConfigurationInterpolationUtil.interpolateString("", this::emptyResolver));
        assertEquals("${}", ConfigurationInterpolationUtil.interpolateString("\\${}", this::emptyResolver));
        assertEquals("${test}", ConfigurationInterpolationUtil.interpolateString("\\${test}", this::emptyResolver));
        assertEquals("${}${}", ConfigurationInterpolationUtil.interpolateString("\\${}\\${}", this::emptyResolver));
        assertEquals("${${}}", ConfigurationInterpolationUtil.interpolateString("\\${\\${}}", this::emptyResolver));
        assertEquals("${", ConfigurationInterpolationUtil.interpolateString("\\${", this::emptyResolver));
        assertEquals("abc${}", ConfigurationInterpolationUtil.interpolateString("abc\\${}", this::emptyResolver));
        assertEquals("${}def", ConfigurationInterpolationUtil.interpolateString("\\${}def", this::emptyResolver));
        assertEquals("abc${}def", ConfigurationInterpolationUtil.interpolateString("abc\\${}def", this::emptyResolver));
    }

    @Test
    public void simpleInterpolationTest() {

        assertEquals(TEST_VALUE, ConfigurationInterpolationUtil.interpolateString("${replaceme}", this::testValueResolver));
        assertEquals("abc" + TEST_VALUE, ConfigurationInterpolationUtil.interpolateString("abc${replaceme}", this::testValueResolver));
        assertEquals(TEST_VALUE + "def", ConfigurationInterpolationUtil.interpolateString("${replaceme}def", this::testValueResolver));
        assertEquals("abc" + TEST_VALUE + "def", ConfigurationInterpolationUtil.interpolateString("abc${replaceme}def", this::testValueResolver));
        assertEquals(TEST_VALUE + TEST_VALUE, ConfigurationInterpolationUtil.interpolateString("${replaceme}${replaceme}", this::testValueResolver));
        assertEquals(TEST_VALUE + " " + TEST_VALUE, ConfigurationInterpolationUtil.interpolateString("${replaceme} ${replaceme}", this::testValueResolver));
    }

    @Test
    public void nestedInterpolationTest() {

        assertEquals("i1", ConfigurationInterpolationUtil.interpolateString("${${i1}}", this::identityResolver));
        assertEquals("i1i2", ConfigurationInterpolationUtil.interpolateString("${i1${i2}}", this::identityResolver));
        assertEquals("i1i2i1", ConfigurationInterpolationUtil.interpolateString("${i1${i2}i1}", this::identityResolver));

        assertEquals("finalValue", ConfigurationInterpolationUtil.interpolateString("${prefix.${inner}.postfix}",
                this.createMapResolver(Map.of(
                        "inner", "innerval",
                        "prefix.innerval.postfix", "finalValue"
                ))));
    }

    @Test
    public void defaultValueInterpolationTest() {

        assertEquals("defaultValue", ConfigurationInterpolationUtil.interpolateString("${test:defaultValue}", this::emptyResolver));
        assertEquals("defaultValue", ConfigurationInterpolationUtil.interpolateString("${:defaultValue}", this::emptyResolver));
        assertEquals("", ConfigurationInterpolationUtil.interpolateString("${test:}", this::emptyResolver));
        assertEquals("", ConfigurationInterpolationUtil.interpolateString("${:}", this::emptyResolver));

        assertEquals("abc.defaultValue", ConfigurationInterpolationUtil.interpolateString("abc.${test:defaultValue}", this::emptyResolver));
        assertEquals("defaultValue.def", ConfigurationInterpolationUtil.interpolateString("${test:defaultValue}.def", this::emptyResolver));
        assertEquals("abc.defaultValue.def", ConfigurationInterpolationUtil.interpolateString("abc.${test:defaultValue}.def", this::emptyResolver));
    }

    @Test
    public void defaultValueAndNestedInterpolationTest() {

        assertEquals("defaultValue", ConfigurationInterpolationUtil.interpolateString("${first:${second:defaultValue}}", this::emptyResolver));
        assertEquals("secondValue", ConfigurationInterpolationUtil.interpolateString("${first:${second:defaultValue}}",
                this.createMapResolver(Map.of(
                        "second", "secondValue"
                ))));
        assertEquals("firstValue", ConfigurationInterpolationUtil.interpolateString("${first:${second:defaultValue}}",
                this.createMapResolver(Map.of(
                        "first", "firstValue"
                ))));

        assertEquals("secondDefault", ConfigurationInterpolationUtil.interpolateString("${${inner:firstDefault}:secondDefault}", this::emptyResolver));
        assertEquals("firstValue", ConfigurationInterpolationUtil.interpolateString("${${inner:firstDefault}:secondDefault}",
                this.createMapResolver(Map.of(
                        "firstDefault", "firstValue"
                ))));
        assertEquals("secondValue", ConfigurationInterpolationUtil.interpolateString("${${inner:firstDefault}:secondDefault}",
                this.createMapResolver(Map.of(
                        "inner", "second",
                        "second", "secondValue"
                ))));
    }
}