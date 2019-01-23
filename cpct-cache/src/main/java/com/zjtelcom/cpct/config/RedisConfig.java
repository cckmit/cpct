package com.zjtelcom.cpct.config;

import com.ctg.itrdc.cache.pool.CtgJedisPool;
import com.ctg.itrdc.cache.pool.CtgJedisPoolConfig;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description reids配置
 * @Author pengy
 * @Date 2018/7/4 10:36
 */
@Configuration
@EnableCaching
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${redisConfig.ip}")
    private String redisIp;
    @Value("${redisConfig.port}")
    private Integer redisPort;
    @Value("${redisConfig.database}")
    private Integer redisDataBase;
    @Value("${redisConfig.password}")
    private String redisPassword;


    /**
     * 集团定制redis注入
     *
     * @return
     */
    @Bean
    public CtgJedisPool ctgJedisPool() {
        List<HostAndPort> hostAndPortList = new ArrayList();
        // 接入机的ip和端口号
        String[] strArr = redisIp.split(",");
        for (String str : strArr) {
            HostAndPort host = new HostAndPort(str, redisPort);
            hostAndPortList.add(host);
        }
        HostAndPort host = new HostAndPort(redisIp, redisPort);
        hostAndPortList.add(host);

        GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(100); //最大空闲连接数
        poolConfig.setMaxTotal(300); // 最大连接数（空闲+使用中），不超过应用线程数，建议为应用线程数的一半
        poolConfig.setMinIdle(5); //保持的最小空闲连接数
        poolConfig.setMaxWaitMillis(3000);
        CtgJedisPoolConfig config = new CtgJedisPoolConfig(hostAndPortList);
        config.setDatabase(redisDataBase).setPassword(redisPassword).setPoolConfig(poolConfig).setPeriod(1000).setMonitorTimeout(100);

        return new CtgJedisPool(config);
    }

}
