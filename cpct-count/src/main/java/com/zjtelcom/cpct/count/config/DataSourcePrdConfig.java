package com.zjtelcom.cpct.count.config;

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
 * 第二数据源  操作cpc的数据库  主要是需求函方面
 */
@Configuration
@MapperScan(basePackages = "com.zjtelcom.cpct_offer.dao", sqlSessionFactoryRef = "prdSqlSessionFactory")
public class DataSourcePrdConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasourceprd")
    public DataSource datasourceprd() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "prdSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("datasourceprd") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:inst/*/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "prdTransactionManager")
    public PlatformTransactionManager testTransactionManager(@Qualifier("datasourceprd") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }



}
