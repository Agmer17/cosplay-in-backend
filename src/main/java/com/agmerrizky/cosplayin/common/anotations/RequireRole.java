package com.agmerrizky.cosplayin.common.anotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {
    String value(); // THE VALUE OF THIS SHOULD BE ADMIN, MODERATOR, USER;
}
