package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.campaign.MktCamStrategyConfRelMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.ProductService;
import com.zjtelcom.cpct.elastic.util.EsSearchUtil;
import com.zjtelcom.cpct.util.DateUtil;
import org.I0Itec.zkclient.DataUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService{

    @Autowired(required = false)
    private FilterRuleMapper filterRuleMapper;
    @Autowired(required = false)
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;
    @Autowired(required = false)
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;

    //销售品关联活动查询
    @Override
    public Map<String,Object> selectProductCam(Map<String,Object> paramMap) {

        Map<String,Object> paramMaps = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>();
        List<Map<String,Object>> productList = new ArrayList<>();

        String offerInfo = null;

        String productCode = (String) paramMap.get("productCode");
        String[] split = productCode.split(",");
        for(int i = 0; i < split.length; i++) {
            List<Map<String,Object>> policyList = new ArrayList<>();
            Map<String,Object> map = new HashMap<>();

            FilterRule filterRule = new FilterRule();
            filterRule.setChooseProduct(split[i]);
            List<FilterRule> filterRuleList = filterRuleMapper.selectByProduct(split[i]);
            for(FilterRule filterRule1 : filterRuleList) {
                List<MktStrategyFilterRuleRelDO> mktStrategyFilterRuleRelDOList = mktStrategyFilterRuleRelMapper.selectByRuleId(filterRule1.getRuleId());

                for(MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO : mktStrategyFilterRuleRelDOList) {
                    List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByStrategyConfId(mktStrategyFilterRuleRelDO.getStrategyId());

                    for(MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
                        Map<String,Object> maps = new HashMap<>();
                        maps.put("activityId",mktCamStrategyConfRelDO.getMktCampaignId());
                        maps.put("policyId",mktStrategyFilterRuleRelDO.getStrategyId());
                        if(filterRule1.getOfferInfo()!=null) {
                            if (filterRule1.getOfferInfo().equals("1000")) {
                                offerInfo = "0";
                            } else if (filterRule1.getOfferInfo().equals("2000")) {
                                offerInfo = "1";
                            }
                        }
                        maps.put("closeType",offerInfo);
                        policyList.add(maps);
                    }
                }
            }
            if(policyList.size() > 0) {
                map.put("productCode",split[i]);
                map.put("policyList", policyList);
                productList.add(map);
            }

        }

        resultMap.put("reqId", DateUtil.getDetailTime() + EsSearchUtil.getRandomStr(2));
        if(productList.size() > 0) {
            resultMap.put("resultCode", "1");
            resultMap.put("resultMsg", "有推荐结果");
        }else{
            resultMap.put("resultCode", "1000");
            resultMap.put("resultMsg", "没有匹配推荐结果");
        }

        resultMap.put("productList",productList);

        paramMaps.put("paramMap",resultMap);
        return paramMaps;
    }
}
