package com.kumuluz.ee.common.dependencies;

import java.lang.annotation.*;

/**
 * @author Tilen Faganel
 * @since 2.0.0
 */
@Repeatable(EeComponentDependencies.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EeComponentDependency {

    EeComponentType value();

    String[] implementations() default {};
}
