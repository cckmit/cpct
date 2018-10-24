package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.Rule;
import com.zjtelcom.cpct.domain.RuleDetail;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.*;
import com.zjtelcom.cpct.dto.filter.FilterRuleModel;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.dubbo.model.MktCampaignResp;
import com.zjtelcom.cpct.dubbo.model.MktStrConfRuleResp;
import com.zjtelcom.cpct.dubbo.model.MktStrategyConfResp;
import com.zjtelcom.cpct.dubbo.model.Ret;
import com.zjtelcom.cpct.dubbo.service.MktCampaignApiService;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/07/17 11:11
 * @version: V1.0
 */

@Service
@Transactional
public class MktCampaignApiServiceImpl implements MktCampaignApiService {

    private static final Logger logger = LoggerFactory.getLogger(MktCampaignApiServiceImpl.class);
    /**
     * 营销活动
     */
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    /**
     * 系统参数
     */
    @Autowired
    private SysParamsMapper sysParamsMapper;
    /**
     * 策略配置和活动关联
     */
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    /**
     * 策略配置基本信息
     */
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;

    @Autowired
    private ContactChannelMapper contactChannelMapper;
    /**
     * 策略配置规则Mapper
     */
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;
    /**
     * 首次协同
     */
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;

    @Autowired
    private FilterRuleMapper filterRuleMapper;

    @Autowired
    private MktCamChlResultMapper mktCamChlResultMapper;

