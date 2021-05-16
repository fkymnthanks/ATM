package com.openmhealth.server.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target({ElementType.TYPE}) // 表示应用于接口、类、枚举、注解
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    Class<?> value();
}
