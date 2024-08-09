package com.hotmart.handson.mongodb.mapping;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectionOptions {

    boolean capped() default false;

    long size() default Integer.MAX_VALUE;

    long maxDocuments() default Integer.MAX_VALUE;
}
