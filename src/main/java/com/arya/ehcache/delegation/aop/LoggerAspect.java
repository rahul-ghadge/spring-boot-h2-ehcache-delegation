package com.arya.ehcache.delegation.aop;


import com.arya.ehcache.delegation.entities.SuperHero;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    @Before("@annotation(com.arya.ehcache.delegation.annotation.LogObjectBefore)")
    public void logSuperHeroBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof SuperHero) {
                SuperHero superHero = (SuperHero) arg;
                log.info("******* Super hero before :: {}", superHero);
                break;
            } else if (arg instanceof Integer) {
                log.info("******* Id before :: {}", arg);
                break;
            }
        }
    }

    @AfterReturning(value = "@annotation(com.arya.ehcache.delegation.annotation.LogObjectAfter)", returning = "result")
    public void logSuperHeroAfter(JoinPoint joinPoint, Object result) {
        Object[] args = joinPoint.getArgs();
        if (Objects.nonNull(result)) {
            if (result instanceof ResponseEntity) {
                ResponseEntity responseEntity = (ResponseEntity) result;

                if (responseEntity.getStatusCode().value() == 200)
                    log.info("******* Returning object :: {}", responseEntity.getBody());
                else
                    log.error("Something went wrong while logging...!");
            }
        }
    }
}