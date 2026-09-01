package com.marketplace.classifieds.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class PerformanceLoggingAspect {

    private static final long THRESHOLD_MS = 5;

    @Around("within(com.marketplace.classifieds.adapter.in.web.controller..*)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startNanos = System.nanoTime();
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("{}.{}() started", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } finally {
            long duration = (System.nanoTime() - startNanos) / 1_000_000;

            log.info("{}.{}() finished in {} ms", className, methodName, duration);

            if (duration > THRESHOLD_MS) {
                log.warn("Slow API detected: {}.{}() took {} ms", className, methodName, duration);
            }
        }
        return result;
    }
}
