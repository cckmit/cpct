package com.zjtelcom.cpct.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;

/**
 * Package: com.zjpii.knowinterface.config
 * Description: TODO
 * author: zqchen
 * date: 2017/9/20 15:43
 * version: V1.0s
 */
@Configuration
public class DataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

}
