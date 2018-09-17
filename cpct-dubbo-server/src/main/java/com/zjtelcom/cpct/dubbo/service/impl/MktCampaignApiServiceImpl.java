package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyFilterRuleRelMapper;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.Rule;
import com.zjtelcom.cpct.domain.RuleDetail;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.campaign.*;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventDTO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfRuleRel;
import com.zjtelcom.cpct.dubbo.model.MktCampaignResp;
import com.zjtelcom.cpct.dubbo.model.MktStrConfRuleResp;
import com.zjtelcom.cpct.dubbo.model.MktStrategyConfResp;
import com.zjtelcom.cpct.dubbo.model.QryMktCampaignListReq;
import com.zjtelcom.cpct.dubbo.service.MktCampaignApiService;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;
import com.zjtelcom.cpct.vo.grouping.TarGrpVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
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
     * 活动关联
     */

    @Autowired
    private MktCampaignRelMapper mktCampaignRelMapper;

    /**
     * 活动与事件关联
     */

    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;

    /**
     * 系统参数
     */

    @Autowired
    private SysParamsMapper sysParamsMapper;
    /**
     * 事件
     */

    @Autowired
    private ContactEvtMapper contactEvtMapper;
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

    /**
     * 下发城市与活动关联
     */
    @Autowired
    private MktCamCityRelMapper mktCamCityRelMapper;

    @Autowired
    private ContactChannelMapper contactChannelMapper;
    /**
     * 下发地市
     */
    @Autowired
    private SysAreaMapper sysAreaMapper;

    /**
     * 策略与过滤规则的关系
     */
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;

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
    private RedisUtils redisUtils;

    @Autowired
    private FilterRuleMapper filterRuleMapper;

    @Autowired
    private OfferMapper offerMapper;

    @Autowired
    private MktCamItemMapper mktCamItemMapper;

    @Autowired
    private MktCamChlResultMapper mktCamChlResultMapper;

    @Autowired
    private MktCamChlResultConfRelMapper mktCamChlResultConfRelMapper;

    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;

    @Autowired
    private MktVerbalConditionMapper mktVerbalConditionMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;

    @Autowired
    private ContactChannelMapper channelMapper;

    @Autowired
    private MktVerbalMapper verbalMapper;

    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;

    @Autowired
    private InjectionLabelMapper labelMapper;

    @Autowired
    private MktCamScriptMapper camScriptMapper;

    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private DisplayColumnLabelMapper displayColumnLabelMapper;

    /**
     * 查询营销活动列表(分页)
     *
     * @param
     * @return
     */
