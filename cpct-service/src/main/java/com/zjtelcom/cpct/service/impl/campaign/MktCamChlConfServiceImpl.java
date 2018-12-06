/**
 * @(#)MktCamChlConfAttrServiceImpl.java, 2018/7/2.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.service.impl.campaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlConfMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamChlResultConfRelMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.question.MktQuestionnaireMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.Rule;
import com.zjtelcom.cpct.domain.RuleDetail;
import com.zjtelcom.cpct.domain.User;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultConfRelDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.question.Questionnaire;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfAttr;
import com.zjtelcom.cpct.dto.campaign.MktCamChlConfDetail;
import com.zjtelcom.cpct.dto.channel.LabelValueVO;
import com.zjtelcom.cpct.dto.channel.OperatorDetail;
import com.zjtelcom.cpct.dto.channel.VerbalVO;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRule;
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.campaign.MktCamChlConfService;
import com.zjtelcom.cpct.service.channel.CamScriptService;
import com.zjtelcom.cpct.service.channel.VerbalService;
import com.zjtelcom.cpct.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description:  协同渠道基本信息配置 和属性配置
 * author: linchao
 * date: 2018/07/02 14:08
 * version: V1.0
 */
@Transactional
@Service
public class MktCamChlConfServiceImpl extends BaseService implements MktCamChlConfService {

    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper;

    @Autowired
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;

    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper; //标签因子

    @Autowired
    private VerbalService verbalService;

    @Autowired
    private CamScriptService camScriptService;

    @Autowired
    private MktCamScriptMapper camScriptMapper;

    @Autowired
    private MktQuestionnaireMapper mktQuestionnaireMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private ContactChannelMapper channelMapper;

    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;

