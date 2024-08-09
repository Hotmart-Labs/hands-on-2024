package com.hotmart.handson.mongodb.mapping;

import org.springframework.data.domain.Sort.Direction;

import java.lang.annotation.*;

@Documented
@Repeatable(Indexes.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String field();

    Direction direction() default Direction.ASC;

    boolean unique() default false;
}
