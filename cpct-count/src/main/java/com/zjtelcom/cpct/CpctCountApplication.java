package com.zjtelcom.cpct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@ImportResource("classpath:dubbo/dubbo-${spring.profiles.active}.xml")
@ComponentScan(value={"com.zjtelcom.cpct"})
@SpringBootApplication
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class, MybatisAutoConfiguration.class})
public class CpctCountApplication {

	public static void main(String[] args) {
		SpringApplication.run(CpctCountApplication.class, args);
		System.out.println("cpct-count 启动完毕");
	}

}

