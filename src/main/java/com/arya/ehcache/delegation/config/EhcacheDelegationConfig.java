package com.arya.ehcache.delegation.config;

import com.arya.ehcache.delegation.repository.impl.SuperHeroRepositoryImpl;
import com.arya.ehcache.delegation.repository.SuperHeroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.Ehcache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(Ehcache.class)
public class EhcacheDelegationConfig implements BeanPostProcessor {

    private final Ehcache ehcache;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof SuperHeroRepository)
            return new SuperHeroRepositoryImpl((SuperHeroRepository) bean, ehcache);

        return bean;
    }
}
