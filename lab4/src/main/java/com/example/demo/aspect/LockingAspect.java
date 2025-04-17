package com.example.demo.aspect;

import com.example.demo.annotation.DistributedLock;
import com.example.demo.exception.LockAcquisitionException;
import com.example.demo.service.RedisClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.UUID;

@Aspect
@Component
public class LockingAspect {

    @Autowired
    private RedisClient redisClient;

    private final ExpressionParser parser = new SpelExpressionParser();

    @Around("@annotation(distributedLock)")
    public Object handleLock(ProceedingJoinPoint joinPoint, DistributedLock distributedLock) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        String evaluatedKey = parser.parseExpression(distributedLock.keyIdentifierExpression()).getValue(context, String.class);
        String lockKey = "lock:" + distributedLock.keyPrefix() + ":" + evaluatedKey;
        String lockValue = UUID.randomUUID().toString();

        Duration timeout = Duration.of(distributedLock.leaseTime(), distributedLock.timeUnit().toChronoUnit());
        boolean acquired = Boolean.TRUE.equals(redisClient.setIfAbsent(lockKey, lockValue, timeout));

        if (!acquired) {
            throw new LockAcquisitionException("Resource is currently locked");
        }

        try {
            return joinPoint.proceed();
        } finally {
            // optional: compare value and release
            redisClient.delete(lockKey);
        }
    }
}
