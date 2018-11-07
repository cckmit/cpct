package com.zjtelcom.cpct;

/**
 * Created by huanghua on 2017/5/23.
 */

import com.ctzj.smt.bss.centralized.authenticate.config.HttpSessionConfig;
import com.ctzj.smt.bss.centralized.authenticate.config.WebSecurityConfig;
import com.ctzj.smt.bss.centralized.authenticate.security.userdetails.service.MyUserDetailsService;
import com.ctzj.smt.bss.centralized.exception.MyBasicErrorController;
import com.ctzj.smt.bss.centralized.exception.RestExceptionHandler;
import com.ctzj.smt.bss.centralized.web.advice.MyResponseBodyAdvice;
import com.ctzj.smt.bss.centralized.web.config.MyFilterConfig;
import com.ctzj.smt.bss.centralized.web.config.MyJacksonConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
//@EnableAutoConfiguration



//@ImportResource({ "classpath:dubbo/*.xml","classpath:task/*.xml" })
@ImportResource({ "classpath:dubbo/dubbo-dev.xml"})
//@ImportResource({ "classpath:dubbo/dubbo-pst.xml"})
@ComponentScan(value={"com.zjtelcom.cpct"})
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
@EnableScheduling
public class Application {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static Logger logger = LoggerFactory.getLogger(Application.class);
    // for local development, debug in IDE
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
        logger.info("***********CPCT*********启动**********");
    }


}

