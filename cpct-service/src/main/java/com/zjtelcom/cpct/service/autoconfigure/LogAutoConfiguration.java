package com.zjtelcom.cpct.service.autoconfigure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(SysLogAspect.class)
@Slf4j
public class LogAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean(SysLogAspect.class)
    public SysLogAspect sysLogAspect() {
        log.info("日志bean启动加载");
        return new SysLogAspect();
    }

}
