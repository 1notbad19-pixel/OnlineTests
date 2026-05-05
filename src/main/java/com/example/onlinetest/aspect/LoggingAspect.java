package com.example.onlinetest.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j

public class LoggingAspect {

    @Around("execution(* com.example.onlinetest.service.*.*(..))")
  public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        log.info("▶️ Starting: {}", methodName);
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        log.info("✅ Completed: {} in {} ms", methodName, executionTime);
        return result;
    }
}