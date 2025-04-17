package com.example.demo.aspect;

import com.example.demo.annotation.RateLimit;
import com.example.demo.exception.RateLimitExceededException;
import com.example.demo.service.RedisClient;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@Order(1)
public class RateLimitAspect {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private HttpServletRequest request;



    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String ip = request.getRemoteAddr();
        String key = "rate:" + rateLimit.keyPrefix() + ":" + ip;
        long duration = rateLimit.timeUnit().toSeconds(rateLimit.duration());

        Long current = redisClient.increment(key, Duration.ofSeconds(duration));

        if (current != null && current > rateLimit.limit()) {
            throw new RateLimitExceededException("Too many requests. Please wait.");
        }

        return joinPoint.proceed();
    }


}