    @Autowired
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Override
    public Ret qryMktCampaignDetail(Long mktCampaignId) throws Exception {
        Ret<MktCampaignResp> ret = new Ret<>();
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        MktCampaignResp mktCampaignResp = BeanUtil.create(mktCampaignDO, new MktCampaignResp());
        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", "");
        for (SysParams sysParams : sysParamList) {
            paramMap.put(sysParams.getParamKey() + sysParams.getParamValue(), sysParams.getParamName());
        }
        mktCampaignResp.setTiggerTypeValue(paramMap.
                get(ParamKeyEnum.TIGGER_TYPE.getParamKey() + mktCampaignDO.getTiggerType()));
        mktCampaignResp.setMktCampaignCategoryValue(paramMap.
                get(ParamKeyEnum.MKT_CAMPAIGN_CATEGORY.getParamKey() + mktCampaignDO.getMktCampaignCategory()));
        mktCampaignResp.setMktCampaignTypeValue(paramMap.
                get(ParamKeyEnum.MKT_CAMPAIGN_TYPE.getParamKey() + mktCampaignDO.getMktCampaignType()));
        mktCampaignResp.setStatusCdValue(paramMap.
                get(ParamKeyEnum.STATUS_CD.getParamKey() + mktCampaignDO.getStatusCd()));

        // 获取活动关联策略集合
        List<MktStrategyConfResp> mktStrategyConfRespList = new ArrayList<>();
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            MktStrategyConfResp mktStrategyConfResp = getMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
            mktStrategyConfRespList.add(mktStrategyConfResp);
        }
        mktCampaignResp.setMktStrategyConfRespList(mktStrategyConfRespList);
        ret.setResultCode(CODE_SUCCESS);
        ret.setData(mktCampaignResp);
        ret.setResultMsg("success");
        return ret;
    }

    /**
     * 查询配置配置信息
     *
     * @param mktStrategyConfId
     * @return
     */

    public MktStrategyConfResp getMktStrategyConf(Long mktStrategyConfId) throws Exception {
        MktStrategyConfResp mktStrategyConfResp = new MktStrategyConfResp();

        //更具Id查询策略配置信息
        MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktStrategyConfId);
        CopyPropertiesUtil.copyBean2Bean(mktStrategyConfResp, mktStrategyConfDO);

        // 获取过滤规则集合
        List<FilterRuleModel> filterRuleModels = filterRuleMapper.selectFilterRuleByStrategyId(mktStrategyConfId);
        mktStrategyConfResp.setFilterRuleModelList(filterRuleModels);

        //查询与策略匹配的所有规则
        List<MktStrConfRuleResp> mktStrConfRuleRespList = new ArrayList<>();
        List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfId);
        List<MktStrategyConfRuleRel> mktStrategyConfRuleRelList = new ArrayList<>();
        for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
            MktStrConfRuleResp mktStrConfRuleResp = BeanUtil.create(mktStrategyConfRuleDO, new MktStrConfRuleResp());

            if (mktStrategyConfRuleDO.getEvtContactConfId() != null) {
                String[] evtContactConfIds = mktStrategyConfRuleDO.getEvtContactConfId().split("/");
                List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                List<MktCamChlConf> mktCamChlConfList = new ArrayList<>();
                for (int i = 0; i < evtContactConfIds.length; i++) {
                    if (evtContactConfIds[i] != "" && !"".equals(evtContactConfIds[i])) {
                        MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(Long.valueOf(evtContactConfIds[i]));
                        MktCamChlConf mktCamChlConf = BeanUtil.create(mktCamChlConfDO, new MktCamChlConf());
                        mktCamChlConfList.add(mktCamChlConf);
                        MktCamChlConfDetail mktCamChlConfDetail = getMktCamChlConf(Long.valueOf(evtContactConfIds[i]));
                        // 获取触点渠道编码
                        Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDetail.getContactChlId());
                        if(channel!=null){
                            mktCamChlConfDetail.setContactChlCode(channel.getContactChlCode());
                        }
                        mktCamChlConfDetailList.add(mktCamChlConfDetail);
                    }
                }
                mktStrConfRuleResp.setMktCamChlConfDetailList(mktCamChlConfDetailList);
            }

            if (mktStrategyConfRuleDO.getMktCamChlResultId() != null) {
                String[] mktCamChlResultIds = mktStrategyConfRuleDO.getMktCamChlResultId().split("/");
                List<MktCamChlResult> mktCamChlResultList = new ArrayList<>();
                for (int i = 0; i < mktCamChlResultIds.length; i++) {
                    if (mktCamChlResultIds[i] != null && !"".equals(mktCamChlResultIds[i])) {
                        MktCamChlResultDO mktCamChlResultDO = mktCamChlResultMapper.selectByPrimaryKey(Long.valueOf(mktCamChlResultIds[i]));
                        MktCamChlResult mktCamChlResult = BeanUtil.create(mktCamChlResultDO, new MktCamChlResult());
                        List<MktCamChlResultConfRelDO> mktCamChlResultConfRelDOList = mktCamChlResultConfRelMapper.selectByMktCamChlResultId(mktCamChlResultDO.getMktCamChlResultId());
                        List<MktCamChlConfDetail> mktCamChlConfDetailList = new ArrayList<>();
                        for (MktCamChlResultConfRelDO mktCamChlResultConfRelDO : mktCamChlResultConfRelDOList) {
                            MktCamChlConfDetail mktCamChlConfDetail = getMktCamChlConf(mktCamChlResultConfRelDO.getEvtContactConfId());
                            mktCamChlConfDetailList.add(mktCamChlConfDetail);
                        }
                        mktCamChlResult.setMktCamChlConfDetailList(mktCamChlConfDetailList);
                        mktCamChlResultList.add(mktCamChlResult);
                    }
                }
                mktStrConfRuleResp.setMktCamChlResultList(mktCamChlResultList);
            }
            mktStrConfRuleRespList.add(mktStrConfRuleResp);
        }
        mktStrategyConfResp.setMktStrConfRuleRespList(mktStrConfRuleRespList);
        return mktStrategyConfResp;
    }


    public MktCamChlConfDetail getMktCamChlConf(Long evtContactConfId) {
        MktCamChlConfDO mktCamChlConfDO = mktCamChlConfMapper.selectByPrimaryKey(evtContactConfId);
        MktCamChlConfDetail mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
        return mktCamChlConfDetail;
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
        List<MktVerbalCondition> mktVerbalConditions = mktVerbalConditionMapper.findConditionListByVerbalId(evtContactConfId);
        List<MktVerbalCondition> labels = new ArrayList<>(); //标签因子
        List<MktVerbalCondition> expressions = new ArrayList<>(); //表达式

        //分类
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
                                ruleDetail.setName(label.getInjectionLabelName());
                            } else {
                                ruleDetail.setName("");
                            }
                            ruleDetail.setContent(condition.getRightParam());
                            ruleDetail.setOperType(condition.getOperType());
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

}
