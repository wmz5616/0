package com.zemcho.guzhe.aspect.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ryan
 * @title: Log
 * @projectName master
 * @description: ZEMCHO
 * @date 2021/4/6 0006 17:24
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Log {
    String description() default "";

    String module() default "";
}
