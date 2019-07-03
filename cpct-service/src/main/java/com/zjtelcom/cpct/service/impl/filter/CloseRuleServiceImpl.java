package com.zjtelcom.cpct.service.impl.filter;

import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cpc.configure.service.api.offer.IFairValueConfigureService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalConditionMapper;
import com.zjtelcom.cpct.dao.channel.OfferMapper;
import com.zjtelcom.cpct.dao.filter.CloseRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.domain.channel.Offer;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.channel.OfferDetail;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.CloseRuleAddVO;
import com.zjtelcom.cpct.dto.filter.CloseRuleVO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.request.filter.CloseRuleReq;
import com.zjtelcom.cpct.service.filter.CloseRuleService;
import com.zjtelcom.cpct.service.synchronize.filter.SynFilterRuleService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct_prd.dao.grouping.TarGrpPrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private TarGrpMapper TarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;

    /**
     * 根据关单规则id集合查询过滤规则集合
     */
    @Override
    public Map<String, Object> getFilterRule(List<Integer> closeRuleIdList) {
        Map<String, Object> map = new HashMap<>();
        List<CloseRule> closeRuleList = new ArrayList<>();
        for (Integer closeRuleId : closeRuleIdList) {
            CloseRule closeRule = closeRuleMapper.selectByPrimaryKey(closeRuleId.longValue());
            closeRuleList.add(closeRule);
        }
        map.put("resultCode", CommonConstant.CODE_SUCCESS);
        map.put("resultMsg", StringUtils.EMPTY);
        map.put("filterRuleList", closeRuleList);
        return map;
    }

    /**
     * 关单规则列表（含分页）
     */
    @Override
    public Map<String, Object> qryFilterRule(CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        Page pageInfo = closeRuleReq.getPageInfo();
        PageHelper.startPage(pageInfo.getPage(), pageInfo.getPageSize());
        List<CloseRule> closeRules = closeRuleMapper.qryFilterRule(closeRuleReq.getCloseRule());
        Page page = new Page(new PageInfo(closeRules));
        List<CloseRuleVO> voList = new ArrayList<>();
        for (CloseRule rule : closeRules){
            CloseRuleVO vo = BeanUtil.create(rule,new CloseRuleVO());
            SysParams sysParams = sysParamsMapper.findParamsByValue("CAM-C-999",rule.getCloseType());
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
    public Map<String, Object> qryFilterRules(CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        List<CloseRule> closeRules = closeRuleMapper.qryFilterRule(closeRuleReq.getCloseRule());
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRules", closeRules);
        return maps;
    }

    /**
     * 删除关单规则
     */
    @Override
    public Map<String, Object> delFilterRule(CloseRule closeRule) {
        Map<String, Object> maps = new HashMap<>();
        CloseRule rule = closeRuleMapper.selectByPrimaryKey(closeRule.getRuleId());
        if(rule != null) {
            closeRuleMapper.delFilterRule(closeRule);
            verbalConditionMapper.deleteByPrimaryKey(rule.getConditionId());
            if (StringUtils.isNotBlank(rule.getLabelCode())){
                TarGrpMapper.deleteByPrimaryKey(Long.valueOf(rule.getLabelCode()).longValue());
                tarGrpConditionMapper.deleteByTarGrpTemplateId(Long.valueOf(rule.getLabelCode()).longValue());
            }

        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synFilterRuleService.deleteSingleFilterRule(closeRule.getRuleId(),"");
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
     *  "closeCode": 0 成功 1 失败
        " productType": 产品类型,
     expression 关单编码自动生成 关单类型 CR001: 判断类型 000001 自然数自增
     */
    @Override
    public Map<String, Object> createFilterRule(CloseRuleAddVO addVO) {
        //受理关单规则 欠费关单规则  拆机关单规则
        Map<String, Object> maps = new HashMap<>();
        final CloseRule closeRule = BeanUtil.create(addVO,new CloseRule());
        closeRule.setCreateDate(DateUtil.getCurrentTime());
        closeRule.setUpdateDate(DateUtil.getCurrentTime());
        closeRule.setStatusDate(DateUtil.getCurrentTime());
        closeRule.setUpdateStaff(UserUtil.loginId());
        closeRule.setCreateStaff(UserUtil.loginId());
        closeRule.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        List<String> codeList = new ArrayList<>();

        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("2000")){
            if (addVO.getChooseProduct()!= null && !addVO.getChooseProduct().isEmpty()){
                for (Long offerId : addVO.getChooseProduct()){
                    Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
                    if (offer==null){
                        continue;
                    }
                    codeList.add(offer.getOfferNbr());
                }
                closeRule.setChooseProduct(ChannelUtil.StringList2String(codeList));
            }
        }
        if (StringUtils.isNotBlank(addVO.getOfferInfo()) && !addVO.getOfferInfo().equals("2000")){
            addVO.setOfferInfo("");
        }
        if (StringUtils.isNotBlank(addVO.getProductType()) && !addVO.getProductType().equals("2000")){
            addVO.setProductType("");
        }
        closeRuleMapper.createFilterRule(closeRule);
        if (StringUtils.isNotBlank(closeRule.getLabelCode())){
            String labelCode = closeRule.getLabelCode();
            TarGrp tarGrp = TarGrpMapper.selectByPrimaryKey(Long.valueOf(labelCode).longValue());
            if (tarGrp!=null){
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(tarGrp));
                //赋值以后转为对象 再去更新
                TarGrpDetail change = JSONObject.parseObject(json.toJSONString(), TarGrpDetail.class);
                change.setTarGrpName(closeRule.getCloseName());
                change.setTarGrpType("4000");
                int i = TarGrpMapper.updateByPrimaryKey(change);
            }
        }
        //关单编码确认类型 自然数自增
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("1000")){
            addVO.setExpression("CR001");
        }
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("3000")){
            addVO.setExpression("CR003");
        }
        if (StringUtils.isNotBlank(addVO.getCloseType()) && addVO.getCloseType().equals("4000")){
            addVO.setExpression("CR004");
        }
        //自动步枪6位数 前面补零
        String expression = CpcUtil.addZeroForNum(String.valueOf(closeRule.getRuleId()), 6);
        closeRuleMapper.updateExpression(closeRule.getRuleId().toString(),addVO.getExpression()+expression);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", closeRule);
        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synFilterRuleService.synchronizeSingleFilterRule(closeRule.getRuleId(),"");
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
    public Map<String, Object> modFilterRule(CloseRuleAddVO editVO) {
        Map<String, Object> maps = new HashMap<>();
        final CloseRule closeRule = closeRuleMapper.selectByPrimaryKey(editVO.getRuleId());
        if (closeRule==null){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", StringUtils.EMPTY);
            return maps;
        }
        BeanUtil.copy(editVO,closeRule);
        closeRule.setUpdateDate(DateUtil.getCurrentTime());
        closeRule.setUpdateStaff(UserUtil.loginId());

        List<String> codeList = new ArrayList<>();
        for (Long offerId : editVO.getChooseProduct()){
            Offer offer = offerMapper.selectByPrimaryKey(Integer.valueOf(offerId.toString()));
            if (offer==null){
                continue;
            }
            codeList.add(offer.getOfferNbr());
        }
        //查看 这个代码 是否修改！！
        closeRule.setChooseProduct(ChannelUtil.StringList2String(codeList));
        closeRuleMapper.updateByPrimaryKey(closeRule);
        if (StringUtils.isNotBlank(closeRule.getLabelCode())){
            String labelCode = closeRule.getLabelCode();
            TarGrp tarGrp = TarGrpMapper.selectByPrimaryKey(Long.valueOf(labelCode).longValue());
            if (tarGrp!=null && !tarGrp.getTarGrpType().equals("4000")){
                JSONObject json = JSONObject.parseObject(JSONObject.toJSONString(tarGrp));
                //赋值以后转为对象 再去更新
                TarGrpDetail change = JSONObject.parseObject(json.toJSONString(), TarGrpDetail.class);
                change.setTarGrpName(closeRule.getCloseName());
                change.setTarGrpType("4000");
                TarGrpMapper.updateByPrimaryKey(change);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("filterRule", closeRule);

        if (SystemParamsUtil.getSyncValue().equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synFilterRuleService.synchronizeSingleFilterRule(closeRule.getRuleId(),"");
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
        CloseRule closeRuleT = closeRuleMapper.selectByPrimaryKey(ruleId);
        if(null == closeRuleT) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", "过滤规则不存在");
        }
        CloseRuleVO vo = BeanUtil.create(closeRuleT,new CloseRuleVO());
        if (closeRuleT.getChooseProduct()!=null && !closeRuleT.getChooseProduct().equals("")){
            List<String> codeList = ChannelUtil.StringToList(closeRuleT.getChooseProduct());
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
        if (closeRuleT.getConditionId()!=null){
            MktVerbalCondition condition = verbalConditionMapper.selectByPrimaryKey(closeRuleT.getConditionId());
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
