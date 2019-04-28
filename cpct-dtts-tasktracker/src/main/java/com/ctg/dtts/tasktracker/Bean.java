package com.ctg.dtts.tasktracker;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

public class Bean implements FactoryBean {

    @Override
    public Object getObject() throws Exception {
        return new RestTemplate();
    }

    @Override
    public Class getObjectType() {
        return RestTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
