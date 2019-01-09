package com.zjtelcom.cpct.elastic.service;

import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.elastic.model.CampaignHitParam;
import com.zjtelcom.cpct.elastic.model.CampaignHitResponse;
import com.zjtelcom.cpct.elastic.model.CampaignInfoTree;
import com.zjtelcom.cpct.elastic.model.TotalModel;
import com.zjtelcom.cpct.elastic.util.DateUtil;
import com.zjtelcom.cpct.elastic.util.ElasticsearchUtil;
import com.zjtelcom.cpct.elastic.util.EsSearchUtil;
import com.zjtelcom.cpct.enums.Operator;
import org.apache.logging.log4j.util.StringMap;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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
        jsonObject.put("date",DateUtil.formatDate(new Date()));
        jsonObject.put("dateSt",new Date());
        jsonObject.put("dateSt2","2018-04-25T08:33:44.840Z");

        String id = ElasticsearchUtil.addData(jsonObject, "params_module", esType, jsonObject.getString("id"));
        System.out.println("*********ID**********: "+id);
    }

    @Override
    public void save(JSONObject jsonObject,String indexName) {
        try {
            ElasticsearchUtil.addData(jsonObject, indexName, esType);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(JSONObject jsonObject,String indexName,String _id) {
        try {
            ElasticsearchUtil.addData(jsonObject, indexName, esType, _id);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        result.put("resultCode","200");
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
        Long total = searchEventCountByParam(param);
        TotalModel[] totalModels = new TotalModel[total.intValue()];
        for (int i = 1;i<=total.intValue();i++){
            TotalModel totalModel = new TotalModel();
            totalModel.setNumber(String.valueOf(i));
            totalModels[i-1] = totalModel;
        }
        for(int j = 0; j < hits.getHits().length; j++) {
            String source = hits.getHits()[j].getSourceAsString();
            System.out.println(source);
            Map<String, Object> stringMap = hits.getHits()[j].getSourceAsMap();
            //todo 触发时间
            stringMap.put("triggerTime",stringMap.get("evtCollectTime")==null ? "" : stringMap.get("evtCollectTime"));
            stringMap.put("timeCost",stringMap.get("timeCost")+"毫秒");
            System.currentTimeMillis();
            eventList.add(stringMap);
        }
        if (eventList.isEmpty()){
            result.put("resultCode","500");
            result.put("resultMsg","查询结果为空");
            return result;
        }
        //除事件外其它使用ISI查询
        param.setSearchBy("ISI");
        param.setIsi(eventList.get(0).get("reqId").toString());

        String ISI = eventList.get(0).get("reqId").toString();
        //查询标签信息
        List<Map<String,Object>> labelList = new ArrayList<>();
        //查询事件后通过事件isi查询标签信息
        param.setIsi(ISI);
        SearchHits labelHits = searchLabelByParam(param);
        labelList = hitsToMapList(labelList, labelHits);
        Map<String,Object> labelInfo = new HashMap<>();
        for (Map<String,Object> label : labelList){
            if (label.get("labelResultList")!=null){
                List<Map<String,Object>> labelResults = (List<Map<String, Object>>) label.get("labelResultList");
                for (Map<String,Object> map : labelResults){
                    map.put("operType",Operator.getOperator(Integer.valueOf(map.get("operType").toString())).getDescription());
                }
                labelInfo.put("labelResultList",label.get("labelResultList"));
            }
        }

        //查询活动信息
        param.setIsi(ISI);
        SearchHits activityHits = searchActivityByParam(param);
        List<Map<String,Object>> activityList = hitsToMapList4More(activityHits);


        List<CampaignInfoTree> activityResult = new ArrayList<>();
        for (Map<String,Object> activity : activityList){
            CampaignInfoTree activityInfo = new CampaignInfoTree();
            Long id = null;
            String name = null;
            String booleanResult = null;
            String hitEntity = null;

            id = Long.valueOf(activity.get("activityId").toString());
            name = activity.get("activityName").toString();
            booleanResult = activity.get("hit")==null ? "false" : activity.get("hit").toString();
            hitEntity = activity.get("hitEntity")==null ? "未知对象" : activity.get("hitEntity").toString();
            if (booleanResult.equals("false")){
                booleanResult = booleanResult+(activity.get("msg")==null ? "(未知原因)" : "("+activity.get("msg")+")");
            }

            activityInfo.setId(id);
            activityInfo.setName(name);
            //todo 命中结果；命中实例
            activityInfo.setResult(booleanResult);
            activityInfo.setHitEntity(hitEntity);
            activityInfo.setType("activity");

            //查询策略信息
            SearchHits strategyHits = searchStrategyByParam(param,activityInfo.getId().toString(),hitEntity);
            List<Map<String,Object>> strategyList = hitsToMapList4More(strategyHits);

            List<CampaignInfoTree> stratygyResult = new ArrayList<>();
            for (Map<String,Object> strategy : strategyList){
                CampaignInfoTree strategyInfo = new CampaignInfoTree();

                id = Long.valueOf(strategy.get("strategyConfId").toString());
                name = strategy.get("strategyConfName")==null ? "" : strategy.get("strategyConfName").toString();
                booleanResult = strategy.get("hit")==null ? "false" : strategy.get("hit").toString();
                hitEntity = strategy.get("hitEntity")==null ? "未知对象" : strategy.get("hitEntity").toString();
                if (booleanResult.equals("false")){
                    booleanResult = booleanResult+(strategy.get("msg")==null ? "(未知原因)" : "("+strategy.get("msg")+")");
                }
                strategyInfo.setId(id);
                strategyInfo.setName(name);
                strategyInfo.setResult(booleanResult);
                strategyInfo.setHitEntity(hitEntity);
                strategyInfo.setType("strategy");


                //查询规则信息
                SearchHits ruleHits = searchRuleByParam(param,strategyInfo.getId().toString(),hitEntity);
                List<Map<String,Object>> ruleList = hitsToMapList4More( ruleHits);

                List<CampaignInfoTree> ruleResult = new ArrayList<>();
                for (Map<String,Object> rule : ruleList){
                    CampaignInfoTree ruleInfo = new CampaignInfoTree();

                    id = Long.valueOf(rule.get("ruleId").toString());
                    name = rule.get("ruleName")==null ? "" : rule.get("ruleName").toString();
                    booleanResult = rule.get("hit")==null ? "false" : rule.get("hit").toString();
                    hitEntity = rule.get("hitEntity")==null ? "未知对象" : rule.get("hitEntity").toString();
                    if (booleanResult.equals("false")){
                        booleanResult = booleanResult+(rule.get("msg")==null ? "(未知原因)" : "("+rule.get("msg")+")");
                    }
                    ruleInfo.setId(id);
                    ruleInfo.setName(name);
                    ruleInfo.setResult(booleanResult);
                    ruleInfo.setHitEntity(hitEntity);
                    ruleInfo.setType("rule");

                    //查询标签实例信息
                    SearchHits labelInfoHits = searchLabelByRuleId(param,ruleInfo.getId().toString(),hitEntity);
                    List<Map<String,Object>> labelInfoList = hitsToMapList4More(labelInfoHits);

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
        response.setTotal(totalModels);
        result.put("resultCode","200");
        result.put("resultMsg",response);
        return result;
    }

    /**
     *规则弹窗---活动命中查询
     * @param ruleId
     * @param isi
     * @return
     */
    @Override
    public Map<String, Object> searchLabelInfoByRuleId(String ruleId, String isi) {
        Map<String,Object> result = new HashMap<>();
        CampaignHitParam param = new CampaignHitParam();
        param.setIsi(isi);
        param.setFrom(0);
        List<Map<String,Object>> labelList = new ArrayList<>();
        SearchHits labelHits = searchLabelByRuleId(param,ruleId,"");
        labelList = hitsToMapList(labelList, labelHits);
        Map<String,Object> labelInfo = new HashMap<>();
        for (Map<String,Object> label : labelList){
            if (label.get("labelResultList")!=null){
                List<Map<String,Object>> labelResults = (List<Map<String, Object>>) label.get("labelResultList");
                for (Map<String,Object> map : labelResults){
                    map.put("operType",Operator.getOperator(Integer.valueOf(map.get("operType").toString())).getDescription());
                }
                labelInfo.put("labelResultList",labelResults);
            }
        }
        result.put("resultCode","200");
        result.put("resultMsg",labelInfo);
        return result;
    }

    //命中数据转换成MapList
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

    //命中数据转换成list 数组 （可优化）
    private List<Map<String,Object>> hitsToMapList4More(SearchHits hits) {
        List<Map<String,Object>> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();

        for(int j = 0; j < hits.getHits().length; j++) {
            String source = hits.getHits()[j].getSourceAsString();
            System.out.println(source);
            Map<String, Object> stringMap = hits.getHits()[j].getSourceAsMap();
            result.add(stringMap);
        }
        return result;
    }


    /**
     * ES查询方法 获取命中对象
     * @return
     */
    private SearchHits getSearchHits(BoolQueryBuilder boolQueryBuilder, SearchRequestBuilder builder,int from) {
        SearchResponse myresponse = builder.setQuery(boolQueryBuilder).setSize(1000)
                .setExplain(true).execute().actionGet();
        return myresponse.getHits();
    }

    /**
     * ES查询方法 获取命中对象（事件）
     * @return
     */
    private SearchHits getSearchHits4Event(BoolQueryBuilder boolQueryBuilder, SearchRequestBuilder builder,int from) {
        SortBuilder sortBuilder = SortBuilders.fieldSort("evtCollectTime")
                .order(SortOrder.DESC).unmappedType("date");
        SearchResponse myresponse = builder.setQuery(boolQueryBuilder)
                .setFrom(from).setSize(1).addSort(sortBuilder)
                .setExplain(true).execute().actionGet();
        return myresponse.getHits();
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
    private SearchHits searchLabelByRuleId(CampaignHitParam param,String ruleId,String hitEntity) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilderByRuleId(param.getIsi(),ruleId,hitEntity);
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
    private SearchHits searchStrategyByParam(CampaignHitParam param,String activityId,String hitEntity) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilderByActivityId(param.getIsi(),activityId,hitEntity);
        SearchRequestBuilder builder = client.prepareSearch(STRATEGY_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }
    /**
     *规则索引查询
     */
    private SearchHits searchRuleByParam(CampaignHitParam param,String strategyId,String hitEntity) {
        BoolQueryBuilder boolQueryBuilder = getBoolQueryBuilderByStrategyId(param.getIsi(),strategyId,hitEntity);
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
            case "EventCode"://资产集成编码
                boolQueryBuilder = getBoolQueryBuilderByEventCode(param.getEventCode(),param.getStartTime(),param.getEndTime());
                break;
            case "AssertNumber"://资产号码
                boolQueryBuilder = getBoolQueryBuilderByAssetNumber(param.getAssetNumber(),param.getStartTime(),param.getEndTime());
                break;
        }
        SearchRequestBuilder builder = client.prepareSearch(EVENT_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits4Event(boolQueryBuilder, builder,param.getFrom());
        return hits;
    }

    /**
     *事件索引查询
     */
    private Long searchEventCountByParam(CampaignHitParam param) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        switch (param.getSearchBy()){
            case "ISI":
                boolQueryBuilder = getBoolQueryBuilder(param.getIsi());
                break;
            case "EventCode":
                boolQueryBuilder = getBoolQueryBuilderByEventCode(param.getEventCode(),param.getStartTime(),param.getEndTime());
                break;
            case "AssertNumber":
                boolQueryBuilder = getBoolQueryBuilderByAssetNumber(param.getAssetNumber(),param.getStartTime(),param.getEndTime());
                break;
        }
        SearchRequestBuilder builder = client.prepareSearch(EVENT_MODULE).setTypes(esType);
        SearchHits hits = getSearchHits(boolQueryBuilder, builder,param.getFrom());
        return hits.getTotalHits();
    }


    //活动id isi 组装查询条件
    private BoolQueryBuilder getBoolQueryBuilderByActivityId(String isi,String activityId,String hitEntity) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        matchQuery("activityId",activityId))
                .must(QueryBuilders.
                        matchQuery("reqId",isi));
        if (!"".equals(hitEntity) && !"未知对象".equals(hitEntity)){
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("hitEntity.keyword",hitEntity));
        }
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    //策略id isi 组装查询条件
    private BoolQueryBuilder getBoolQueryBuilderByStrategyId(String isi,String strategyId,String hitEntity) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        termQuery("strategyConfId",Long.valueOf(strategyId)))
                .must(QueryBuilders.
                        matchQuery("reqId",isi));
        if (!"".equals(hitEntity) && !"未知对象".equals(hitEntity)){
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("hitEntity.keyword",hitEntity));
        }
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    //规则id isi 组装查询条件
    private BoolQueryBuilder getBoolQueryBuilderByRuleId(String isi,String ruleId,String hitEntity) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        termQuery("ruleId",(Long.valueOf(ruleId))))
                .must(QueryBuilders.
                        matchQuery("reqId",isi));
        if (!"".equals(hitEntity) && !"未知对象".equals(hitEntity)){
            boolQueryBuilder.must(QueryBuilders.matchPhraseQuery("hitEntity.keyword",hitEntity));
        }
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    //isi 组装查询条件
    private BoolQueryBuilder getBoolQueryBuilder(String ISI) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        matchQuery("reqId",ISI));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    //后面删除
    private BoolQueryBuilder getBoolQueryBuilderForTest(String ISI) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        matchQuery("reqId",ISI));
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    //资产集成编码组装查询条件
    private BoolQueryBuilder getBoolQueryBuilderByEventCode(String eventCode,Date startTime,Date endTime ) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        matchQuery("integrationId",eventCode));
        if (startTime!=null && endTime!=null){
            String start = DateUtil.formatDate(startTime);
            String end = DateUtil.formatDate(endTime);
            boolQueryBuilder .must(QueryBuilders.rangeQuery("evtCollectTime").from(start).to(end));
        }
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    //资产号码和时间段组装查询条件
    private BoolQueryBuilder getBoolQueryBuilderByAssetNumber(String assetNumber,Date startTime,Date endTime) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.
                        matchQuery("accNbr",assetNumber));
        if (startTime!=null && endTime!=null){
            String start = DateUtil.formatDate(startTime);
            String end = DateUtil.formatDate(endTime);
            boolQueryBuilder .must(QueryBuilders.rangeQuery("evtCollectTime").from(start).to(end));
        }
        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

}
