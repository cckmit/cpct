package com.zjtelcom.cpct.elastic.service;

import com.alibaba.fastjson.JSONObject;

public interface EsService {

    void add();

    void save(JSONObject jsonObject,String indexName);
}
