package com.zjtelcom.cpct.dubbo.service.dubboThreadPool.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.service.es.EsHitsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class ListMapLabelTaskServiceImpl implements Callable {
    private static final Logger log = LoggerFactory.getLogger(ListMapLabelTaskServiceImpl.class);

    @Autowired
    private EsHitsService esHitService;  //es存储

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    private Object o;
    private Map<String, String> mktAllLabel;
    private Map<String, String> map;
    private DefaultContext<String, Object> context;
    private JSONObject esJson;
    Map<String, String> labelItems;


    // 处理资产级标签和销售品级标签
    private DefaultContext<String, Object> getAssetAndPromLabel(
            Map<String, String> mktAllLabel, Map<String, String> params,
            Map<String, String> privateParams, DefaultContext<String, Object> context,
            JSONObject esJson, Map<String, String> labelItems) {
        //资产级标签
        DefaultContext<String, Object> contextNew = new DefaultContext<String, Object>();
        if (mktAllLabel.get("assetLabels") != null && !"".equals(mktAllLabel.get("assetLabels"))) {
            JSONObject assParam = new JSONObject();
            assParam.put("queryNum", privateParams.get("accNbr"));
            assParam.put("c3", params.get("lanId"));
            assParam.put("queryId", privateParams.get("integrationId"));
            assParam.put("type", "1");
            assParam.put("queryFields", mktAllLabel.get("assetLabels"));
            assParam.put("centerType", "00");
            //因子查询-----------------------------------------------------
            Map<String, Object> dubboResult = yzServ.queryYz(JSON.toJSONString(assParam));
            if ("0".equals(dubboResult.get("result_code").toString())) {
                JSONObject body = new JSONObject((HashMap) dubboResult.get("msgbody"));
                //ES log 标签实例
                //拼接规则引擎上下文
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    //添加到上下文
                    contextNew.put(entry.getKey(), entry.getValue());
                }
            } else {
                log.info("查询资产标签失败");
                esJson.put("hit", "false");
                esJson.put("msg", "查询资产标签失败");
                //esHitService.save(esJson, IndexList.ACTIVITY_MODULE,params.get("reqId") + activityId + params.get("accNbr"));
                esHitService.save(esJson, IndexList.EVENT_MODULE, params.get("reqId"));
                return null;
            }
        }
        contextNew.putAll(labelItems);   //添加事件采集项中作为标签使用的实例
        contextNew.putAll(context);      // 客户级标签
        contextNew.put("integrationId", privateParams.get("integrationId"));
        contextNew.put("accNbr", privateParams.get("accNbr"));
        return contextNew;
    }



    public ListMapLabelTaskServiceImpl(HashMap<String, Object> hashMap) {
        this.o = hashMap.get("o");
        this.mktAllLabel =(Map<String, String>) hashMap.get("mktAllLabel");
        this.map = ( Map<String, String>)hashMap.get("map");
        this.context =(DefaultContext<String, Object>) hashMap.get("context");
        this.esJson = (JSONObject)hashMap.get("esJson");
        this.labelItems =( Map<String, String>) hashMap.get("labelItems");
    }
    @Override
    public Object call() throws Exception {
        DefaultContext<String, Object> resultMap = new DefaultContext<>();
        Map<String, String> privateParams = new ConcurrentHashMap<>();
        privateParams.put("isCust", "0"); //是客户级
        privateParams.put("accNbr", ((Map) o).get("ACC_NBR").toString());
        privateParams.put("integrationId", ((Map) o).get("ASSET_INTEG_ID").toString());
        privateParams.put("custId", map.get("custId"));

        Map<String, Object> assetAndPromLabel = getAssetAndPromLabel(mktAllLabel, map, privateParams, context, esJson, labelItems);
        if (assetAndPromLabel != null) {
            resultMap.putAll(assetAndPromLabel);
        }
        return resultMap;
    }

}
