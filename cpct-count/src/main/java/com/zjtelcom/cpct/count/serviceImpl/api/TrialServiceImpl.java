package com.zjtelcom.cpct.count.serviceImpl.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.count.base.enums.ResultEnum;
import com.zjtelcom.cpct.count.service.api.TrialService;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.HttpUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.es.es.entity.TrialTarGrp;
import com.zjtelcom.es.es.entity.TrilTarGrpParam;
import com.zjtelcom.es.es.entity.model.LabelResultES;
import com.zjtelcom.es.es.entity.model.TrialResponseES;
import com.zjtelcom.es.es.service.EsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Service
public class TrialServiceImpl implements TrialService {
    private Logger log = LoggerFactory.getLogger(GroupApiServiceImpl.class);

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired(required = false)
    private EsService esService;
    @Autowired
    private RestTemplate restTemplate;


    /**
     * 分群试算
     * @param tarMap
     * @return
     */
    @Override
    public TrialResponseES trialTarGrp(Map<String, Object> tarMap) {
        TrialResponseES responseES = new TrialResponseES();
        int size = (int)tarMap.get("size");
        String groupId = (String)tarMap.get("groupId");
        List<String> groupList = new ArrayList<>();
        TrialTarGrp trialTarGrp = new TrialTarGrp();
        if(groupId.contains(";")){
            String[] split = groupId.split(";");
            for (String s:split){
                groupList.add(s);
            }
            trialTarGrp = getParam(groupList,"2000");
        }else {
            String[] split = groupId.split(",");
            for (String s:split){
                groupList.add(s);
            }
            trialTarGrp = getParam(groupList,"1000");
        }
        trialTarGrp.setSize(size);
        try {
//            String url="http://192.168.137.1:8080/es/searchByTarGrp";
//            responseES = restTemplate.postForObject(url, trialTarGrp, TrialResponseES.class);
            responseES = esService.searchByTarGrp(trialTarGrp);
            log.info("es返回信息："+ JSONObject.parseObject(JSON.toJSONString(responseES)));

        }catch (Exception e){
            e.printStackTrace();
            log.error("分群试算失败：es接口调用错误");
            //es返回200成功
            responseES.setResultCode(ResultEnum.FAILED.getStatus());
            responseES.setResultMsg("分群试算失败：es接口调用错误");
        }
        return responseES;
    }



    //type :1000 分开 2000 合并
    private TrialTarGrp getParam (List<String> groupList,String type){
        TrialTarGrp trialTarGrp = new TrialTarGrp();
        ArrayList<TrilTarGrpParam> params = new ArrayList<>();

        if (type.equals("1000")){
            for (String tarGrpId : groupList){
                TrilTarGrpParam param = new TrilTarGrpParam();
                //redis取规则
                String rule = "";
                List<LabelResult> labelResultList = new ArrayList<>();
                ArrayList<LabelResultES> labelResultES = new ArrayList<>();

                //获取规则
                ExecutorService executorService = Executors.newCachedThreadPool();
                try {
                    Future<Map<String, Object>> future = executorService.submit(new TarGrpRuleTask(Long.valueOf(tarGrpId),null,type, tarGrpConditionMapper, injectionLabelMapper));
                    rule = future.get().get("express").toString();
                    labelResultList = ( List<LabelResult>)future.get().get("labelResultList");
                    // 关闭线程池
                    if (!executorService.isShutdown()) {
                        executorService.shutdown();
                    }
                }catch (Exception e){
                    // 关闭线程池
                    if (!executorService.isShutdown()) {
                        executorService.shutdown();
                    }
                }
                System.out.println("*************************" + rule);
                param.setRule(rule);
                for (LabelResult labelResult : labelResultList){
                    LabelResultES labelEs = BeanUtil.create(labelResult,new LabelResultES());
                    labelResultES.add(labelEs);
                }
                param.setLabelResultList(labelResultES);
                param.setTarGrp(tarGrpId);
                params.add(param);
            }
        }else if (type.equals("2000")){
            TrilTarGrpParam param = new TrilTarGrpParam();
            //redis取规则
            String rule = "";
            List<LabelResult> labelResultList = new ArrayList<>();
            ArrayList<LabelResultES> labelResultES = new ArrayList<>();

            //获取规则
            ExecutorService executorService = Executors.newCachedThreadPool();
            try {
                Future<Map<String, Object>> future = executorService.submit(new TarGrpRuleTask(null,groupList,type, tarGrpConditionMapper, injectionLabelMapper));
                rule = future.get().get("express").toString();
                labelResultList = ( List<LabelResult>)future.get().get("labelResultList");
                // 关闭线程池
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            }catch (Exception e){
                // 关闭线程池
                if (!executorService.isShutdown()) {
                    executorService.shutdown();
                }
            }
            System.out.println("*************************" + rule);
            param.setRule(rule);
            for (LabelResult labelResult : labelResultList){
                LabelResultES labelEs = BeanUtil.create(labelResult,new LabelResultES());
                labelResultES.add(labelEs);
            }
            param.setLabelResultList(labelResultES);
            param.setTarGrp(ChannelUtil.list2String(groupList,";"));
            params.add(param);
        }
        trialTarGrp.setParams(params);
        return trialTarGrp;
    }


