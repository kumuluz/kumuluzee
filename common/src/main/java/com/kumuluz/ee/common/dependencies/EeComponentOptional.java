package com.kumuluz.ee.common.dependencies;

import java.lang.annotation.*;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
@Repeatable(EeComponentOptionals.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EeComponentOptional {

    EeComponentType value();

    String[] implementations() default {};
}
