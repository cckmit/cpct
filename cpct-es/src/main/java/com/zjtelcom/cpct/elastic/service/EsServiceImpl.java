package com.zjtelcom.cpct.elastic.service;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.elastic.util.ElasticsearchUtil;
import com.zjtelcom.cpct.elastic.util.EsSearchUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class EsServiceImpl implements EsService {

    protected Logger logger = LoggerFactory.getLogger(EsServiceImpl.class);

    /**
     * 测试索引
     */
    private String indexName="event";

    /**
     * 类型
     */
    private String esType="doc";


    @Override
    public void add() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id",System.currentTimeMillis()+ EsSearchUtil.getRandomStr(15));
        jsonObject.put("age", 25);
        jsonObject.put("name", "j-" + new Random(100).nextInt());
        jsonObject.put("date", new Date());
        for (int i = 0;i<850 ;i++){
            jsonObject.put("TEST"+i,i+1);
        }
        String id = ElasticsearchUtil.addData(jsonObject, indexName, esType, jsonObject.getString("id"));
        System.out.println("*********ID**********: "+id);
    }

    @Override
    public void save(JSONObject jsonObject,String indexName) {
        String id = ElasticsearchUtil.addData(jsonObject, indexName, esType, jsonObject.getString("ISI"));

        logger.info("test..."+id);
    }


}