/*

    @Override
    public Map<String, Object> qryMktCampaignList(QryMktCampaignListReq qryMktCampaignListReq) throws Exception {
        Map<String, Object> qryMktCampaignListMap = new HashMap<>();
        // 获取分页信息，默认第1页，10条数据
        Integer page = 1;
        Integer pageSize = 10;
        if (qryMktCampaignListReq.getPageInfo() != null) {
            if (qryMktCampaignListReq.getPageInfo().getPage() != null && qryMktCampaignListReq.getPageInfo().getPage() != 0) {
                page = qryMktCampaignListReq.getPageInfo().getPage();
            }
            if (qryMktCampaignListReq.getPageInfo().getPageSize() != null && qryMktCampaignListReq.getPageInfo().getPageSize() != 0) {
                page = qryMktCampaignListReq.getPageInfo().getPageSize();
            }
        }

        MktCampaignDO mktCampaignDOReq = new MktCampaignDO();
        try {
            CopyPropertiesUtil.copyBean2Bean(mktCampaignDOReq, qryMktCampaignListReq);
        } catch (Exception e) {
            logger.error("[op:MktCampaignServiceImpl] falied to get mktCampaignDO = {}", JSON.toJSON(mktCampaignDOReq), e);
        }

        PageHelper.startPage(page, pageSize);
        List<MktCampaignDO> mktCampaignDOList = mktCampaignMapper.qryMktCampaignListByCondition(mktCampaignDOReq);
        Page pageInfo = new Page(new PageInfo(mktCampaignDOList));
        List<MktCampaign> mktCampaignList = new ArrayList<>();
        try {
            for (MktCampaignDO mktCampaignDOResp : mktCampaignDOList) {
                MktCampaign mktCampaign = new MktCampaign();
                CopyPropertiesUtil.copyBean2Bean(mktCampaign, mktCampaignDOResp);
                mktCampaignList.add(mktCampaign);
            }
        } catch (Exception e) {
            logger.error("[op:MktCampaignServiceImpl] falied to get mktCampaignList = {}", JSON.toJSON(mktCampaignList), e);
        }
        qryMktCampaignListMap.put("resultCode", CommonConstant.CODE_SUCCESS);
        qryMktCampaignListMap.put("resultMsg", "查询活动列表成功！");
        qryMktCampaignListMap.put("mktCampaigns", mktCampaignList);
        qryMktCampaignListMap.put("pageInfo", pageInfo);
        return qryMktCampaignListMap;
    }
*/
    @Override
    public Map<String, Object> qryMktCampaignDetail(Long mktCampaignId) throws Exception {
        // 获取关系
        List<MktCampaignRelDO> mktCampaignRelDOList = mktCampaignRelMapper.selectByAmktCampaignId(mktCampaignId, StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        List<CityProperty> applyRegionIds = new ArrayList<>();
        // 获取活动基本信息
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        MktCampaignResp mktCampaignResp = BeanUtil.create(mktCampaignDO, new MktCampaignResp());

        // 获取下发城市集合

        List<MktCamCityRelDO> mktCamCityRelDOList = mktCamCityRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        List<SysArea> sysAreaList = new ArrayList<>();
        for (MktCamCityRelDO mktCamCityRelDO : mktCamCityRelDOList) {
            SysArea sysArea = sysAreaMapper.selectByPrimaryKey(mktCamCityRelDO.getCityId().intValue());
            sysAreaList.add(sysArea);
        }
        mktCampaignResp.setSysAreaList(sysAreaList);

        // 获取所有的sysParam
        Map<String, String> paramMap = new HashMap<>();
        List<SysParams> sysParamList = sysParamsMapper.selectAll("", 0L);
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
        mktCampaignResp.setExecTypeValue(paramMap.
                get(ParamKeyEnum.EXEC_TYPE.getParamKey() + mktCampaignDO.getExecType()));

        // 获取活动关联的事件
        List<MktCamEvtRelDO> mktCamEvtRelDOList = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        if (mktCamEvtRelDOList != null) {
            List<EventDTO> eventDTOList = new ArrayList<>();
            for (MktCamEvtRelDO mktCamEvtRelDO : mktCamEvtRelDOList) {
                Long eventId = mktCamEvtRelDO.getEventId();
                ContactEvt contactEvt = contactEvtMapper.getEventById(eventId);
                if (contactEvt != null) {
                    EventDTO eventDTO = new EventDTO();
                    eventDTO.setEventId(eventId);
                    eventDTO.setEventName(contactEvt.getContactEvtName());
                    eventDTOList.add(eventDTO);
                }
            }
            mktCampaignResp.setEventDTOS(eventDTOList);
        }
        // 获取试运算展示列标签
        DisplayColumn calcDisplay = new DisplayColumn();
        calcDisplay.setDisplayColumnId(mktCampaignDO.getCalcDisplay());
        Map<String, Object> calcDisplayListMap = queryLabelListByDisplayId(calcDisplay);
        mktCampaignResp.setCalcDisplayList((List<MessageLabelInfo>) calcDisplayListMap.get("resultMsg"));

        // 获取isale展示列标签
        DisplayColumn isaleDisplay = new DisplayColumn();
        isaleDisplay.setDisplayColumnId(mktCampaignDO.getIsaleDisplay());
        Map<String, Object> isaleDisplayListMap = queryLabelListByDisplayId(isaleDisplay);
        mktCampaignResp.setIsaleDisplayList((List<MessageLabelInfo>) isaleDisplayListMap.get("resultMsg"));


        // 获取活动关联策略集合
        List<MktStrategyConfResp> mktStrategyConfRespList = new ArrayList<>();

        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOList = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamStrategyConfRelDO mktCamStrategyConfRelDO : mktCamStrategyConfRelDOList) {
            //MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktCamStrategyConfRelDO.getStrategyConfId());
            MktStrategyConfResp mktStrategyConfResp = getMktStrategyConf(mktCamStrategyConfRelDO.getStrategyConfId());
            mktStrategyConfRespList.add(mktStrategyConfResp);
        }
        mktCampaignResp.setMktStrategyConfRespList(mktStrategyConfRespList);
        Map<String, Object> maps = new HashMap<>();
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("mktCampaignResp", mktCampaignResp);
        return maps;
    }

    /**
     * 查询配置配置信息
     *
     * @param mktStrategyConfId
     * @return
     */

    public MktStrategyConfResp getMktStrategyConf(Long mktStrategyConfId) throws Exception {
        MktStrategyConfResp mktStrategyConfResp = new MktStrategyConfResp();
        //查出获取所有的城市信息, 设成全局Map
        Map<Integer, String> cityMap = new HashMap<>();
        List<SysArea> sysAreaAllList = sysAreaMapper.selectAll();
        for (SysArea sysArea : sysAreaAllList) {
            cityMap.put(sysArea.getAreaId(), sysArea.getName());
        }

        //更具Id查询策略配置信息
        MktStrategyConfDO mktStrategyConfDO = mktStrategyConfMapper.selectByPrimaryKey(mktStrategyConfId);
        CopyPropertiesUtil.copyBean2Bean(mktStrategyConfResp, mktStrategyConfDO);
        // 适用城市
        List<Integer> areaIdList = new ArrayList<>();
        List<SysArea> sysAreaList = new ArrayList<>();
        String[] areaIds = mktStrategyConfDO.getAreaId().split("/");
        if (areaIds != null && !"".equals(areaIds[0])) {
            for (String areaId : areaIds) {
                areaIdList.add(Integer.valueOf(areaId));
                SysArea sysArea = (SysArea) redisUtils.get("CITY_" + areaId);
                sysAreaList.add(sysArea);
            }
            mktStrategyConfResp.setAreaList(sysAreaList);
        }

        // 策略适用渠道
        List<Channel> channelsList = new ArrayList<>();
        String[] channelIds = mktStrategyConfDO.getChannelsId().split("/");
        for (String channelId : channelIds) {
            Channel channel = contactChannelMapper.selectByPrimaryKey(Long.valueOf(channelId));
            channelsList.add(channel);
        }
        mktStrategyConfResp.setChannelsList(channelsList);


        // 获取过滤规则集合
        List<Long> filterRuleIdList = mktStrategyFilterRuleRelMapper.selectByStrategyId(mktStrategyConfId);
        List<FilterRule> filterRuleList = new ArrayList<>();
        for (Long filterRuleId : filterRuleIdList) {
            FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(filterRuleId);
            filterRuleList.add(filterRule);
        }
        mktStrategyConfResp.setFilterRuleList(filterRuleList);


        //查询与策略匹配的所有规则
        List<MktStrConfRuleResp> mktStrConfRuleRespList = new ArrayList<>();
        List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = mktStrategyConfRuleMapper.selectByMktStrategyConfId(mktStrategyConfId);
        List<MktStrategyConfRuleRel> mktStrategyConfRuleRelList = new ArrayList<>();
        for (MktStrategyConfRuleDO mktStrategyConfRuleDO : mktStrategyConfRuleDOList) {
            MktStrConfRuleResp mktStrConfRuleResp = BeanUtil.create(mktStrategyConfRuleDO, new MktStrConfRuleResp());

            // 目标分群
            List<TarGrpConditionVO> tarGrpConditionVOList = listTarGrpCondition(mktStrategyConfRuleDO.getTarGrpId());
            mktStrConfRuleResp.setTarGrpConditionList(tarGrpConditionVOList);

            // 销售品
            if (mktStrategyConfRuleDO.getProductId() != null) {
                String[] productIds = mktStrategyConfRuleDO.getProductId().split("/");
                List<Long> productIdList = new ArrayList<>();
                List<Offer> offerList = new ArrayList<>();
                for (int i = 0; i < productIds.length; i++) {
                    if (productIds[i] != "" && !"".equals(productIds[i])) {
                        productIdList.add(Long.valueOf(productIds[i]));
                        MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(productIds[i]));
                        Offer offer = offerMapper.selectByPrimaryKey(mktCamItem.getItemId().intValue());
                        offerList.add(offer);
                    }
                }
                mktStrConfRuleResp.setOfferList(offerList);
            }

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
        List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList = mktCamChlConfAttrMapper.selectByEvtContactConfId(evtContactConfId);
        MktCamChlConfDetail mktCamChlConfDetail = BeanUtil.create(mktCamChlConfDO, new MktCamChlConfDetail());
        List<MktCamChlConfAttr> mktCamChlConfAttrList = new ArrayList<>();
        for (MktCamChlConfAttrDO mktCamChlConfAttrDO : mktCamChlConfAttrDOList) {
            MktCamChlConfAttr mktCamChlConfAttr = BeanUtil.create(mktCamChlConfAttrDO, new MktCamChlConfAttr());
            if (mktCamChlConfAttr.getAttrId().equals(ConfAttrEnum.RULE.getArrId())) {
                //通过EvtContactConfId获取规则放入属性中
                String rule = ruleSelect(mktCamChlConfAttr.getEvtContactConfId());
                mktCamChlConfAttr.setAttrValue(rule);
            }
            mktCamChlConfAttrList.add(mktCamChlConfAttr);
        }
        // 查询痛痒点话术列表
        Map<String, Object> verbalListMap = getVerbalListByConfId(UserUtil.loginId(), evtContactConfId);
        List<VerbalVO> verbalVOList = (List<VerbalVO>) verbalListMap.get("resultMsg");
        mktCamChlConfDetail.setVerbalVOList(verbalVOList);

        // 查询脚本
        CamScript camScript = camScriptMapper.selectByConfId(evtContactConfId);
        mktCamChlConfDetail.setCamScript(camScript);
        mktCamChlConfDetail.setMktCamChlConfAttrList(mktCamChlConfAttrList);

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

    public Map<String, Object> getVerbalListByConfId(Long userId, Long confId) {
        Map<String, Object> result = new HashMap<>();
        //todo 推送渠道对象
        List<MktVerbal> verbalList = verbalMapper.findVerbalListByConfId(confId);
        List<VerbalVO> verbalVOS = new ArrayList<>();
        for (MktVerbal verbal : verbalList) {
            if (verbal == null) {
                result.put("resultCode", CODE_FAIL);
                result.put("resultMsg", "痛痒点话术不存在");
                return result;
            }
            VerbalVO verbalVO = supplementVo(ChannelUtil.map2VerbalVO(verbal), verbal);
            verbalVOS.add(verbalVO);
        }
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", verbalVOS);
        return result;
    }

    /**
     * 痛痒点话术返回结果包装
     */

    private VerbalVO supplementVo(VerbalVO verbalVO, MktVerbal verbal) {
        List<VerbalConditionVO> conditionVOList = new ArrayList<>();
        List<MktVerbalCondition> conditions = verbalConditionMapper.findChannelConditionListByVerbalId(verbal.getVerbalId());
        for (MktVerbalCondition condition : conditions) {
            VerbalConditionVO vo = BeanUtil.create(condition, new VerbalConditionVO());
            vo.setOperName(Operator.getOperator(Integer.valueOf(condition.getOperType())).getDescription());
            if (!condition.getLeftParamType().equals("2000")) {
                Label label = labelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                if (label.getConditionType() != null && !label.getConditionType().equals("")) {
                    vo.setConditionType(label.getConditionType());
                }
                vo.setLeftParamName(label.getInjectionLabelName());
                if (label.getRightOperand() != null) {
                    vo.setValueList(ChannelUtil.StringToList(label.getRightOperand()));
                }
                setOperator(vo, label);
            }
            conditionVOList.add(vo);
        }
        verbalVO.setConditionList(conditionVOList);
        Channel channel = channelMapper.selectByPrimaryKey(verbalVO.getChannelId());
        if (channel != null) {
            verbalVO.setChannelName(channel.getContactChlName());
            verbalVO.setChannelParentId(channel.getParentId());
            Channel parent = channelMapper.selectByPrimaryKey(channel.getParentId());
            if (parent != null) {
                verbalVO.setChannelParentName(parent.getContactChlName());
            }
        }
        return verbalVO;
    }


    private void setOperator(VerbalConditionVO vo, Label label) {
        if (label.getOperator() != null && !label.getOperator().equals("")) {
            List<String> opratorList = ChannelUtil.StringToList(label.getOperator());
            List<OperatorDetail> opStList = new ArrayList<>();
            for (String operator : opratorList) {
                Operator op = Operator.getOperator(Integer.valueOf(operator));
                OperatorDetail detail = new OperatorDetail();
                if (op != null) {
                    detail.setOperValue(op.getValue());
                    detail.setOperName(op.getDescription());
                }
                opStList.add(detail);
            }
            vo.setOperatorList(opStList);
        }
    }


    public List<TarGrpConditionVO> listTarGrpCondition(Long tarGrpId) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        //通过mktCamGrpRulId获取所有活动关联关系
//        MktCamGrpRul mktCamGrpRul = mktCamGrpRulMapper.selectByPrimaryKey(mktCamGrpRulId);
//        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(mktCamGrpRul.getTarGrpId());
        List<TarGrpCondition> listTarGrpCondition = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
        List<TarGrpConditionVO> grpConditionList = new ArrayList<>();
        List<TarGrpVO> tarGrpVOS = new ArrayList<>();//传回前端展示信息
        for (TarGrpCondition tarGrpCondition : listTarGrpCondition) {
            List<String> valueList = new ArrayList<>();
            List<OperatorDetail> operatorList = new ArrayList<>();
            TarGrpConditionVO tarGrpConditionVO = new TarGrpConditionVO();
            CopyPropertiesUtil.copyBean2Bean(tarGrpConditionVO, tarGrpCondition);
            //塞入左参中文名
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(tarGrpConditionVO.getLeftParam()));
            if (label == null) {
                continue;
            }
            tarGrpConditionVO.setLeftParamName(label.getInjectionLabelName());
/*            //塞入领域
            FitDomain fitDomain = null;
            if (label.getFitDomain() != null) {
                fitDomain = FitDomain.getFitDomain(Integer.parseInt(label.getFitDomain()));
                tarGrpConditionVO.setFitDomainId(Long.valueOf(fitDomain.getValue()));
                tarGrpConditionVO.setFitDomainName(fitDomain.getDescription());
            }*/
            //将操作符转为中文
            if (tarGrpConditionVO.getOperType() != null && !tarGrpConditionVO.getOperType().equals("")) {
                Operator op = Operator.getOperator(Integer.parseInt(tarGrpConditionVO.getOperType()));
                tarGrpConditionVO.setOperTypeName(op.getDescription());
            }
            //todo 通过左参id
            String operators = label.getOperator();
            String[] operator = operators.split(",");
            if (operator.length > 1) {
                for (int i = 0; i < operator.length; i++) {
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[i]));
                    OperatorDetail operatorDetail = new OperatorDetail();
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            } else {
                if (operator.length == 1) {
                    OperatorDetail operatorDetail = new OperatorDetail();
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[0]));
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            }
            String rightOperand = label.getRightOperand();
            String[] rightOperands = rightOperand.split(",");
            if (rightOperands.length > 1) {
                for (int i = 0; i < rightOperands.length; i++) {
                    valueList.add(rightOperands[i]);
                }
            } else {
                if (rightOperands.length == 1) {
                    valueList.add(rightOperands[0]);
                }
            }
            tarGrpConditionVO.setConditionType(label.getConditionType());
            tarGrpConditionVO.setValueList(valueList);
            tarGrpConditionVO.setOperatorList(operatorList);
            grpConditionList.add(tarGrpConditionVO);
        }
        return grpConditionList;
    }


    /**
     * 获取展示列标签列表
     *
     * @param req
     * @return
     */

    public Map<String, Object> queryLabelListByDisplayId(DisplayColumn req) {
        Map<String, Object> maps = new HashMap<>();
        List<DisplayColumnLabel> realList = displayColumnLabelMapper.findListByDisplayId(req.getDisplayColumnId());
        List<LabelDTO> labelList = new ArrayList<>();
        List<Long> messageTypes = new ArrayList<>();

        for (DisplayColumnLabel real : realList) {
            Label label = injectionLabelMapper.selectByPrimaryKey(real.getInjectionLabelId());
            if (label == null) {
                continue;
            }
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setInjectionLabelId(label.getInjectionLabelId());
            labelDTO.setInjectionLabelName(label.getInjectionLabelName());
            labelDTO.setMessageType(real.getMessageType());
            labelList.add(labelDTO);
            if (!messageTypes.contains(real.getMessageType())) {
                messageTypes.add(real.getMessageType());
            }
        }
        List<MessageLabelInfo> mlInfoList = new ArrayList<>();
        for (int i = 0; i < messageTypes.size(); i++) {

            Long messageType = messageTypes.get(i);
            Message messages = messageMapper.selectByPrimaryKey(messageType);
            MessageLabelInfo info = BeanUtil.create(messages, new MessageLabelInfo());
            List<LabelDTO> dtoList = new ArrayList<>();
            for (LabelDTO dto : labelList) {
                if (messageType.equals(dto.getMessageType())) {
                    dtoList.add(dto);
                }
            }
            info.setLabelDTOList(dtoList);
            //判断是否选中
            if (dtoList.isEmpty()) {
                info.setChecked("1");//false
            } else {
                info.setChecked("0");//true
            }
            mlInfoList.add(info);
        }
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg", mlInfoList);
        return maps;

    }
}
