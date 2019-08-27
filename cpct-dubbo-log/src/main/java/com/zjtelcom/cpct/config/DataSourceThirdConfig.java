package com.zjtelcom.cpct.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Package: com.zjtelcom.cpct.config
 * Description: 配置第三数据源
 * author: linchao
 * date: 2018/8/24 11:43
 * version: V1.0s
 */
@Configuration
@MapperScan(basePackages = "com.zjtelcom.cpct_prod.dao", sqlSessionFactoryRef = "thirdSqlSessionFactory")
public class DataSourceThirdConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasourcethird")
    public DataSource datasourceThird() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "thirdSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("datasourceThird") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:prod/*/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "thirdTransactionManager")
    public PlatformTransactionManager testTransactionManager(@Qualifier("datasourceThird") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }

}
