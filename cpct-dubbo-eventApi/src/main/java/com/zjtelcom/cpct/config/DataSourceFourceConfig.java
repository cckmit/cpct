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
 * Description: 配置第四数据源
 * author: linchao
 * date: 2018/8/24 11:43
 * version: V1.0s
 */
@Configuration
@MapperScan(basePackages = "com.zjtelcom.cpct_offer.dao", sqlSessionFactoryRef = "fourceSqlSessionFactory")
public class DataSourceFourceConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasourcefourth")
    public DataSource datasourceFourth() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "fourceSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("datasourceFourth") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:inst/*/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "fourceTransactionManager")
    public PlatformTransactionManager testTransactionManager(@Qualifier("datasourceFourth") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }

//    @Bean
//    @ConfigurationProperties(prefix = "spring.jta.atomikos.datasource_prd")
//    public DataSource datasourcePrd() {
//        return new AtomikosDataSourceBean();
//    }
//
//    @Bean(name = "prdSqlSessionFactory")
//    public SqlSessionFactory sqlSessionFactory(@Qualifier("datasourcePrd") DataSource dataSource) throws Exception {
//        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
//        sessionFactoryBean.setDataSource(dataSource);
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        sessionFactoryBean.setMapperLocations(resolver.getResources("classpath*:sync/*/*.xml"));
//        return sessionFactoryBean.getObject();
//    }

}
