package com.zjtelcom.cpct;

/**
 * Created by huanghua on 2017/5/23.
 */

import com.ctzj.smt.bss.centralized.authenticate.config.HttpSessionConfig;
import com.ctzj.smt.bss.centralized.authenticate.config.WebSecurityConfig;
import com.ctzj.smt.bss.centralized.authenticate.security.userdetails.service.MyUserDetailsService;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.servlet.MultipartConfigElement;


@SpringBootApplication
@ImportResource("classpath:dubbo/dubbo-${spring.profiles.active}.xml")
@Import({WebSecurityConfig.class, HttpSessionConfig.class, MyUserDetailsService.class})
@ComponentScan(value = {"com.zjtelcom.cpct"})
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

    /**
     * 文件上传配置
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //单个文件最大
        factory.setMaxFileSize("307200"); //KB,MB
        /// 设置总上传数据总大小
        factory.setMaxRequestSize("307200");
        return factory.createMultipartConfig();
    }

}


