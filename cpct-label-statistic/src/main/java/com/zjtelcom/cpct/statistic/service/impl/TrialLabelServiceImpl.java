package com.zjtelcom.cpct.statistic.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.statistic.service.TrialLabelService;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class TrialLabelServiceImpl implements TrialLabelService {

    private Logger logger = LoggerFactory.getLogger(TrialLabelServiceImpl.class);

    @Autowired
    private TransportClient client;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Override
    public Map<String, Object> trialUerLabelLog(String s, String messageID, String key) {
        Map<String, Object> resultMap = new HashMap<>();
        boolean result = true;
        List list = JSON.parseObject(s, List.class);
        String[] split = key.split("_");
        String index = split[0] + split[1];
        //判断索引是否存在
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(index);
        IndicesExistsResponse inExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
        if (!inExistsResponse.isExists()) {
            result = createIndex(index);
        }
        //数据导入
        if(result) {
            try {
                //dubbo调用，获取用户全部标签
                //批量导入
//                BulkRequestBuilder bulkRequest = client.prepareBulk();
//                int count = 0;
//                for(int i=0;i<mapList.size();i++) {
//                    String id = list.get(i).toString();
//                    JSONObject jsonObject = new JSONObject();
//                    bulkRequest.add(client.prepareIndex(index, "doc", id).setSource(jsonObject));
//                    count++;
//                    if(count == 20) {
//                        bulkRequest.get();
//                        bulkRequest = client.prepareBulk();
//                        count = 0;
//                    }
//                }
            } catch(Exception e) {
                logger.error("标签入es库失败：" + e);
            }
        }
        return resultMap;
    }

    public boolean createIndex(String index) {
        boolean result = true;
        client.admin().indices().prepareCreate(index).setSettings(Settings.builder()
                .put("index.number_of_shards", 15)
                .put("index.number_of_replicas", 0)
                .put("index.refresh_interval", "30s")
                .put("index.translog.flush_threshold_size", "1g")
                .put("index.translog.sync_interval", "60s")
                .put("index.translog.durability", "async")
                .put("index.mapping.total_fields.limit", 2000)).get();
        XContentBuilder mapping = null;
        try {
            mapping = jsonBuilder().startObject().startObject("properties");
            List<Label> labelList = injectionLabelMapper.selectAll();
            Iterator<Label> iterator = labelList.iterator();
            while (iterator.hasNext()) {
                Label label = iterator.next();
                if (label != null) {
                    if (label.getInjectionLabelCode().equals("CPCP_BIRTH_DAY") || label.getInjectionLabelCode().equals("PROM_LIST")) {
                        mapping.startObject(label.getInjectionLabelCode()).field("type", "text").startObject("fields").startObject("keyword").field("type", "keyword").field("ignore_above", 256).endObject().endObject().endObject();
                        continue;
                    }
                    if ("1300".equals(label.getLabelDataType())) {
                        mapping.startObject(label.getInjectionLabelCode()).field("type", "double").endObject();
                    } else if ("1100".equals(label.getLabelDataType())) {
                        mapping.startObject(label.getInjectionLabelCode()).field("type", "date").endObject();
                    } else {
                        mapping.startObject(label.getInjectionLabelCode()).field("type", "text").startObject("fields").startObject("keyword").field("type", "keyword").field("ignore_above", 256).endObject().endObject().endObject();
                    }
                }
            }
            mapping.endObject().endObject();
            PutMappingRequest putMappingRequest = Requests.putMappingRequest(index).type("doc").source(mapping);
            client.admin().indices().putMapping(putMappingRequest).actionGet();
        } catch (IOException e) {
            logger.error("设置为索引Mapping失败！Exception = ", e);
            result = false;
        }
        mapping.close();
        return result;
    }
}
