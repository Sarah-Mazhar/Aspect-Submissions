package com.example.demo.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    long limit();  // max requests
    long duration();  // window length
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    String keyPrefix() default "";
}