    @Override
    public Map<String, Object> saveMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail) {
        MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            //添加协同渠道基本信息
            CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConfDetail);
            mktCamChlConfDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            mktCamChlConfDO.setCreateDate(new Date());
            mktCamChlConfDO.setCreateStaff(UserUtil.loginId());
            mktCamChlConfDO.setUpdateDate(new Date());
            mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfMapper.insert(mktCamChlConfDO);
            Long evtContactConfId = mktCamChlConfDO.getEvtContactConfId();
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", evtContactConfId);
            mktCamChlConfMap.put("evtContactConfName",mktCamChlConfDO.getEvtContactConfName());
            MktCamChlConfDetail mktCamChlConfDetailNew = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
            // 添加属性
            List<MktCamChlConfAttr> mktCamChlConfAttrList = mktCamChlConfDetail.getMktCamChlConfAttrList();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = new ArrayList<>();
            for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfAttrList) {
                MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(mktCamChlConfAttr, new MktCamChlConfAttrDO());
                mktCamChlConfAttrDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                if (mktCamChlConfAttr.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                    mktCamChlConfAttrDO.setAttrValue(evtContactConfId.toString());
                    //协同渠道自策略规则保存
                    String params = mktCamChlConfAttr.getAttrValue();
                    ruleInsert(evtContactConfId, params);
                }
                //mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                mktCamChlConfAttrDOList.add(mktCamChlConfAttrDO);
            }
            if (mktCamChlConfAttrDOList.size()>0){
                mktCamChlConfAttrMapper.insertBatch(mktCamChlConfAttrDOList);
            }
            List<MktCamChlConfAttr> mktCamChlConfAttrNewList = new ArrayList<>();
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                mktCamChlConfAttrNewList.add(mktCamChlConfAttr);
            }
            mktCamChlConfDetailNew.setMktCamChlConfAttrList(mktCamChlConfAttrNewList);
            // 将推送渠道缓存到redis
            redisUtils.set("MktCamChlConfDetail_" + evtContactConfId, mktCamChlConfDetailNew);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to save MktCamChlConf = {}", mktCamChlConfDO, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", mktCamChlConfDO.getEvtContactConfId());
        }
        return mktCamChlConfMap;
    }

    /**
     * 更新协同渠道配置
     *
     * @param mktCamChlConfDetail
     * @return
     */
    @Override
    public Map<String, Object> updateMktCamChlConf(MktCamChlConfDetail mktCamChlConfDetail) {
        MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            // 更新协同渠道基本信息
            CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConfDetail);
            mktCamChlConfDO.setStatusCd(StatusCode.STATUS_CODE_NOTACTIVE.getStatusCode());
            mktCamChlConfDO.setUpdateDate(new Date());
            mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfMapper.updateByPrimaryKey(mktCamChlConfDO);
            Long evtContactConfId = mktCamChlConfDO.getEvtContactConfId();
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.UPDATE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", evtContactConfId);

            // 将属性插入库中
            List<MktCamChlConfAttr> mktCamChlConfAttrList = mktCamChlConfDetail.getMktCamChlConfAttrList();
            for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfAttrList) {
                MktCamChlConfAttrDO mktCamChlConfAttrDO = new MktCamChlConfAttrDO();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttrDO, mktCamChlConfAttr);
                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                if (mktCamChlConfAttr.getAttrId()!=null && mktCamChlConfAttr.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                    mktCamChlConfAttrDO.setAttrValue(evtContactConfId.toString());
                    //删除旧的关联规则 todo 静态
                    mktVerbalConditionMapper.deleteByVerbalId("1", evtContactConfId);
                    //保存新的规则 j
                    String params = mktCamChlConfAttr.getAttrValue();
                    ruleInsert(evtContactConfId, params);
                }
                mktCamChlConfAttrMapper.updateByPrimaryKey(mktCamChlConfAttrDO);
            }
            // 将推送渠道缓存到redis
            redisUtils.set("MktCamChlConfDetail_" + evtContactConfId, mktCamChlConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to save MktCamChlConf = {}", mktCamChlConfDO, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.UPDATE_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("evtContactConfId", mktCamChlConfDO.getEvtContactConfId());
        }
        return mktCamChlConfMap;
    }

    /**
     * 查询协同渠道配置
     *
     * @param evtContactConfId
     * @return
     */
    @Override
    public Map<String, Object> getMktCamChlConf(Long evtContactConfId) {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
        try {
            MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
            CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDetail, mktCamChlConfDO);
            // 通过查询结果与推送渠道的关系，判断是否为二次协同
            MktCamChlResultConfRelDO mktCamChlResultConfRelDO = mktCamChlResultConfRelMapper.selectByConfId(evtContactConfId);
            if (mktCamChlResultConfRelDO != null) {
                mktCamChlConfDetail.setIsSecondCoop("1");
            } else {
                mktCamChlConfDetail.setIsSecondCoop("0");
            }
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                MktCamChlConfAttr mktCamChlConfAttr = new MktCamChlConfAttr();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttr, mktCamChlConfAttrDO);
                // 协同规则
                if (mktCamChlConfAttr.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                    //通过EvtContactConfId获取规则放入属性中
                    String rule = ruleSelect(mktCamChlConfAttr.getEvtContactConfId());
                    mktCamChlConfAttr.setAttrValue(rule);
                } else if (mktCamChlConfAttr.getAttrId().equals(ConfAttrEnum.QUESTION.getArrId()) && !"".equals(mktCamChlConfAttr.getAttrValue())) {
                    // 问卷
                    Questionnaire questionnaire = mktQuestionnaireMapper.selectByPrimaryKey(Long.valueOf(mktCamChlConfAttr.getAttrValue()));
                    mktCamChlConfAttr.setAttrValueName(questionnaire.getNaireName());
                }
                mktCamChlConfAttrList.add(mktCamChlConfAttr);
            }
            Channel channel = channelMapper.selectByPrimaryKey(mktCamChlConfDO.getContactChlId());
            if (channel != null) {
                mktCamChlConfDetail.setContactChlCode(channel.getContactChlCode());
            }
            // 查询痛痒点话术列表
            Map<String, Object> verbalListMap = verbalService.getVerbalListByConfId(UserUtil.loginId(), evtContactConfId);
            List<VerbalVO> verbalVOList = (List<VerbalVO>) verbalListMap.get("resultMsg");
            mktCamChlConfDetail.setVerbalVOList(verbalVOList);

            // 查询脚本
            CamScript camScript = camScriptMapper.selectByConfId(evtContactConfId);
            mktCamChlConfDetail.setCamScript(camScript);

            mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetail", mktCamChlConfDetail);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to getMktCamChlConf by evtContactConfId = {}", evtContactConfId, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetail", mktCamChlConfDetail);
        }
        return mktCamChlConfMap;
    }


    /**
     * 查询协同渠道列表
     *
     * @return
     */
    @Override
    public Map<String, Object> listMktCamChlConf() {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
        try {
            //获取所有的协同渠道配置基本信息
            List<MktCamChlConfDO> mktCamChlConfDOList = mktCamChlConfMapper.selectAll();
            for (MktCamChlConfDO mktCamChlConfDO : mktCamChlConfDOList) {
                // 遍历查询出对应的属性
                List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                MktCamChlConfDetail mktCamChlConfDetail = new MktCamChlConfDetail();
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDetail, mktCamChlConfDO);
                for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                    MktCamChlConfAttr mktCamChlConfAttr = new MktCamChlConfAttr();
                    CopyPropertiesUtil.copyBean2Bean(mktCamChlConfAttr, mktCamChlConfAttrDO);
                    if (mktCamChlConfAttr.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                        //通过EvtContactConfId获取规则放入属性中
                        String rule = ruleSelect(mktCamChlConfAttr.getEvtContactConfId());
                        mktCamChlConfAttr.setAttrValue(rule);
                    }
                    mktCamChlConfDetail.getMktCamChlConfAttrList().add(mktCamChlConfAttr);
                }
                mktCamChlConfDetailList.add(mktCamChlConfDetail);
            }
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetailList", mktCamChlConfDetailList);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to get mktCamChlConfDetailList evtContactConfId = {}", mktCamChlConfDetailList, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.GET_CAM_CHL_CONF_FAILURE.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetailList", mktCamChlConfDetailList);
        }
        return mktCamChlConfMap;
    }

    /**
     * 删除策略配置
     *
     * @param evtContactConfId
     * @return
     */
    @Override
    public Map<String, Object> deleteMktCamChlConf(Long evtContactConfId, Long ruleId) {
        Map<String, Object> mktCamChlConfMap = null;
        try {
            mktCamChlConfMapper.deleteByPrimaryKey(evtContactConfId);
            mktCamChlConfAttrMapper.deleteByEvtContactConfId(evtContactConfId);
            //判断是否是结果下的
/*        MktCamChlResultConfRelDO mktCamChlResultConfRelDO = mktCamChlResultConfRelMapper.selectByConfId(evtContactConfId);
        if (mktCamChlResultConfRelDO != null) {
            mktCamChlResultConfRelMapper.deleteByPrimaryKey(mktCamChlResultConfRelDO.getMktCamChlResultConfRelId());
        } else if (mktCamChlResultConfRelDO == null && ruleId != null && ruleId != 0) {
            MktStrategyConfRuleDO mktStrategyConfRuleDO = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleId);
            String confIds = mktStrategyConfRuleDO.getEvtContactConfId();
            if (confIds != null && "".equals(confIds)) {
                String[] confIdArray = confIds.split("/");
                String confIdsNew = null;
                for (int i = 0; i < confIdArray.length ; i++) {
                    if(!confIdArray[i].equals(evtContactConfId.toString()) && i == 0 ){
                        confIdsNew += confIdArray[i] ;
                    } else if(!confIdArray[i].equals(evtContactConfId.toString()) && i > 0 ) {
                        confIdsNew += "/" + confIdArray[i] ;
                    }
                }
                mktStrategyConfRuleDO.setEvtContactConfId(confIdsNew);
            }
            mktStrategyConfRuleMapper.updateByPrimaryKey(mktStrategyConfRuleDO);
        }*/


            //删除旧的关联规则 todo 静态
            mktVerbalConditionMapper.deleteByVerbalId("1", evtContactConfId);

            // 将推送渠道缓存到redis
            redisUtils.remove("MktCamChlConfDetail_" + evtContactConfId);
            mktCamChlConfMap = new HashMap<>();
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.DELETE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to delete mktCamChlConfDetailList evtContactConfId = {}", evtContactConfId, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.DELETE_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlConfMap;
    }


    /**
     * 保存协同渠道子策略规则
     *
     * @param evtContactConfId
     * @param param
     */
    public void ruleInsert(Long evtContactConfId, String param) {
        //转换为json对象
        if (param != null && !"null".equals(param)) {
            JSONObject jsonObject = JSONObject.parseObject(param);
            System.out.println(jsonObject.toString());
            //解析参数
            Rule rule = jsonObject.toJavaObject(Rule.class);
            //保存
            saveDetail(evtContactConfId, rule);
        }
    }

    /**
     * 递归规则并保存
     *
     * @param evtContactConfId
     * @param rule
     * @return
     */
    public Long saveDetail(Long evtContactConfId, Rule rule) {
        //保存规则
        List<RuleDetail> list = rule.getListData();
        Long idLeft;
        Long idRight;
        String type = rule.getType();
        if (list.size() > 0) {  //第一层
            //先保存第一条标签因子
            idLeft = insert(evtContactConfId, list.get(0).getId(), list.get(0).getOperType(), list.get(0).getContent());
            for (int i = 1; i < list.size(); i++) {
                idRight = insert(evtContactConfId, list.get(i).getId(), list.get(i).getOperType(), list.get(i).getContent());
                idLeft = insert(evtContactConfId, idLeft, type, idRight);
            }
            if (rule.getRuleChildren() != null) {
                idLeft = insert(evtContactConfId, idLeft, type, saveDetail(evtContactConfId, rule.getRuleChildren()));
            }
        } else {
            idLeft = 0L;
        }
        return idLeft;
    }

    /**
     * 子策略规则插入数据库（标签因子类型）
     *
     * @param evtContactConfId
     * @param left
     * @param operType
     * @param right
     * @return
     */
    public Long insert(Long evtContactConfId, Integer left, String operType, String right) {
        MktVerbalCondition mktVerbalCondition = new MktVerbalCondition();
        mktVerbalCondition.setVerbalId(evtContactConfId);
        mktVerbalCondition.setLeftParam(left.toString());
        mktVerbalCondition.setRightParam(right);
        mktVerbalCondition.setOperType(operType);
        mktVerbalCondition.setLeftParamType("1000"); //标签因子
        mktVerbalCondition.setRightParamType("3000"); //固定值
        mktVerbalCondition.setConditionType("1");
        mktVerbalConditionMapper.insert(mktVerbalCondition);
        return mktVerbalCondition.getConditionId();
    }

    /**
     * 子策略规则插入数据库（表达式类型）
     *
     * @param evtContactConfId
     * @param left
     * @param operType
     * @param right
     * @return
     */
    public Long insert(Long evtContactConfId, Long left, String operType, Long right) {
        MktVerbalCondition mktVerbalCondition = new MktVerbalCondition();
        mktVerbalCondition.setVerbalId(evtContactConfId);
        mktVerbalCondition.setLeftParam(left.toString());
        mktVerbalCondition.setRightParam(right.toString());
        mktVerbalCondition.setOperType(operType);
        mktVerbalCondition.setLeftParamType("2000"); //表达式
        mktVerbalCondition.setRightParamType("2000"); //表达式
        mktVerbalCondition.setConditionType("1");
        mktVerbalConditionMapper.insert(mktVerbalCondition);
        return mktVerbalCondition.getConditionId();
    }

    /**
     * 查询协同子策略规则并拼接格式
     *
     * @param evtContactConfId
     * @return
     */
    public String ruleSelect(Long evtContactConfId) {
        //唯一ID
        //查询出所有规则
        List<MktVerbalCondition> mktVerbalConditions = (List<MktVerbalCondition>) redisUtils.get("RULE_BERBALC_CONDITION_" + evtContactConfId);
        if (mktVerbalConditions == null) {
            mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);
        }


        List<MktVerbalCondition> labels = new ArrayList<>(); //标签因子
        List<MktVerbalCondition> expressions = new ArrayList<>(); //表达式

        //将标签和表达式分类
        for (MktVerbalCondition mktVerbalCondition : mktVerbalConditions) {
            if ("1000".equals(mktVerbalCondition.getLeftParamType())) {
                labels.add(mktVerbalCondition);
            } else if ("2000".equals(mktVerbalCondition.getLeftParamType())) {
                expressions.add(mktVerbalCondition);
            }
        }
        Rule rule = parseRules(labels, expressions, 0);
        return JSON.toJSONString(rule);
    }

    /**
     * 递归查询规则
     *
     * @param labels
     * @param expressions
     * @param index
     * @return
     */
    public Rule parseRules(List<MktVerbalCondition> labels, List<MktVerbalCondition> expressions, int index) {
        Rule rule = new Rule();
        List<RuleDetail> ruleDetails = new ArrayList<>();
        RuleDetail ruleDetail;

        //遍历所有表达式
        if (expressions.size() > 0) {
            rule.setType(expressions.get(index).getOperType());
            for (int i = index; i < expressions.size(); i++) {
                //判断类型  如果不相同就进入下一级
                if (rule.getType().equals(expressions.get(i).getOperType())) {
                    for (MktVerbalCondition condition : labels) {
                        if (expressions.get(i).getLeftParam().equals(condition.getConditionId().toString()) || expressions.get(i).getRightParam().equals(condition.getConditionId().toString())) {
                            ruleDetail = new RuleDetail();
                            ruleDetail.setId(Integer.parseInt(condition.getLeftParam()));
                            //查询获取标签因子名称
                            Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(condition.getLeftParam()));
                            if (label != null) {
                                String[] operators = label.getOperator().split(",");
                                List<OperatorDetail> operatorDetailList = new ArrayList<>();
                                for (String operator : operators) {
                                    OperatorDetail operatorDetail = new OperatorDetail();
                                    operatorDetail.setOperValue(Integer.valueOf(operator));
                                    operatorDetail.setOperName(Operator.getOperator(Integer.valueOf(operator)).getDescription());
                                    operatorDetailList.add(operatorDetail);
                                }

                                List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(label.getInjectionLabelId());
                                List<LabelValueVO> valueList = ChannelUtil.valueList2VOList(labelValues);
                                ruleDetail.setValueList(valueList);
                                ruleDetail.setOperatorList(operatorDetailList);
                                ruleDetail.setName(label.getInjectionLabelName());
                                ruleDetail.setConditionType(label.getConditionType());
                            } else {
                                ruleDetail.setName("");
                            }
                            ruleDetail.setContent(condition.getRightParam());
                            ruleDetail.setOperType(condition.getOperType());
                            ruleDetail.setOperTypeName(Operator.getOperator(Integer.valueOf(condition.getOperType())).getDescription());
                            ruleDetails.add(ruleDetail);
                        }
                    }
                } else {
                    rule.setRuleChildren(parseRules(labels, expressions, i));
                    break;
                }
            }
        }

        //判断是否是一个标签的情况
        if (labels.size() == 1) {
            rule.setType("1000");
            ruleDetail = new RuleDetail();
            ruleDetail.setId(Integer.parseInt(labels.get(0).getLeftParam()));
            //查询获取标签因子名称
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.parseLong(labels.get(0).getLeftParam()));
            if (label != null) {
                ruleDetail.setName(label.getInjectionLabelName());
            } else {
                ruleDetail.setName("");
            }
            ruleDetail.setContent(labels.get(0).getRightParam());
            ruleDetail.setOperType(labels.get(0).getOperType());
            ruleDetails.add(ruleDetail);
        }

        if (ruleDetails.size() == 0) {
            return null;
        }

        rule.setListData(ruleDetails);
        return rule;
    }


    /**
     * 通过父推送渠道Id复制协同渠道
     *
     * @param parentEvtContactConfId
     * @return
     */
    @Override
    public Map<String, Object> copyMktCamChlConf(Long parentEvtContactConfId) throws Exception {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            // 获取原协同渠道
            MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(parentEvtContactConfId);
            mktCamChlConfDO.setEvtContactConfId(null);
            mktCamChlConfDO.setCreateStaff(UserUtil.loginId());
            mktCamChlConfDO.setCreateDate(new Date());
            mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfDO.setUpdateDate(new Date());
            // 新增协同渠道
            mktCamChlConfMapper.insert(mktCamChlConfDO);
            Long childEvtContactConfId = mktCamChlConfDO.getEvtContactConfId();
            // 获取原渠道的属性
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(parentEvtContactConfId);
            // 获取原渠道的规则，通过parentEvtContactConfId获取规则放入属性中
            String rule = ruleSelect(parentEvtContactConfId);
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDONewList = new ArrayList<>();
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                mktCamChlConfAttrDO.setContactChlAttrRstrId(null);
                mktCamChlConfAttrDO.setEvtContactConfId(childEvtContactConfId);
                mktCamChlConfAttrDONewList.add(mktCamChlConfAttrDO);
                if (mktCamChlConfAttrDO.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                    mktCamChlConfAttrDO.setAttrValue(childEvtContactConfId.toString());
                    //协同渠道自策略规则保存
                    mktCamChlConfAttrDO.setAttrValue(childEvtContactConfId.toString());
                    //  String params = mktCamChlConfAttrDO.getAttrValue();
                    ruleInsert(childEvtContactConfId, rule);
                }
                MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                mktCamChlConfAttr.setEvtContactConfId(childEvtContactConfId);
                mktCamChlConfAttrList.add(mktCamChlConfAttr);
            }
            // 批量插入
            mktCamChlConfAttrMapper.insertBatch(mktCamChlConfAttrDONewList);
            MktCamChlConfDetail mktCamChlConfDetailNew = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
            mktCamChlConfDetailNew.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            redisUtils.set("MktCamChlConfDetail_" + mktCamChlConfDetailNew.getEvtContactConfId(), mktCamChlConfDetailNew);

            // 查询痛痒点话术列表
            verbalService.copyVerbal(parentEvtContactConfId, childEvtContactConfId);

            // 查询脚本
            camScriptService.copyCamScript(parentEvtContactConfId, null, childEvtContactConfId);

            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetail", mktCamChlConfDetailNew);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to getMktCamChlConfDO by parentEvtContactConfId = {}", parentEvtContactConfId, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlConfMap;
    }


    /**
     * 通过父推送渠道Id复制协同渠道
     *
     * @param parentEvtContactConfId
     * @return
     */
    @Override
    public Map<String, Object> copyMktCamChlConfFormRedis(Long parentEvtContactConfId, String scriptDesc) throws Exception {
        Map<String, Object> mktCamChlConfMap = new HashMap<>();
        try {
            // 获取原协同渠道
            MktCamChlConfDetail mktCamChlConfDetail = (MktCamChlConfDetail) redisUtils.get("MktCamChlConfDetail_" + parentEvtContactConfId);
            MktCamChlConfDO mktCamChlConfDO = new MktCamChlConfDO();
            List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = new ArrayList<>();
            if (mktCamChlConfDetail != null) {
                CopyPropertiesUtil.copyBean2Bean(mktCamChlConfDO, mktCamChlConfDetail);
                for (MktCamChlConfAttr mktCamChlConfAttr : mktCamChlConfDetail.getMktCamChlConfAttrList()) {
                    MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(mktCamChlConfAttr, new MktCamChlConfAttrDO());
                    mktCamChlConfAttrDOList.add(mktCamChlConfAttrDO);
                }
            } else {
                mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(parentEvtContactConfId);
                mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(parentEvtContactConfId);
            }
            mktCamChlConfDO.setEvtContactConfId(null);
            mktCamChlConfDO.setCreateStaff(UserUtil.loginId());
            mktCamChlConfDO.setCreateDate(new Date());
            mktCamChlConfDO.setUpdateStaff(UserUtil.loginId());
            mktCamChlConfDO.setUpdateDate(new Date());
            // 新增协同渠道
            mktCamChlConfMapper.insert(mktCamChlConfDO);
            Long childEvtContactConfId = mktCamChlConfDO.getEvtContactConfId();
            // 获取原渠道的规则，通过parentEvtContactConfId获取规则放入属性中
            String rule = ruleSelect(parentEvtContactConfId);
            List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                mktCamChlConfAttrDO.setContactChlAttrRstrId(null);
                mktCamChlConfAttrDO.setEvtContactConfId(childEvtContactConfId);

                if (mktCamChlConfAttrDO.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                    //协同渠道自策略规则保存
                    mktCamChlConfAttrDO.setAttrValue(childEvtContactConfId.toString());
                    //  String params = mktCamChlConfAttrDO.getAttrValue();
                    ruleInsert(childEvtContactConfId, rule);
                }
            }

            // 批量插入推送渠道属性
            mktCamChlConfAttrMapper.insertBatch(mktCamChlConfAttrDOList);
            for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
                MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
                mktCamChlConfAttrList.add(mktCamChlConfAttr);
            }
            MktCamChlConfDetail mktCamChlConfDetailNew = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
            mktCamChlConfDetailNew.setMktCamChlConfAttrList(mktCamChlConfAttrList);
            redisUtils.set("MktCamChlConfDetail_" + mktCamChlConfDetailNew.getEvtContactConfId(), mktCamChlConfDetailNew);

            // 查询痛痒点话术列表
            verbalService.copyVerbal(parentEvtContactConfId, childEvtContactConfId);
            // 查询脚本
            Map<String, Object> map = camScriptService.copyCamScript(parentEvtContactConfId, scriptDesc, childEvtContactConfId);
            CamScript newScript = (CamScript) map.get("resultMsg");
            mktCamChlConfDetailNew.setCamScript(newScript);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_SUCCESS);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_SUCCESS.getErrorMsg());
            mktCamChlConfMap.put("mktCamChlConfDetail", mktCamChlConfDetailNew);
        } catch (Exception e) {
            logger.error("[op:MktCamChlConfServiceImpl] fail to getMktCamChlConfDO by parentEvtContactConfId = {}", parentEvtContactConfId, e);
            mktCamChlConfMap.put("resultCode", CommonConstant.CODE_FAIL);
            mktCamChlConfMap.put("resultMsg", ErrorCode.SAVE_CAM_CHL_CONF_FAILURE.getErrorMsg());
        }
        return mktCamChlConfMap;
    }
}