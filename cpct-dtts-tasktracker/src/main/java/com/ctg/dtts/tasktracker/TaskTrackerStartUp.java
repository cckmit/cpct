package com.ctg.dtts.tasktracker;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TaskTrackerStartUp {

    public static void main(String[] args) {
        System.out.println("multitasktracker begin");
        new ClassPathXmlApplicationContext("/tasktracker-multitask-spring-xml.xml",
                "/application-database.xml", "/application-mybatis.xml", "/dubbo-prod.xml");
    }
}
