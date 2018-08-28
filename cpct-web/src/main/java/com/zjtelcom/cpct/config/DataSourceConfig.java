package com.zjtelcom.cpct.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Package: com.zjtelcom.cpct.config
 * Description: TODO
 * author: zqchen
 * date: 2017/9/20 15:43
 * version: V1.0s
 */
@Configuration
@MapperScan(basePackages = "com.zjtelcom.cpct.dao", sqlSessionFactoryRef = "masterSqlSessionFactory")
public class DataSourceConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Primary
    @Bean(name = "masterSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource")DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath:mybatis/*/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "devTransactionManager")
    @Primary
    public DataSourceTransactionManager devTransactionManager()
            throws SQLException {
        return new DataSourceTransactionManager(dataSource());
    }

}
