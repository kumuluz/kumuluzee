package com.kumuluz.ee.configuration.cdi;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation specifies key name for automatic initialisation of a field from configuration.
 *
 * @param value key name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ConfigValue {
    @Nonbinding String value() default "";
}
