package com.zjtelcom.cpct.elastic.service;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.elastic.model.CampaignHitParam;
import com.zjtelcom.cpct.elastic.model.CampaignHitResponse;
import com.zjtelcom.cpct.elastic.model.CampaignInfoTree;
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

import static com.zjtelcom.cpct.elastic.config.IndexList.*;

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
     * 命中查询条数
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> searchCampaignHitsTotal(CampaignHitParam param) {
        Map<String,Object> result = new HashMap<>();
        List<String> isiList = new ArrayList<>();
        List<Map<String,Object>> eventList = new ArrayList<>();
        SearchHits hits = searchEventByParam(param);
        eventList = hitsToMapList(eventList, hits);
        for (int i = 0;i<eventList.size();i++){
            isiList.add(eventList.get(i).get("ISI").toString());
        }
        result.put("resultCode","0");
        result.put("resultMsg","命中");
        result.put("total",hits.getTotalHits());
        result.put("ISI",isiList);
        return result;
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
        if (eventList.isEmpty()){
            result.put("resultCode","1");
            result.put("resultMsg","查询结果为空");
            return result;
        }
        //除事件外其它使用ISI查询
        param.setSearchBy("ISI");
        param.setIsi(eventList.get(0).get("ISI").toString());

        String ISI = eventList.get(0).get("ISI").toString();
        //查询标签信息
        List<Map<String,Object>> labelList = new ArrayList<>();
        //查询事件后通过事件isi查询标签信息
        param.setIsi(ISI);
        SearchHits labelHits = searchLabelByParam(param);
        labelList = hitsToMapList(labelList, labelHits);
        Map<String,Object> labelInfo = new HashMap<>();
        for (Map<String,Object> label : labelList){
            if (label.get("labelResultList")!=null){
                labelInfo.put("labelResultList",label.get("labelResultList"));
            }
        }

        //查询活动信息
        param.setIsi(ISI);
        SearchHits activityHits = searchActivityByParam(param);
        List<Map<String,Object>>[] activityList = hitsToMapList4More( activityHits);


        List<CampaignInfoTree> activityResult = new ArrayList<>();
        for (List<Map<String,Object>> activity : activityList){
            CampaignInfoTree activityInfo = new CampaignInfoTree();
            Long id = null;
            String name = null;
            boolean booleanResult;
            String reason = null;
            for (Map<String,Object> map : activity){
                id = Long.valueOf(map.get("activityId").toString());
                name = map.get("activityName").toString();
            }
            activityInfo.setId(id);
            activityInfo.setName(name);
            //todo 命中结果；命中实例
            activityInfo.setResult(true);
            activityInfo.setHitEntity("命中得对象");
            activityInfo.setReason("命中原因");

            //查询策略信息
            SearchHits strategyHits = searchStrategyByParam(param,activityInfo.getId().toString());
            List<Map<String,Object>>[] strategyList = hitsToMapList4More(strategyHits);

            List<CampaignInfoTree> stratygyResult = new ArrayList<>();
            for (List<Map<String,Object>> strategy : strategyList){
                CampaignInfoTree strategyInfo = new CampaignInfoTree();
                for (Map<String,Object> map : strategy){
                    id = Long.valueOf(map.get("strategyConfId").toString());
                    name = map.get("strategyConfName").toString();
                }
                strategyInfo.setId(id);
                //todo 策略名称有吗
                strategyInfo.setName(name);
                //todo 命中结果；命中实例
                strategyInfo.setResult(true);
                strategyInfo.setHitEntity("命中得对象");
                strategyInfo.setReason("命中原因");


                //查询规则信息
                SearchHits ruleHits = searchRuleByParam(param,strategyInfo.getId().toString());
                List<Map<String,Object>>[] ruleList = hitsToMapList4More( ruleHits);

                List<CampaignInfoTree> ruleResult = new ArrayList<>();
                for (List<Map<String,Object>> rule : ruleList){
                    CampaignInfoTree ruleInfo = new CampaignInfoTree();
                    for (Map<String,Object> map : rule){
                        id = Long.valueOf(map.get("ruleId").toString());
                        name = map.get("ruleName").toString();
                    }
                    //todo 规则id 暂时没有
                    ruleInfo.setId(id);
                    //todo 规则名称有吗
                    ruleInfo.setName(name);
                    //todo 命中结果；命中实例
                    ruleInfo.setResult(true);
                    ruleInfo.setHitEntity("命中得对象");
                    ruleInfo.setReason("命中原因");

                    //查询标签实例信息
                    //todo 规则id 暂无
                    SearchHits labelInfoHits = searchLabelByRuleId(param,ruleInfo.getId().toString());
                    List<Map<String,Object>>[] labelInfoList = hitsToMapList4More(labelInfoHits);

                    ruleInfo.setLabelList(labelInfoList);
                    ruleResult.add(ruleInfo);

                }
                strategyInfo.setChildren(ruleResult);
                stratygyResult.add(strategyInfo);
            }
            activityInfo.setChildren(stratygyResult);
            activityResult.add(activityInfo);
        }

        Map<String,Object> activity = new HashMap<>();
        activity.put("activityList",activityResult);
        response.setCampaignInfo(activity);
        eventList.get(0).remove("activityList");
        response.setLabelInfo(labelInfo);
        response.setEventInfo(eventList.get(0));
        response.setTotal(Long.parseLong(String.valueOf(eventList.size())));
        result.put("resultCode","0");
        result.put("resultMsg",response);
        return result;
    }

    @Override
    public Map<String, Object> searchLabelInfoByRuleId(String ruleId, String isi) {
        Map<String,Object> result = new HashMap<>();
        CampaignHitParam param = new CampaignHitParam();
        param.setIsi(isi);
        param.setFrom(0);
        List<Map<String,Object>> labelList = new ArrayList<>();
        SearchHits labelHits = searchLabelByRuleId(param,ruleId);
        labelList = hitsToMapList(labelList, labelHits);
        Map<String,Object> labelInfo = new HashMap<>();
        for (Map<String,Object> label : labelList){
            if (label.get("labelResultList")!=null){
                labelInfo.put("labelResultList",label.get("labelResultList"));
            }
        }
        result.put("rusultCode","0");
        result.put("resultMsg",labelInfo);
        return result;
    }

    private List<Map<String, Object>> hitsToMapList(List<Map<String, Object>> mapList, SearchHits hits) {
        for(int j = 0; j < hits.getHits().length; j++) {
            Map<String,Object> map = new HashMap<>();
            String source = hits.getHits()[j].getSourceAsString();
            System.out.println(source);
            Map<String, Object> stringMap = hits.getHits()[j].getSourceAsMap();
            mapList.add(stringMap);
        }

        return mapList;
    }

    private List<Map<String,Object>>[] hitsToMapList4More( SearchHits hits) {
        List<Map<String,Object>>[] result = new List[hits.getHits().length];

        for(int j = 0; j < hits.getHits().length; j++) {
            List<Map<String, Object>> mapList = new ArrayList<>();
            Map<String, Object> map = new HashMap<>();
            String source = hits.getHits()[j].getSourceAsString();
            System.out.println(source);
            Map<String, Object> stringMap = hits.getHits()[j].getSourceAsMap();
            mapList.add(stringMap);
            result[j] = mapList;
        }
        return result;
    }



    /**
     *标签索引查询--Isi
     */
    private SearchHits searchLabelByParam(CampaignHitParam param) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilder(param.getIsi());
        SearchRequestBuilder builder = client.prepareSearch(Label_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }

    /**
     *标签索引查询--RuleId
     */
    private SearchHits searchLabelByRuleId(CampaignHitParam param,String ruleId) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilderByRuleId(param.getIsi(),ruleId);
        SearchRequestBuilder builder = client.prepareSearch(Label_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }

    /**
     *活动索引查询
     */
    private SearchHits searchActivityByParam(CampaignHitParam param) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilderForTest(param.getIsi());
        SearchRequestBuilder builder = client.prepareSearch(ACTIVITY_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }

    /**
     *策略索引查询
     */
    private SearchHits searchStrategyByParam(CampaignHitParam param,String activityId) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilderByActivityId(param.getIsi(),activityId);
        SearchRequestBuilder builder = client.prepareSearch(STRATEGY_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }
    /**
     *规则索引查询
     */
    private SearchHits searchRuleByParam(CampaignHitParam param,String strategyId) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilderByStrategyId(param.getIsi(),strategyId);
        SearchRequestBuilder builder = client.prepareSearch(RULE_MODULE).setTypes(esType);
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
                boolQueryBuilder = getBoolQueryBuilder(param.getIsi());
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
                .setFrom(from).setSize(1)
//                .setFetchSource(fields,null)
                .setExplain(true).execute().actionGet();
        return myresponse.getHits();
    }


    private BoolQueryBuilder getBoolQueryBuilderByActivityId(String isi,String activityId) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        rangeQuery("activityId").gt(activityId))
                .must(QueryBuilders.
                        rangeQuery("ISI").gt(isi));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    private BoolQueryBuilder getBoolQueryBuilderByStrategyId(String isi,String strategyId) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        termQuery("strategyConfId",Long.valueOf(strategyId)))
                .must(QueryBuilders.
                        rangeQuery("ISI").gt(isi));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    private BoolQueryBuilder getBoolQueryBuilderByRuleId(String isi,String ruleId) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        termQuery("ruleId",(Long.valueOf(ruleId))))
                .must(QueryBuilders.
                        rangeQuery("ISI").gt(isi));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    private BoolQueryBuilder getBoolQueryBuilder(String ISI) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        rangeQuery("ISI").gt(ISI));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    //后面删除
    private BoolQueryBuilder getBoolQueryBuilderForTest(String ISI) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        rangeQuery("orderISI").gt(ISI));
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
