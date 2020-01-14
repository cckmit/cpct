package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cache.service.api.CacheIndexApi.ICacheProdIndexQryService;
import com.ctzj.smt.bss.cache.service.api.model.CacheResultObject;
import com.telin.dubbo.service.QueryBindByAccCardService;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dubbo.service.SpecialService;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description: 根据事件编码进行特殊业务处理代码
 * @author: linchao
 * @date: 2020/01/03 11:27
 * @version: V1.0
 */
@Service
@Transactional
public class SpecialServiceImpl implements SpecialService {

    private static final Logger log = LoggerFactory.getLogger(SpecialServiceImpl.class);

    @Autowired(required = false)
    private QueryBindByAccCardService queryBindByAccCardService; // 通过号码查询绑定状态

    @Autowired(required = false)
    private ICacheProdIndexQryService iCacheProdIndexQryService;

    @Autowired(required = false)
    private YzServ yzServ; //因子实时查询dubbo服务

    @Autowired
    private RedisUtils redisUtils;  // redis方法

    @Autowired
    private EventRedisService eventRedisService;

    private final static String USED_FLOW = "used_flow";

    private final static String TOTAL_FLOW = "total_flow";


    @Override
    public Map<String, Object> deal(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        String eventCode = (String) params.get("eventCode");
        // 标签
        Map<String, String> labelItems = (Map<String, String>) params.get("labelItems");
        // 入参
        Map<String, String> map = (Map<String, String>) params.get("inParams");
        //解析事件采集项
        JSONObject evtParams = JSONObject.parseObject(map.get("evtContent"));

        //判断是否有流量事件,EVTS000001001,EVTS000001002
        // CPCP_USED_FLOW为已使用流量， CPCP_LEFT_FLOW为剩余流量, CPCP_NEED_FLOW 需要流量
        if ("EVTS000001001".equals(eventCode) || "EVTS000001002".equals(eventCode) && evtParams != null && evtParams.get("CPCP_USED_FLOW") != null && evtParams.get("CPCP_LEFT_FLOW") != null) {
            String cpcpNeedFlow = getCpcpNeedFlow((String) evtParams.get("CPCP_USED_FLOW"), (String) evtParams.get("CPCP_LEFT_FLOW"));
            labelItems.put("CPCP_NEED_FLOW", cpcpNeedFlow);
        }


        /**
         *  满意度调查事件，定义采集项
         *  1、判断联系电话是否绑定微信公众号，是绑定用户则送微厅进行线上测评。
         *  2、判断业务号码是否绑定微信公众号，是绑定用户则送微厅进行线上测评。
         *  3、判断联系电话是否电信号码（接一个查询是否c网资产的判断接口），是发短信。
         *  4、判断产品类型：PHY-MAN-0001 （移动电话）业务号码是否移动电话，是发短信
         *  5、将来：联系电话推IVR
         *
         */
        Map<String, Object> surveyMapRedis = eventRedisService.getRedis("SATISFACTION_SURVEY");
        String eventCodeStr = (String) surveyMapRedis.get("SATISFACTION_SURVEY");
        if (eventCode.indexOf(eventCodeStr) >= 0) {
            // 联系号码-事件采集项
            String contactNumber = (String) evtParams.get("CPCP_CONTACT_NUMBER");
            Map<String, Object> paramsMap = new HashMap<>();
            paramsMap.put("phone", contactNumber);
            paramsMap.put("type", "1");
            labelItems.put("CPCP_PUSH_NUMBER", contactNumber);
            // 判断该联系号码是否绑定微厅
            Map<String, Object> resultMap = queryBindByAccCardService.queryBindByAccCard(paramsMap);
            if (resultMap != null && resultMap.get("data") != null && ((List<Map>) resultMap.get("data")).size() > 0) {
                // 绑定微厅
                List<Map<String, Object>> dataMapList = (List<Map<String, Object>>) resultMap.get("data");
                for (Map dataMap : dataMapList) {
                    if (dataMap.get("tel_status") != null && (Integer) dataMap.get("tel_status") == 0) {
                        labelItems.put("CPCP_PUSH_CHANNEL", "1"); // 1-微厅, 2-短厅, 3-IVR
                        break;
                    }
                }

            } else {
                // 判断资产号码是否绑定微厅
                paramsMap.put("phone", map.get("accNbr"));
                Map<String, Object> accResultMap = queryBindByAccCardService.queryBindByAccCard(paramsMap);
                if (accResultMap != null && accResultMap.get("data") != null && ((List<Map>) accResultMap.get("data")).size() > 0) {
                    // 绑定微厅
                    List<Map<String, Object>> dataMapList = (List<Map<String, Object>>) accResultMap.get("data");
                    for (Map dataMap : dataMapList) {
                        if (dataMap.get("tel_status") != null && (Integer) dataMap.get("tel_status") == 0) {
                            labelItems.put("CPCP_PUSH_CHANNEL", "1"); // 1-微厅, 2-短厅, 3-IVR
                            break;
                        }
                    }
                } else {
                    // 若未绑定微厅，查看联系号码是否为C网用户
                    // 价格判断是否为手机号码
                    log.info("111---contactNumber --->" + contactNumber);
                    boolean isMobile = isMobile(contactNumber);
                    boolean isCUser = false;
                    log.info("222---isMobile --->" + isMobile);
                    if (isMobile) {
                        CacheResultObject<Set<String>> prodInstIdResult = iCacheProdIndexQryService.qryProdInstIndex3(contactNumber, "100000");
                        log.info("333---是否为C网用户-----prodInstIdResult --->" + JSON.toJSONString(prodInstIdResult));
                        if (prodInstIdResult != null && prodInstIdResult.getResultObject() != null && prodInstIdResult.getResultObject().size() > 0) {
                            labelItems.put("CPCP_PUSH_CHANNEL", "2"); // 1-微厅, 2-短厅, 3-IVR
                            isCUser = true;
                        }
                    }
                    if (!isCUser) {
                        boolean isCdma = false;
                        //资产类型
                        Map<String, Object> productTypeRedis = eventRedisService.getRedis("CPCP_PRODUCT_TYPE");
                        String cpcpProductType = (String) productTypeRedis.get("CPCP_PRODUCT_TYPE");
                        log.info("444---cpcpProductType --->" + cpcpProductType);
                        if(cpcpProductType!=null && "PHY-MAN-0001".equals(evtParams.get("CPCP_PRODUCT_TYPE"))){
                            labelItems.put("CPCP_PUSH_CHANNEL", "2"); // 1-微厅, 2-短厅, 3-IVR
                            isCdma = true;
                        }
                        // 若不为C网用户，则“推送渠道”为IVR外呼
                        if (!isCdma) {
                            labelItems.put("CPCP_PUSH_CHANNEL", "3"); // 1-微厅, 2-短厅, 3-IVR
                        }
                    }
                }
            }
        }

        // 计费短信合并功能 CPCP_JIFEI_CONTENT
//        if (evtParams != null) {
//            cpcpJifeiContent(labelItems, evtParams);
//        }
        result.put("labelItems", labelItems);
        log.info("555---labelItems --->" + JSON.toJSONString(labelItems));
        return result;
    }


