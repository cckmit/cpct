package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.filter.CloseRuleMapper;
import com.zjtelcom.cpct.dao.filter.MktStrategyCloseRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyCloseRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.ProductService;
import com.zjtelcom.cpct.elastic.util.EsSearchUtil;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.event.EventRedisService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.logging.Logger;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired(required = false)
    private CloseRuleMapper closeRuleMapper;
    @Autowired(required = false)
    private MktStrategyCloseRuleRelMapper mktStrategyCloseRuleRelMapper;
    @Autowired(required = false)
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private EventRedisService eventRedisService;
    @Autowired
    private RedisUtils redisUtils;

    //销售品关联活动查询
    @Override
    public Map<String, Object> selectProductCam(List<Map<String, Object>> paramList) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map<String, Object> paramMap : paramList) {
            List<Map<String, Object>> productList = new ArrayList<>();
            String offerInfo = null;
            String productCode = (String) paramMap.get("productCode");
            String type = (String) paramMap.get("type");
            String[] split = productCode.split(",");
            for (int i = 0; i < split.length; i++) {
                List<Map<String, Object>> activityList = new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                if ("0".equals(type)) {
                    type = "1000";
                } else if ("1".equals(type)) {
                    type = "2000";
                } else if ("2".equals(type)) {
                    type = "3000";
                }
                Object list = redisUtils.hget("CLOSE_RULE_LIST_" + split[i],type);
                List<CloseRule> filterRuleList= new ArrayList<>();
                if (list!=null){
                    filterRuleList = (List<CloseRule>) list;
                }else {
                    filterRuleList = closeRuleMapper.selectByProduct(split[i], type, StatusCode.RECEIVE_CLOSE.getStatusCode());
                    redisUtils.hset("CLOSE_RULE_LIST_"+ split[i],type,filterRuleList);
                }
                for (CloseRule filterRule1 : filterRuleList) {
                    Object ruleList = redisUtils.get("CAM_CLOSE_RULE_LIST_" + filterRule1.getRuleId());
                    List<MktStrategyCloseRuleRelDO> mktStrategyCloseRuleRelDOList = new ArrayList<>();
                    if (ruleList!=null){
                        mktStrategyCloseRuleRelDOList = (List<MktStrategyCloseRuleRelDO>) ruleList;
                    }else {
                         mktStrategyCloseRuleRelDOList = mktStrategyCloseRuleRelMapper.selectByRuleId(filterRule1.getRuleId());
                        redisUtils.setRedisUnit("CAM_CLOSE_RULE_LIST_"+filterRule1.getRuleId(),mktStrategyCloseRuleRelDOList,300);
                    }
                    for (MktStrategyCloseRuleRelDO mktStrategyCloseRuleRelDO : mktStrategyCloseRuleRelDOList) {
                        Map<String, Object> maps = new HashMap<>();
                        if (filterRule1.getOfferInfo() != null) {
                            if (filterRule1.getOfferInfo().equals("1000")) {
                                offerInfo = "0";
                            } else if (filterRule1.getOfferInfo().equals("2000")) {
                                offerInfo = "1";
                            } else if (filterRule1.getOfferInfo().equals("3000")) {
                                offerInfo = "2";
                            }
                        }
                        Map<String, Object> mktCampaignRedis = eventRedisService.getRedis("MKT_CAMPAIGN_", mktStrategyCloseRuleRelDO.getStrategyId());
                        MktCampaignDO mktCampaignDO = new MktCampaignDO();
                        if (mktCampaignRedis != null) {
                                mktCampaignDO = (MktCampaignDO) mktCampaignRedis.get("MKT_CAMPAIGN_" + mktStrategyCloseRuleRelDO.getStrategyId());
                        }
                        if (mktCampaignDO==null || (!"2002".equals(mktCampaignDO.getStatusCd())
                                && !"2008".equals(mktCampaignDO.getStatusCd()))){
                            mktStrategyCloseRuleRelMapper.deleteByPrimaryKey(mktStrategyCloseRuleRelDO.getMktStrategyFilterRuleRelId());
                            continue;
                        }
                        if ("2001".equals(mktCampaignDO.getStatusCd())){
                            continue;
                        }
                        Object o = redisUtils.get("CLOSE_RULE_" + filterRule1.getRuleId());
                        CloseRule closeRule = new CloseRule();
                        if (o!=null){
                            closeRule = (CloseRule) o;
                        }else {
                            closeRule  = closeRuleMapper.selectByPrimaryKey(filterRule1.getRuleId());
                            redisUtils.setRedisUnit("CLOSE_RULE_"+closeRule.getRuleId(),closeRule,300);
                        }
                        maps.put("closeType", offerInfo);
                        maps.put("activityId", mktCampaignDO.getInitId());
                        maps.put("closeName",filterRule1.getCloseName());
                        maps.put("closeCode",filterRule1.getCloseCode());
                        maps.put("closeNumber",closeRule.getExpression());
                        activityList.add(maps);
                    }
                }
                if (activityList.size() > 0) {
                    map.put("productCode", split[i]);
                    map.put("activityList", activityList);
                    productList.add(map);
                }

            }
            resultMap.put("reqId", DateUtil.getDetailTime() + EsSearchUtil.getRandomStr(2));
            if (productList.size() > 0) {
                resultMap.put("resultCode", "1");
                resultMap.put("resultMsg", "有推荐结果");
            } else {
                resultMap.put("resultCode", "1000");
                resultMap.put("resultMsg", "没有匹配推荐结果");
            }
            resultMap.put("productList", productList);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> getCloseCampaign(Map<String, Object> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        String filterType = (String) paramMap.get("filterType");
        System.out.println("关单接口入参 ： ！！ "+filterType);
        List<Map<String,Object>> strings = new ArrayList<>();
        if (StringUtils.isNotBlank(filterType)){
            String[] split = filterType.split(",");
            for (String s : split) {
                List<Map<String,Object>> list = null;
                HashMap<String, Object> map = new HashMap<>();
                map.put("filterType",s);
                map.put("date",DateUtil.getDateFormatStr(new Date()));
                map.put("status","2002");
                if (StringUtils.isNotBlank(filterType)) {
                    list = closeRuleMapper.getCloseCampaign(map);
                }
               if (!list.isEmpty() && list != null) {
                   for (Map<String, Object> stringObjectMap : list) {
                       strings.add(stringObjectMap);
                   }
               }
            }
        }
        System.out.println("返回结果： "+strings.toArray());
        resultMap.put("resultCode","200");
        resultMap.put("resultData",strings);
        return resultMap;
    }


}
