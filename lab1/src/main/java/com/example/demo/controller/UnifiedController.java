package com.example.demo.controller;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data")
public class UnifiedController {

    @GetMapping
    public String getData() {
        return "GET response";
    }

    @PostMapping
    public String postData() {
        return "POST response";
    }

    @PutMapping
    public String putData() {
        return "PUT response";
    }

    @DeleteMapping
    public String deleteData() {
        return "DELETE response";
    }

 // Inner static aspect class
//    @Aspect
//    @Component
//    public static class LoggingAspect {
//
//        @Before("execution(* com.example.demo.controller.UnifiedController.*(..))")
//        public void logBefore(JoinPoint joinPoint) {
//            System.out.println(">>> Aspect Triggered Before Method: " + joinPoint.getSignature().getName());
//        }
//    }
}