    // 计算剩余需要的流量
    private String getCpcpNeedFlow(String cpcpUsedFlow, String cpcpLeftFlow) {
        // 获取当天
        int currentDay = DateUtil.getCurrentDay();
        // 当前月的天数
        int lastDay = DateUtil.getLastDayForCurrentMonth();
        // 已用的流量数
        double cpcpUsedFlowDouble = Double.valueOf(cpcpUsedFlow.replace("GB", ""));
        // 剩余的流量数
        double cpcpLeftFlowDouble = Double.valueOf(cpcpLeftFlow.replace("GB", ""));
        // 这个月到currentDay过的日均用量
        double userAvg = cpcpUsedFlowDouble / currentDay;
        // 需要流量
        double needFlow = 0;
        if (userAvg * (lastDay - currentDay) > cpcpLeftFlowDouble) {
            needFlow = (userAvg * (lastDay - currentDay) - cpcpLeftFlowDouble) * 1024;
        }
        DecimalFormat df = new DecimalFormat("#.##");
        return String.valueOf(df.format(needFlow));
    }


    /**
     * 计费短信合并功能 CPCP_JIFEI_CONTENT
     *
     * @param labelItems
     */
/*    private void cpcpJifeiContent(Map<String, String> labelItems, JSONObject evtParams) {
        StringBuilder content = new StringBuilder();
        String usedFlow = "";
        String totalFlow = "";

        usedFlow = (String) redisUtils.get(USED_FLOW);
        if (usedFlow == null || "".equals(usedFlow)) {
            List<SysParams> usedFlowList = sysParamsMapper.listParamsByKeyForCampaign(USED_FLOW);
            if (usedFlowList != null && usedFlowList.size() > 0) {
                SysParams usedFlowParams = usedFlowList.get(0);
                if (usedFlowParams != null) {
                    usedFlow = usedFlowParams.getParamValue();
                    redisUtils.set(USED_FLOW, usedFlow);
                }
            }
        }

        totalFlow = (String) redisUtils.get(TOTAL_FLOW);
        if (totalFlow == null || "".equals(totalFlow)) {
            List<SysParams> totalFlowList = sysParamsMapper.listParamsByKeyForCampaign(TOTAL_FLOW);
            if (totalFlowList != null && totalFlowList.size() > 0) {
                SysParams totalFlowParams = totalFlowList.get(0);
                if (totalFlowParams != null) {
                    totalFlow = totalFlowParams.getParamValue();
                    redisUtils.set(TOTAL_FLOW, totalFlow);
                }
            }
        }

        if (evtParams.get("CPCP_JIFEI_CONTENT") != null) {
            List<Map<String, String>> contentMapList = (List<Map<String, String>>) evtParams.get("CPCP_JIFEI_CONTENT");
            for (int i = 0; i < contentMapList.size(); i++) {
                if (i > 0) {
                    content.append("，");
                }
                content.append(contentMapList.get(i).get("message_name").toString());
                content.append(usedFlow + contentMapList.get(i).get(USED_FLOW) + "，");
                content.append(totalFlow + contentMapList.get(i).get(TOTAL_FLOW));
            }
        }
        labelItems.put("CPCP_JIFEI_MESSAGE", content.toString());
    }*/



    /**
     * 正则判断是否为手机号码
     *
     * @param phone
     * @return
     */
    private static boolean isMobile(String phone) {
        Pattern p = null;
        Matcher m = null;
        boolean isMatch = false;
        //制定验证条件
        String regex1 = "^[1][3,4,5,7,8][0-9]{9}$";
        String regex2 = "^((13[0-9])|(14[579])|(15([0-3,5-9]))|(16[6])|(17[0135678])|(18[0-9]|19[89]))\\d{8}$";

        p = Pattern.compile(regex2);
        m = p.matcher(phone);
        isMatch = m.matches();
        return isMatch;
    }

}