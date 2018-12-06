package com.zjtelcom.cpct.elastic.controller;

import com.zjtelcom.cpct.elastic.util.ElasticsearchUtil;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@EnableAutoConfiguration
@RequestMapping("/esSearch")
public class EsSearchController {

    private static final Logger logger = LoggerFactory.getLogger(EsSearchController.class);

    @Autowired
    private TransportClient client;
    /**
     * 测试索引
     */
    private String indexName = "filebeat-bigdata";

    /**
     * 类型
     */
    private String esType = "external";

    /**
     * 本机id
     */
    private String localIp = "192.168.0.152";


    @RequestMapping("/index")
    public String createIndexWithMapping() throws IOException {
        ElasticsearchUtil.createIndexWithMapping("sky");
        return "OK";
    }

}
