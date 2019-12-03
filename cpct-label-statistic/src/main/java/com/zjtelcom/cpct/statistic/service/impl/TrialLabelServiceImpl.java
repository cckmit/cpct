package com.zjtelcom.cpct.statistic.service.impl;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.statistic.service.TrialLabelService;
import com.zjtelcom.es.es.service.EsServiceInfo;
import net.sf.json.JSONArray;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
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
    @Autowired(required = false)
    private EsServiceInfo esServiceInfo;

    private String esType = "doc";

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
                Map map = esServiceInfo.queryCustomerByList(list);
                if (map!=null && map.get("resultCode").toString().equals("200")) {
                    List<Map<String,Object>> resultData =( List<Map<String,Object>>) map.get("resultData");
                    JSONArray jsonArray = JSONArray.fromObject(resultData);
                    IndexResponse response = client.prepareIndex(index, esType).setSource(jsonArray).get();
                    System.out.println(response.status().getStatus());
                }
            } catch(Exception e) {
                logger.error("标签入es库失败：" + e);
            }
        }
        return resultMap;
    }

    public boolean createIndex(String index) {
        boolean result = true;
        client.admin().indices().prepareCreate(index).setSettings(Settings.builder()
                .put("index.number_of_shards", 15) //数据分片数
                .put("index.number_of_replicas", 0) //数据备份数，如果只有一台机器，设置为0
                .put("index.refresh_interval", "30s") //每个索引的刷新频率
                .put("index.translog.flush_threshold_size", "1g") //事务日志大小到达此预设值，则执行flush
                .put("index.translog.sync_interval", "60s") //保证操作不会丢失  写入的数据被缓存到内存中，再每60秒执行一次 fsync
                .put("index.translog.durability", "async") //如果你不确定这个行为的后果，最好是使用默认的参数（ "index.translog.durability": "request" ）来避免数据丢失。
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