    class TarGrpRuleTask implements Callable<Map<String,Object>> {

        private TarGrpConditionMapper tarGrpConditionMapper;

        private InjectionLabelMapper injectionLabelMapper;

        private Long tarGrpId;

        private String type;

        private List<String> targrpIdList;

        public TarGrpRuleTask( Long tarGrpId,List<String> targrpIdList,String type,TarGrpConditionMapper tarGrpConditionMapper, InjectionLabelMapper injectionLabelMapper) {
            this.tarGrpConditionMapper = tarGrpConditionMapper;
            this.injectionLabelMapper = injectionLabelMapper;
            this.tarGrpId = tarGrpId;
            this.type = type;
            this.targrpIdList = targrpIdList;

        }

        @Override
        public Map<String, Object> call() {
            Map<String, Object> result = new HashMap<>();
            //  2.判断活动的客户分群规则---------------------------
            //查询分群规则list
            List<TarGrpCondition> conditionList = new ArrayList<>();
            if (type.equals("2000")){
                for (String tarId : targrpIdList){
                    List<TarGrpCondition> conditions = tarGrpConditionMapper.listTarGrpCondition(Long.valueOf(tarId));
                    conditionList.addAll(conditions);
                }
            }else {
                conditionList  = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
            }
            List<LabelResult> labelResultList = new ArrayList<>();
            List<String> codeList = new ArrayList<>();

            StringBuilder express = new StringBuilder();
                //将规则拼装为表达式
                if (conditionList != null && conditionList.size() > 0) {
                    express.append("if(");
                    //遍历所有规则
                    for (int i = 0; i < conditionList.size(); i++) {
                        LabelResult labelResult = new LabelResult();
                        String type = conditionList.get(i).getOperType();
                        Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(conditionList.get(i).getLeftParam()));

                        labelResult.setLabelCode(label.getInjectionLabelCode());
                        labelResult.setLabelName(label.getInjectionLabelName());
                        labelResult.setRightOperand(label.getLabelType());
                        labelResult.setRightParam(conditionList.get(i).getRightParam());
                        labelResult.setClassName(label.getClassName());
                        labelResult.setOperType(type);
                        labelResultList.add(labelResult);
                        codeList.add(label.getInjectionLabelCode());
                        if ("7100".equals(type)) {
                            express.append("!");
                        }
                        express.append("(");
                        express.append(label.getInjectionLabelCode());
                        if ("1000".equals(type)) {
                            express.append(">");
                        } else if ("2000".equals(type)) {
                            express.append("<");
                        } else if ("3000".equals(type)) {
                            express.append("==");
                        } else if ("4000".equals(type)) {
                            express.append("!=");
                        } else if ("5000".equals(type)) {
                            express.append(">=");
                        } else if ("6000".equals(type)) {
                            express.append("<=");
                        } else if ("7000".equals(type) || "7100".equals(type)) {
                            express.append("in");
                        }else if ("7200".equals(type)) {
                            express.append("@@@@");//区间于
                        } else if ("7100".equals(type)) {
                            express.append("notIn");
                        }
                        express.append(conditionList.get(i).getRightParam());
                        express.append(")");
                        if (i + 1 != conditionList.size()) {
                            express.append("&&");
                        }
                    }
                    express.append(") {return true} else {return false}");
                }else {
                    express.append("");
                }

                // 将表达式存入Redis
                System.out.println(">>express->>>>:" + JSON.toJSONString(express));

                System.out.println("TAR_GRP_ID>>>>>>>>>>" + tarGrpId + ">>>>>>>>codeList->>>>:" + JSON.toJSONString(codeList));

            result.put("express",express.toString());
            result.put("labelResultList",labelResultList);

            return result;
        }

    }


















}
