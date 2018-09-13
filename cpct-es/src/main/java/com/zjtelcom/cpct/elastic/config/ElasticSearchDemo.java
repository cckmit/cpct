package com.zjtelcom.cpct.elastic.config;


import com.zjtelcom.cpct.elastic.service.EsService;

import java.util.Date;

public class ElasticSearchDemo implements Runnable {

    private EsService esService;

    public ElasticSearchDemo(EsService esService) {
        this.esService = esService;
    }

    public void run() {
        System.out.println("开始："+new Date());
        for (int i =0; i<10000;i++){
            esService.add();
        }
        System.out.println("结束："+new Date());
    }


}
