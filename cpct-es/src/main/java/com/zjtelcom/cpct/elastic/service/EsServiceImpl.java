package com.zjtelcom.cpct.elastic.service;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.elastic.model.CampaignHitParam;
import com.zjtelcom.cpct.elastic.model.CampaignHitResponse;
import com.zjtelcom.cpct.elastic.util.ElasticsearchUtil;
import com.zjtelcom.cpct.elastic.util.EsSearchUtil;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.elastic.config.IndexList.EVENT_MODULE;
import static com.zjtelcom.cpct.elastic.config.IndexList.Label_MODULE;

@Service
public class EsServiceImpl implements EsService {

    protected Logger logger = LoggerFactory.getLogger(EsServiceImpl.class);

    @Autowired
    private TransportClient client;

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


    /**
     * 活动命中查询
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> searchCampaignHitsInfo(CampaignHitParam param) {
        Map<String,Object> result = new HashMap<>();
        CampaignHitResponse response = new CampaignHitResponse();

        //查询事件信息
        List<Map<String,Object>> eventList = new ArrayList<>();
        SearchHits hits = searchEventByParam(param);
        for(int j = 0; j < hits.getHits().length; j++) {
            String source = hits.getHits()[j].getSourceAsString();
            System.out.println(source);
            Map<String, Object> stringMap = hits.getHits()[j].getSourceAsMap();
            System.currentTimeMillis();
            eventList.add(stringMap);
        }

        //查询标签信息
        List<Map<String,Object>> labelList = new ArrayList<>();
        //查询事件后通过事件isi查询标签信息
        param.setISI(eventList.get(0).get("ISI").toString());
        SearchHits labelHits = searchLabelByParam(param);
        for(int j = 0; j < labelHits.getHits().length; j++) {
            String source = labelHits.getHits()[j].getSourceAsString();
            System.out.println(source);
            Map<String, Object> stringMap = labelHits.getHits()[j].getSourceAsMap();
            labelList.add(stringMap);
        }
        Map<String,Object> activityList = new HashMap<>();
        activityList.put("activityList", eventList.get(0).get("activityList"));
        response.setCampaignInfo(activityList);
        eventList.get(0).remove("activityList");
        response.setLabelInfo(labelList);
        response.setEventInfo(eventList.get(0));
        result.put("resultCode","0");
        result.put("resultMsg",response);
        return result;
    }


    /**
     *标签索引查询
     */
    private SearchHits searchLabelByParam(CampaignHitParam param) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param.getISI());
        SearchRequestBuilder builder = client.prepareSearch(Label_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }

    /**
     *事件索引查询
     */
    private SearchHits searchEventByParam(CampaignHitParam param) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        switch (param.getSearchBy()){
            case "ISI":
                boolQueryBuilder = getBoolQueryBuilder(param.getISI());
                break;
            case "EventCode":
                boolQueryBuilder = getBoolQueryBuilderByEventCode(param.getEventCode());
                break;
            case "AssertNumber":
                boolQueryBuilder = getBoolQueryBuilderByAssetNumber(param.getAssetNumber(),param.getStartTime(),param.getEndTime());
                break;
        }
        SearchRequestBuilder builder = client.prepareSearch(EVENT_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }


    private SearchHits getSearchHits(BoolQueryBuilder boolQueryBuilder, SearchRequestBuilder builder,int from) {
        SearchResponse myresponse = builder.setQuery(boolQueryBuilder)
                .setFrom(from-1).setSize(50)
//                .setFetchSource(fields,null)
                .setExplain(true).execute().actionGet();
        return myresponse.getHits();
    }

    private BoolQueryBuilder getBoolQueryBuilder(String ISI) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        rangeQuery("ISI").gt(ISI));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    private BoolQueryBuilder getBoolQueryBuilderByEventCode(String eventCode ) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        rangeQuery("eventCode").gt(eventCode));
        System.out.println(boolQueryBuilder);

        return boolQueryBuilder;
    }


    private BoolQueryBuilder getBoolQueryBuilderByAssetNumber(String assetNumber,Date startTime,Date endTime) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        rangeQuery("assetNumber").gt(assetNumber))
                .must(QueryBuilders.rangeQuery("startTime").gte(startTime))
                .must(QueryBuilders.rangeQuery("endTime").lte(endTime));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

}
