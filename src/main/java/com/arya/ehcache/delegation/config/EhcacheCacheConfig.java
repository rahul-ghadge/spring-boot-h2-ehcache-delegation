package com.arya.ehcache.delegation.config;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@EnableCaching
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EhcacheCacheConfig {

    @Bean
    public CacheManager getCustomCacheManager() {
        CacheManager cacheManager = CacheManager.create(getEhCacheConfiguration());
        cacheManager.addCache(ehcache());
        return cacheManager;
    }

    @Primary
    @Bean
    public Ehcache ehcache() {
        CacheConfiguration cacheConfig = new CacheConfiguration("my-cache", 1000)
                .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
                .eternal(false)
                .timeToLiveSeconds(3600)
                .timeToIdleSeconds(3600);
        return new Cache(cacheConfig);
    }

    private net.sf.ehcache.config.Configuration getEhCacheConfiguration() {
        net.sf.ehcache.config.Configuration configuration = new net.sf.ehcache.config.Configuration();
        DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
        diskStoreConfiguration.setPath("java.io.tmpdir");
        configuration.addDiskStore(diskStoreConfiguration);
        return configuration;
    }
}
