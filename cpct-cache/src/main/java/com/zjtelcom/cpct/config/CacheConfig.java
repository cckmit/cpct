package com.zjtelcom.cpct.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: linchao
 * @date: 2020/08/11 17:48
 * @version: V1.0
 */
@Configuration
@EnableCaching
public class CacheConfig {
    /**
     * 配置缓存管理器
     *
     * @return 缓存管理器
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(300, TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(300)
                // 缓存的最大条数
                .maximumSize(3000)
                .build();
    }
}
