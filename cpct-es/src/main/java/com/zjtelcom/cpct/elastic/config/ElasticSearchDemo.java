package com.zjtelcom.cpct.elastic.config;


import com.zjtelcom.cpct.elastic.service.EsHitService;

import java.util.Date;

public class ElasticSearchDemo implements Runnable {

    private EsHitService esService;

    public ElasticSearchDemo(EsHitService esService) {
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
