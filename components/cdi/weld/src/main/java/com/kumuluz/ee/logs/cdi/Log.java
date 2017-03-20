/**
 * Copyright (c) Sunesis d.o.o.
 */
package com.kumuluz.ee.logs.cdi;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author Rok Povse
 * @author Marko Skrjanec
 */
@Inherited
@InterceptorBinding
@Retention(RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Log {
    @Nonbinding LogParams[] value() default {};
    @Nonbinding boolean methodCall() default true;
}