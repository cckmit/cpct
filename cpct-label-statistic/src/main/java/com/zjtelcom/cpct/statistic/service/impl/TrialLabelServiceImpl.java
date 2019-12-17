package com.zjtelcom.cpct.statistic.service.impl;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleRelMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.statistic.service.TrialLabelService;
import com.zjtelcom.es.es.entity.model.LabelResultES;
import com.zjtelcom.es.es.service.EsServiceInfo;
import net.sf.json.JSONArray;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
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

    @Autowired
    TrialOperationMapper trialOperationMapper;

    @Autowired
    MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;

    @Autowired
    MktStrategyConfRuleMapper mktStrategyConfRuleMapper;

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
        logger.info("查看result:" + result);
        //数据导入 todo
        if (result) {
            try {
                Map map = esServiceInfo.queryCustomerByList(list);
                logger.info("查询所有标签的值是否有数据:" + (map == null ? 0 : 1));
                if (map != null && map.get("resultCode").toString().equals("200")) {
                    List<Map<String, Object>> resultData = (List<Map<String, Object>>) map.get("resultData");
                    logger.info("resultData数据接收成功,查看第一条数据:" + JSON.toJSON(resultData.get(0)));
                    JSONArray jsonArray = JSONArray.fromObject(resultData);
                    IndexResponse response = client.prepareIndex(index, esType).setSource(jsonArray).get();
                    System.out.println("走到这里是表示索引创建成功?:" + response.status().getStatus());
                }
            } catch (Exception e) {
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


    /**
     * 統計分析 es集群三 對全量數據 再次選擇標籤  查看數據
     *
     * @param param
     * @return
     */
    @Override
    public Map<String, Object> statisticalAnalysts(Map<String, Object> param) {
        HashMap<String, Object> result = new HashMap<>();
        HashMap<String, Object> data = new HashMap<>();
        List<Map<String, String>> list1 = (List<Map<String, String>>) param.get("list");
        Map<String, Object> stringObjectMap = commonTarGrpTemplateCount(list1, result);
        Object list = stringObjectMap.get("expressions");
        List<String> expressions = list == null ? new ArrayList<>() : (ArrayList) list;
        Object labelList = stringObjectMap.get("labelList");
        List<LabelResultES> labelDataList = labelList == null ? new ArrayList<>() : (ArrayList) labelList;
        // 二次搜索条件 查询拼接
        List analustList = (List<Object>) param.get("analustList");
        Map<String, Object> stringObjectMap2 = commonTarGrpTemplateCount(analustList, result);
        Object list2 = stringObjectMap2.get("expressions");
        List<String> expressions2 = list == null ? new ArrayList<>() : (ArrayList) list2;
        Object labelList2 = stringObjectMap2.get("labelList");
        List<LabelResultES> labelDataList2 = labelList2 == null ? new ArrayList<>() : (ArrayList) labelList2;

        String id = param.get("id").toString();
        TrialOperation trialOperation = trialOperationMapper.selectByPrimaryKey(Long.valueOf(id));
        Long batchNum = trialOperation.getBatchNum();//批次
        Long strategyId = trialOperation.getStrategyId();//规则
        List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOS = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(strategyId);
        //每一个规则查询
        for (int i = 0; i < mktStrategyConfRuleRelDOS.size(); i++) {
            ArrayList<Map<String, Object>> arrayList = new ArrayList<>();
            String indexs = batchNum + mktStrategyConfRuleRelDOS.get(i).getMktStrategyConfRuleId().toString();
            logger.info("indexs=>:" + indexs);
            SearchResponse myresponse = null;
            Long totalHits = null;
            for (String expression2 : expressions2) {
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                HashMap<String, Object> map = new HashMap<>();
                buildQuery(boolQuery, expression2, labelDataList2, null, null);

                for (String expression : expressions) {
                    buildQuery(boolQuery, expression, labelDataList, null, null);
                }
                myresponse = client.prepareSearch(indexs)
                        .setTypes(esType)
                        .setQuery(boolQuery)
                        .setFetchSource("ASSET_NUMBER", null)
                        .setExplain(false)
                        .execute().actionGet();

                SearchHits hits = myresponse.getHits();
                totalHits = hits.totalHits;
                logger.info("totalHits=>" + totalHits.toString());

                map.put("total", totalHits.toString());
                map.put("expression2", expression2);
                arrayList.add(map);
            }
            // 遍历ES查询
            MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(mktStrategyConfRuleRelDOS.get(i).getMktStrategyConfRuleRelId());
            data.put("name", mktStrategyConfRuleDO.getMktStrategyConfRuleName());
            data.put("rule",arrayList);
        }
        result.put("code",200);
        result.put("data",data);
        return result;
    }

    public Map<String, Object> commonTarGrpTemplateCount(List<Map<String, String>> tarGrplist, Map<String, Object> params) {
        List<String> expressions = new ArrayList<>();
        List<LabelResultES> labelList = new ArrayList<>();
        for (Map<String, String> tarGrpCondition : tarGrplist) {
            String code = tarGrpCondition.get("code");
            String operType = tarGrpCondition.get("operType");
            operType = equationSymbolConversion(operType);
            String rightParam = tarGrpCondition.get("rightParam");
            String expression = code + operType + rightParam;
            expressions.add(expression);
            LabelResultES label = new LabelResultES();
            label.setLabelCode(code);
            label.setLabelDataType(tarGrpCondition.get("labelType") == null ? "" : tarGrpCondition.get("labelType"));
            labelList.add(label);
        }
        params.put("expressions", expressions);
        params.put("labelList", labelList);
        return params;
    }

    public String equationSymbolConversion(String type) {
        switch (type) {
            case "1000":
                return ">";
            case "2000":
                return "<";
            case "3000":
                return "==";
            case "4000":
                return "!=";
            case "5000":
                return ">=";
            case "6000":
                return "<=";
            case "7000":
                return "in";
            case "7100":
                return "notIn";
            case "7200":
                return "@@@@";
            default:
                return "";
        }
    }

    public BoolQueryBuilder buildQuery(BoolQueryBuilder boolQueryBuilder, String ruleTotest, List<LabelResultES> labelResultList, List<LabelResultES> crmList, String labelType) {
        String[] result = null;
        if (ruleTotest.contains(">=")) {
            result = ruleTotest.split(">=");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(result[0].trim()).gte(result[1].trim()));
            }
        } else if (ruleTotest.contains("<=")) {
            result = ruleTotest.split("<=");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(result[0].trim()).lte(result[1].trim()));
            }
        } else if (ruleTotest.contains("!=")) {
            result = ruleTotest.split("!=");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                if (isTextLabel(labelResultList, result)) {
                    boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery(result[0] + ".keyword", result[1].trim()));
                } else {
                    boolQueryBuilder.mustNot(QueryBuilders.matchPhraseQuery(result[0], result[1].trim()));
                }
            }
        } else if (ruleTotest.contains("<")) {
            result = ruleTotest.split("<");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(result[0]).lt(result[1].trim()));
            }
        } else if (ruleTotest.contains(">")) {
            result = ruleTotest.split(">");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                boolQueryBuilder.must(QueryBuilders.rangeQuery(result[0]).gt(result[1].trim()));
            }
        } else if (ruleTotest.contains("==")) {
            result = ruleTotest.split("==");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                if (isTextLabel(labelResultList, result) && !result[0].equals("CPCP_BIRTH_DAY")) {
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(result[0] + ".keyword", result[1].trim()));
                } else if (result[0].equals("CPCP_BIRTH_DAY")) {
                    String replace = "";
                    if (result[1] != null && (result[1].contains("{ForMonth}") || result[1].contains("{ForDay}"))) {
                        if (result[1] != null && result[1].contains("{ForMonth}")) {
                            replace = result[1].replace("{ForMonth}", getMonth(new Date()));
                        }
                        if (result[1] != null && result[1].contains("{ForDay}")) {
                            if (replace.equals("")) {
                                replace = result[1].replace("{ForDay}", getDay(new Date()));
                            } else {
                                replace = replace.replace("{ForDay}", getDay(new Date()));
                            }
                        }
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(result[0] + ".keyword", replace.trim()));
                    } else {
                        boolQueryBuilder.must(QueryBuilders.wildcardQuery(result[0] + ".keyword", result[1].trim()));
                    }
                } else {
                    boolQueryBuilder.must(QueryBuilders.matchPhraseQuery(result[0], result[1].trim()));
                }
            }
        } else if (ruleTotest.contains("in")) {
            result = ruleTotest.split("in");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                String[] resList = null;
                resList = result[1].split(",");
                if (isTextLabel(labelResultList, result)) {
                    boolQueryBuilder.must(QueryBuilders.termsQuery(result[0] + ".keyword", resList));
                } else {
                    boolQueryBuilder.must(QueryBuilders.termsQuery(result[0], resList));
                }
            }
        } else if (ruleTotest.contains("@@@@")) {//区间于
            result = ruleTotest.split("@@@@");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                String[] value = result[1].split(",");
                boolQueryBuilder.must(QueryBuilders.rangeQuery(result[0]).gte(value[0].trim()))
                        .must(QueryBuilders.rangeQuery(result[0]).lte(value[1].trim()));
            }
        } else if (ruleTotest.contains("notIn")) {
            result = ruleTotest.split("notIn");
            if (isBigDataLabel(labelResultList, result, crmList) && labelTypeViladate(labelResultList, result, labelType)) {
                String[] resList = null;
                resList = result[1].split(",");
                if (isTextLabel(labelResultList, result)) {
                    boolQueryBuilder.mustNot(QueryBuilders.termsQuery(result[0] + ".keyword", resList));
                } else {
                    boolQueryBuilder.mustNot(QueryBuilders.termsQuery(result[0], resList));
                }
            }
        }
//        System.out.println(boolQueryBuilder);
        return boolQueryBuilder;
    }

    private boolean isBigDataLabel(List<LabelResultES> list, String[] result, List<LabelResultES> crmList) {
        if (list == null) {
            return true;
        }
        for (LabelResultES label : list) {
            if (result[0].equals(label.getLabelCode())) {
                //判断标签是否是大数据标签
                if (label.getClassName() != null) {
                    //添加crm标签
                    crmList.add(label);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean labelTypeViladate(List<LabelResultES> list, String[] result, String labelType) {
        return true;
    }


    private boolean isTextLabel(List<LabelResultES> list, String[] result) {
        if (list == null) {
            return true;
        }
        for (LabelResultES label : list) {
            if (result[0].equals(label.getLabelCode())) {
                //判断标签是否是字符类型
                if (label.getLabelDataType().equals("1200")) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    public static String getMonth(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM");
        String result = sdf.format(date);
        return result;
    }

    public static String getDay(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        String result = sdf.format(date);
        return result;
    }
}
