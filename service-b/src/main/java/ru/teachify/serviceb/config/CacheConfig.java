package ru.teachify.serviceb.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @ConditionalOnClass(name = "com.github.benmanes.caffeine.cache.Caffeine")
    public CacheManager caffeineCacheManager() {
        com.github.benmanes.caffeine.cache.Caffeine<Object, Object> caffeine =
                com.github.benmanes.caffeine.cache.Caffeine.newBuilder()
                        .expireAfterWrite(java.time.Duration.ofHours(23))
                        .maximumSize(100);

        org.springframework.cache.caffeine.CaffeineCacheManager cm = new org.springframework.cache.caffeine.CaffeineCacheManager("oauth2Tokens");
        cm.setCaffeine(caffeine);
        return cm;
    }

    @Bean
    @ConditionalOnMissingBean(CacheManager.class)
    public CacheManager simpleCacheManager() {
        // Fallback: простой ConcurrentMapCacheManager (не требует внешних зависимостей)
        return new ConcurrentMapCacheManager("oauth2Tokens");
    }
}
