package com.zjtelcom.cpct.service.impl.filter;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.filter.CloseRuleMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.domain.channel.Offer;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.channel.OfferDetail;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleAddVO;
import com.zjtelcom.cpct.dto.filter.FilterRuleVO;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import com.zjtelcom.cpct.service.filter.CloseRuleService;
import com.zjtelcom.cpct.service.synchronize.filter.SynFilterRuleService;
import com.zjtelcom.cpct.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@Service
@Transactional
public class CloseRuleServiceImpl implements CloseRuleService {

    @Autowired
    CloseRuleMapper closeRuleMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;
    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private SynFilterRuleService synFilterRuleService;

    @Value("${sync.value}")
    private String value;

    /**
     * 根据关单规则id集合查询过滤规则集合
     */
    @Override
    public Map<String, Object> getFilterRule(List<Integer> filterRuleIdList) {
        Map<String, Object> map = new HashMap<>();
        List<FilterRule> filterRuleList = new ArrayList<>();
        for (Integer filterRuleId : filterRuleIdList) {
            FilterRule filterRule = closeRuleMapper.selectByPrimaryKey(filterRuleId.longValue());
            filterRuleList.add(filterRule);
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRuleList", filterRuleList);
        return map;
    }

    /**
     * 关单规则列表（含分页）
     */
    @Override
    public Map<String, Object> qryFilterRule(FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        Page pageInfo = filterRuleReq.getPageInfo();
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        List<FilterRule> filterRules = closeRuleMapper.qryFilterRule(filterRuleReq.getFilterRule());
        Page page = new Page(new PageInfo(filterRules));
        List<FilterRuleVO> voList = new ArrayList<>();
        for (FilterRule rule : filterRules){
            FilterRuleVO vo = BeanUtil.create(rule,new FilterRuleVO());
            SysParams sysParams = sysParamsMapper.findParamsByValue("CAM-C-999",rule.getFilterType());
            if (sysParams!=null){
                vo.setFilterTypeName(sysParams.getParamName());
            }
            voList.add(vo);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", voList);
        maps.put("pageInfo",page);
        return maps;
    }

    /**
     * 关单规则列表（不含分页）
     */
    @Override
    public Map<String, Object> qryFilterRules(FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        List<FilterRule> filterRules = closeRuleMapper.qryFilterRule(filterRuleReq.getFilterRule());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", filterRules);
        return maps;
    }

    /**
     * 删除关单规则
     */
    @Override
    public Map<String, Object> delFilterRule(FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        FilterRule rule = closeRuleMapper.selectByPrimaryKey(filterRule.getRuleId());
        if(rule != null) {
            closeRuleMapper.delFilterRule(filterRule);
            verbalConditionMapper.deleteByPrimaryKey(rule.getConditionId());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synFilterRuleService.deleteSingleFilterRule(filterRule.getRuleId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        return maps;
    }

    /**
     * 新建关单规则
     *  "operator": 1000 成功 2000 失败
        " expression": "关单编号",
     */
    @Override
    public Map<String, Object> createFilterRule(FilterRuleAddVO addVO) {
        //受理关单规则 欠费关单规则  拆机关单规则
        Map<String, Object> maps = new HashMap<>();
        final FilterRule filterRule = BeanUtil.create(addVO,new FilterRule());
        filterRule.setCreateDate(DateUtil.getCurrentTime());
        filterRule.setUpdateDate(DateUtil.getCurrentTime());
        filterRule.setStatusDate(DateUtil.getCurrentTime());
        filterRule.setUpdateStaff(UserUtil.loginId());
        filterRule.setCreateStaff(UserUtil.loginId());
        filterRule.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        List<String> codeList = new ArrayList<>();
        if (StringUtils.isNotBlank(addVO.getFilterType()) && addVO.getFilterType().equals("2000")){
            if (addVO.getChooseProduct()!= null && !addVO.getChooseProduct().isEmpty()){
                for (Long offerId : addVO.getChooseProduct()){
                    Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
                    if (offer==null){
                        continue;
                    }
                    codeList.add(offer.getOfferNbr());
                }
                filterRule.setChooseProduct(ChannelUtil.StringList2String(codeList));
            }
        }
        if (StringUtils.isNotBlank(addVO.getOfferInfo()) && !addVO.getOfferInfo().equals("2000")){
            addVO.setOfferInfo("");
        }
        if (StringUtils.isNotBlank(addVO.getExecutionChannel()) && !addVO.getExecutionChannel().equals("2000")){
            addVO.setExecutionChannel("");
        }
        closeRuleMapper.createFilterRule(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", filterRule);

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synFilterRuleService.synchronizeSingleFilterRule(filterRule.getRuleId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return maps;
    }

    /**
     * 修改过滤规则
     */
    @Override
    public Map<String, Object> modFilterRule(FilterRuleAddVO editVO) {
        Map<String, Object> maps = new HashMap<>();
        final FilterRule filterRule = closeRuleMapper.selectByPrimaryKey(editVO.getRuleId());
        if (filterRule==null){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", StringUtils.EMPTY);
            return maps;
        }
        BeanUtil.copy(editVO,filterRule);
        filterRule.setUpdateDate(DateUtil.getCurrentTime());
        filterRule.setUpdateStaff(UserUtil.loginId());

        List<String> codeList = new ArrayList<>();
        for (Long offerId : editVO.getChooseProduct()){
            Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
            if (offer==null){
                continue;
            }
            codeList.add(offer.getOfferNbr());
        }
        filterRule.setChooseProduct(ChannelUtil.StringList2String(codeList));
//        if (filterRule.getFilterType().equals("3000")){
//            filterRule.setLabelCode("PROM_LIST");
//        }
//        if (editVO.getFilterType().equals("6000")){
//            if (filterRule.getConditionId()==null){
//                MktVerbalCondition condition = BeanUtil.create(editVO.getCondition(),new MktVerbalCondition());
//                condition.setVerbalId(0L);
//                condition.setConditionType(ConditionType.FILTER_RULE.getValue().toString());
//                verbalConditionMapper.insert(condition);
//                filterRule.setConditionId(condition.getConditionId());
//            }else {
//                MktVerbalCondition condition = verbalConditionMapper.selectByPrimaryKey(filterRule.getConditionId());
//                if (condition!=null){
//                    BeanUtil.copy(editVO.getCondition(),condition);
//                    verbalConditionMapper.updateByPrimaryKey(condition);
//                }
//            }
//        }
        closeRuleMapper.updateByPrimaryKey(filterRule);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", filterRule);

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synFilterRuleService.synchronizeSingleFilterRule(filterRule.getRuleId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return maps;
    }

    /**
     * 根据过滤规则id集合查询过滤规则集合
     */
    @Override
    public Map<String, Object> getFilterRule(Long ruleId) {
        Map<String, Object> map = new HashMap<>();
        FilterRule filterRuleT = closeRuleMapper.selectByPrimaryKey(ruleId);
        if(null == filterRuleT) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", "过滤规则不存在");
        }
        FilterRuleVO vo = BeanUtil.create(filterRuleT,new FilterRuleVO());
        if (filterRuleT.getChooseProduct()!=null && !filterRuleT.getChooseProduct().equals("")){
            List<String> codeList = ChannelUtil.StringToList(filterRuleT.getChooseProduct());
            List<OfferDetail> productList = new ArrayList<>();
            for (String code : codeList){
                List<Offer> offer = offerMapper.selectByCode(code);
                if (offer!=null && !offer.isEmpty()){
                    OfferDetail offerDetail = BeanUtil.create(offer.get(0),new OfferDetail());
                    productList.add(offerDetail);
                }
            }
            vo.setProductList(productList);
        }
        if (filterRuleT.getConditionId()!=null){
            MktVerbalCondition condition = verbalConditionMapper.selectByPrimaryKey(filterRuleT.getConditionId());
            if (condition!=null){
                Label label = labelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                if (label!=null){
                    vo.setLabelId(label.getInjectionLabelId());
                    vo.setConditionName(label.getInjectionLabelName());
                    vo.setOperType(condition.getOperType());
                    vo.setRightParam(condition.getRightParam());
                }
            }
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRule", vo);
        return map;
    }



}
