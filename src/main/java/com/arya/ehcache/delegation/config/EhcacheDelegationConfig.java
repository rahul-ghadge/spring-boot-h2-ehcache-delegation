package com.arya.ehcache.delegation.config;

import com.arya.ehcache.delegation.repository.impl.EhcacheSuperHeroRepositoryImpl;
import com.arya.ehcache.delegation.repository.SuperHeroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(Ehcache.class)
@ConditionalOnClass(Ehcache.class)
public class EhcacheDelegationConfig implements BeanPostProcessor {

    private final Ehcache ehcache;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof SuperHeroRepository) {
            log.info("*** Ehcache delegation with EhcacheSuperHeroRepositoryImpl class implementation in use ***");
            return new EhcacheSuperHeroRepositoryImpl((SuperHeroRepository) bean, ehcache);
        }
        return bean;
    }
}
