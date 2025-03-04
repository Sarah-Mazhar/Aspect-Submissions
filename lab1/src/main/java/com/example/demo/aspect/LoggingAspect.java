package com.example.demo.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    @Before("execution(* com.example.demo.controller.UnifiedController.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println(">>> Aspect Triggered Before Method: " + joinPoint.getSignature().getName());
    }
}
