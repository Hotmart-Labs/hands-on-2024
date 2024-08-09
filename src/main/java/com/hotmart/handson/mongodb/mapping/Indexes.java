package com.hotmart.handson.mongodb.mapping;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexes {

    Index[] value();
}
