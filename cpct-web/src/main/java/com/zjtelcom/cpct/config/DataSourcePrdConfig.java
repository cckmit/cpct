package com.zjtelcom.cpct.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * Package: com.zjtelcom.cpct.config
 * Description: 配置第二数据源
 * author: linchao
 * date: 2018/8/24 11:43
 * version: V1.0s
 */
@Configuration
@MapperScan(basePackages = "com.zjtelcom.cpct_prd.dao", sqlSessionFactoryRef = "prdSqlSessionFactory")
public class DataSourcePrdConfig {


    @Bean
    @ConfigurationProperties(prefix = "spring.datasource_prd")
    public DataSource datasourcePrd() {
        return DataSourceBuilder.create().type(DruidDataSource.class).build();
    }

    @Bean(name = "prdSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("datasourcePrd") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource);
        sessionFactoryBean.setMapperLocations(new PathMatchingResourcePatternResolver()
                .getResources("classpath*:sync/*/*.xml"));
        return sessionFactoryBean.getObject();
    }

    @Bean(name = "prodTransactionManager")
    public DataSourceTransactionManager prodTransactionManager()
            throws SQLException {
        return new DataSourceTransactionManager(datasourcePrd());
    }
}
