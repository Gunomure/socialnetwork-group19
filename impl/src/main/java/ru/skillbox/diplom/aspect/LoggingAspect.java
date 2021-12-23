package ru.skillbox.diplom.aspect;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * ru.skillbox.diplom.service.*.*(..))")
    public void callAtServicePackage() {}

    @Pointcut("within(ru.skillbox.diplom.controller..*)")
    public void callAtControllerPackage() {}

    @Pointcut("within(ru.skillbox.diplom.repository..*)")
    public void callAtRepositoryPackage() {}

    @Pointcut("execution(public * ru.skillbox.diplom.websocket.*.*(..))")
    public void callAtWebSocketPackage() {}


    @Before("callAtServicePackage() || callAtWebSocketPackage() || callAtRepositoryPackage()")
    public void beforeAllPublicMethodsInService(JoinPoint jp){
        log.debug(jp.getSignature().getDeclaringType().getSimpleName() + "." + jp.getSignature().getName() + ": " + Arrays.toString(jp.getArgs()));
    }

    @Before("callAtControllerPackage()")
    public void beforeAllPublicMethodsInController(JoinPoint jp){
        log.info(jp.getSignature().getDeclaringType().getSimpleName() + "." + jp.getSignature().getName() + ": " + Arrays.toString(jp.getArgs()));
    }

    @AfterReturning(pointcut = "callAtServicePackage()", returning = "result")
    public void afterReturningAtAllPublicMethodsInController(JoinPoint jp, Object result){
        log.debug(jp.getSignature().getDeclaringType().getSimpleName() + "." + jp.getSignature().getName() + " has returned " + this.getValue(result));
    }

    @AfterThrowing(pointcut = "callAtServicePackage() || callAtControllerPackage() || callAtWebSocketPackage()", throwing = "exception")
    public void afterThrowing(JoinPoint jp, Throwable exception){
        log.error(exception.getClass().getSimpleName() + " has been thrown in " +
                jp.getSignature().getDeclaringType().getSimpleName() + "." + jp.getSignature().getName() + " and caused by: " + exception.getMessage());
    }

    private String getValue(Object result) {
        String returnValue = null;
        if (null != result) {
            if (result.toString().endsWith("@" + Integer.toHexString(result.hashCode()))) {
                returnValue = ReflectionToStringBuilder.toString(result);
            } else {
                returnValue = result.toString();
            }
        }
        return returnValue;
    }
}
