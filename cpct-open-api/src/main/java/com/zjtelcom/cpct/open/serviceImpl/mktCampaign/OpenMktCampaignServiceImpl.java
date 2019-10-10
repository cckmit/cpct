package com.zjtelcom.cpct.open.serviceImpl.mktCampaign;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.strategy.*;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleRelDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.PostEnum;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.event.OpenEvent;
import com.zjtelcom.cpct.open.entity.event.OpenEvtTrigCamRulEntity;
import com.zjtelcom.cpct.open.entity.mktAlgorithms.OpenMktAlgorithmsEntity;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfAttrEntity;
import com.zjtelcom.cpct.open.entity.mktCamChlConf.OpenMktCamChlConfEntity;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItem;
import com.zjtelcom.cpct.open.entity.mktCamItem.OpenMktCamItemEntity;
import com.zjtelcom.cpct.open.entity.mktCampaign.OpenMktCampaign;
import com.zjtelcom.cpct.open.entity.mktCampaignEntity.*;
import com.zjtelcom.cpct.open.entity.mktCpcAlgorithmsRule.OpenMktCpcAlgorithmsRulEntity;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategy;
import com.zjtelcom.cpct.open.entity.mktStrategy.OpenMktStrategyEntity;
import com.zjtelcom.cpct.open.entity.script.OpenScript;
import com.zjtelcom.cpct.open.entity.tarGrp.OpenTarGrpConditionEntity;
import com.zjtelcom.cpct.open.entity.tarGrp.OpenTarGrpEntity;
import com.zjtelcom.cpct.open.service.mktCampaign.OpenMktCampaignService;
import com.zjtelcom.cpct.pojo.MktCamStrategyRel;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.alibaba.fastjson.JSONObject;

import java.util.*;

/**
 * @author: anson
 * @CreateDate: 2018-11-05 17:32:51
 * @version: V 1.0
 * 营销活动openapi相关服务
 */
@Service
@Transactional
public class OpenMktCampaignServiceImpl extends BaseService implements OpenMktCampaignService {

    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    @Autowired
    private MktStrategyConfRuleMapper  mktStrategyConfRuleMapper;
    @Autowired
    private MktCamStrategyRelMapper mktCamStrategyRelMapper;
    @Autowired
    private MktStrategyMapper mktStrategyMapper;
    @Autowired
    private MktStrategyConfMapper mktStrategyConfMapper;
    @Autowired
    private MktCamScriptMapper mktCamScriptMapper;
    @Autowired
    private MktCamChlConfMapper mktCamChlConfMapper;
    @Autowired
    private MktCamChlConfAttrMapper mktCamChlConfAttrMapper;
    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper;
    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private MktAlgorithmsMapper mktAlgorithmsMapper;
    @Autowired
    private MktCpcAlgorithmsRulMapper mktCpcAlgorithmsRulMapper;
    @Autowired
    private MktCamRecomCalcRelMapper mktCamRecomCalcRelMapper;
    @Autowired
    private MktCamCityRelMapper mktCamCityRelMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private ContactChannelMapper contactChannelMapper;
    @Autowired
    private MktStrategyFilterRuleRelMapper mktStrategyFilterRuleRelMapper;
    @Autowired
    private OfferMapper offerMapper;
    @Autowired
    private MktResourceMapper mktResourceMapper;
    @Autowired
    private SysAreaMapper sysAreaMapper;
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;


    /**
     * 查询营销活动信息
     *
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> queryById(String id) {
        Map<String, Object> resultMap = new HashMap<>();
        long queryId = CommonUtil.stringToLong(id);
        MktCampaignDO mktCampaignDO = mktCampaignMapper.selectByPrimaryKey(queryId);
        if (null == mktCampaignDO) {
            JSONObject json=new JSONObject();
            json.put("message","对应营销活动信息不存在!");
            resultMap.put("params", json);
            return resultMap;
        }
        //将活动信息转换为openapi返回格式
        OpenMktCampaign campaign=getOpenCampaign(mktCampaignDO);
        //活动推荐条目列表  活动脚本列表  维挽策略列表
        List<OpenMktCamItem> mktCamItemList=new ArrayList<>();
        List<OpenScript> mktScriptList=new ArrayList<>();
        List<OpenMktStrategy> mktStrategyList=new ArrayList<>();

        //1.查询营销推荐维挽策略
        List<MktCamStrategyRel> mktCamStrategyRels = mktCamStrategyRelMapper.selectByMktCampaignId(queryId);
        if (!mktCamStrategyRels.isEmpty()){
            for (MktCamStrategyRel rel:mktCamStrategyRels){
                MktStrategy mktStrategy = mktStrategyMapper.selectByPrimaryKey(rel.getStrategyId());
                if(null!=mktStrategy){
                    OpenMktStrategy openMktStrategy = BeanUtil.create(mktStrategy, new OpenMktStrategy());
                    if(null!=mktStrategy.getStatusDate()){
                        openMktStrategy.setStatusDate(DateUtil.getDatetime(mktStrategy.getStatusDate()));
                    }
                    mktStrategyList.add(openMktStrategy);
                }
            }
            campaign.setMktStrategy(mktStrategyList);
        }

        //2. 查询营销活动推荐脚本
        List<CamScript> camScripts = mktCamScriptMapper.selectByCampaignId(queryId);
        if(!camScripts.isEmpty()){
            for (CamScript script:camScripts){
                if(null!=script){
                    OpenScript openScript = BeanUtil.create(script, new OpenScript());
                    openScript.setId(script.getMktCampaignScptId());
                    openScript.setHref("/mktScript/"+script.getMktCampaignScptId().toString());
                    openScript.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
                    mktScriptList.add(openScript);
                }
            }
            campaign.setMktCamScript(mktScriptList);
        }


        //查询相关营销活动推荐条目列表(属于规则) 活动id  471比较多   468比较少
        //2.查出活动下的策略   500
        List<MktCamStrategyConfRelDO> mktCamStrategyConfRelDOS = mktCamStrategyConfRelMapper.selectByMktCampaignId(mktCampaignDO.getMktCampaignId());
        if (mktCamStrategyConfRelDOS.isEmpty()){
            resultMap.put("params", campaign);
            return resultMap;
        }

        //3.查出策略下的规则
        for (MktCamStrategyConfRelDO m:mktCamStrategyConfRelDOS){
            List<MktStrategyConfRuleRelDO> mktStrategyConfRuleRelDOS = mktStrategyConfRuleRelMapper.selectByMktStrategyConfId(m.getStrategyConfId());

            if(mktStrategyConfRuleRelDOS.isEmpty()){
                resultMap.put("params", campaign);
                return resultMap;
            }
            //3.1 通过规则id查出规则
            for (MktStrategyConfRuleRelDO ruleRelDo:mktStrategyConfRuleRelDOS){
                MktStrategyConfRuleDO rule = mktStrategyConfRuleMapper.selectByPrimaryKey(ruleRelDo.getMktStrategyConfRuleId());
                // 规则信息中可以得到 1分群id  2推送条目id集合  3协同渠道配置id集合  4二次协同渠道配置结果id集合
                if(rule!=null){
                      //3.2 获取推荐条目信息   997/998/999/1000格式
                    if(StringUtils.isNotBlank(rule.getProductId())){
                        String[] split = rule.getProductId().split("/");
                        for (int i = 0; i <split.length ; i++) {
                            //得到活动推荐条目
                            MktCamItem mktCamItem = mktCamItemMapper.selectByPrimaryKey(Long.valueOf(split[i]));
                            if(null!=mktCamItem){
                                OpenMktCamItem openMktCamItem = BeanUtil.create(mktCamItem, new OpenMktCamItem());
                                openMktCamItem.setId(mktCamItem.getItemId());
                                openMktCamItem.setHref("/mktCamItem/"+mktCamItem.getItemId().toString());
                                openMktCamItem.setMktActivityNbr(mktCampaignDO.getMktActivityNbr());
                                mktCamItemList.add(openMktCamItem);
                            }
                        }

                    }
                }

            }
            //返回活动推荐条目列表
            campaign.setMktCamItem(mktCamItemList);
        }

        //设置id  和href  转换时间为对应格式
        campaign.setId(Long.valueOf(id));
        campaign.setHref("/mktCampaign/" + id);

        resultMap.put("params", campaign);
        return resultMap;
    }


    /**
     * 通过活动id获取策略列表
     * @param campaignId
     */
    public static List<MktCamStrategyConfRelDO> getStrategyList(Long campaignId){
        List<MktCamStrategyConfRelDO> list=new ArrayList<>();


        return list;
    }


    /**
     * 通过策略id获取规则列表
     * @param strategyId
     * @return
     */
    public static List<MktStrategyConfRuleDO> getRuleList(Long strategyId){
        List<MktStrategyConfRuleDO> list=new ArrayList<>();


        return list;
    }


    /**
     * 通过规则获取活动推荐条目列表
     * @param ruleId
     * @return
     */
    public static List<MktCamItem> getMktCamItemList(Long ruleId){
        List<MktCamItem> list=new ArrayList<>();

        return list;
    }











    @Override
    public Map<String, Object> updateByParams(String id, Object object) {
        return null;
    }

    @Override
    public Map<String, Object> deleteById(String id) {
        return null;
    }

    @Override
    public Map<String, Object> queryListByMap(Map<String, Object> map) {
        return null;
    }

    /**
     * 转换为openapi格式
     * @param mktCampaignDO
     * @return
     */
    public OpenMktCampaign getOpenCampaign(MktCampaignDO mktCampaignDO){
        OpenMktCampaign campaign=BeanUtil.create(mktCampaignDO,new OpenMktCampaign());
        //转换时间格式  beginTime endTime  planBeginTime  planEndTime statusDate
        if(null!=mktCampaignDO.getBeginTime()){
            campaign.setBeginTime(DateUtil.getDatetime(mktCampaignDO.getBeginTime()));
        }
        if(null!=mktCampaignDO.getEndTime()){
            campaign.setEndTime(DateUtil.getDatetime(mktCampaignDO.getEndTime()));
        }
        if(null!=mktCampaignDO.getPlanBeginTime()){
            campaign.setPlanBeginTime(DateUtil.getDatetime(mktCampaignDO.getPlanBeginTime()));
        }
        if(null!=mktCampaignDO.getPlanEndTime()){
            campaign.setPlanEndTime(DateUtil.getDatetime(mktCampaignDO.getPlanEndTime()));
        }

        return campaign;
    }


    public static void main(String[] args) {
        String s="997/998/999/1000";
        String[] split = s.split("/");
        for (int i = 0; i <split.length ; i++) {
            System.out.println(split[i]);
        }

    }

    @Override
    public Map<String, Object> addByObject(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Object> singleMktCampaign = new HashMap<>();
        List<Map<String, Object>> mktCampaigns = new ArrayList<>();

        CreateMktCampaignReq createMktCampaignReq = (CreateMktCampaignReq)object;
        List<OpenMktCampaignEntity> openMktCampaignEntityList = createMktCampaignReq.getMktCampaignDetails();
        for(OpenMktCampaignEntity openMktCampaignEntity : openMktCampaignEntityList) {
            singleMktCampaign.put("mktActivityNbr",openMktCampaignEntity.getMktActivityNbr());
            singleMktCampaign.put("mktCampaignId",openMktCampaignEntity.getMktCampaignId());
            singleMktCampaign.put("mktCampaignName",openMktCampaignEntity.getMktCampaignName());
            mktCampaigns.add(singleMktCampaign);
            if(!openMktCampaignEntity.getActType().equals("ADD")) {
                resultObject.put("mktCampaigns",mktCampaigns);
                resultMap.put("resultCode","1");
                resultMap.put("resultMsg","处理失败,营服活动的数据操作类型字段的值不是ADD");
                resultMap.put("resultObject",resultObject);
                return resultMap;
            }
            //新增营服活动
            MktCampaignDO mktCampaignDO = BeanUtil.create(openMktCampaignEntity, new MktCampaignDO());
            mktCampaignDO.setMktCampaignId(null);
            mktCampaignDO.setMktCampaignCategory(openMktCampaignEntity.getManageType());
            if(openMktCampaignEntity.getMktCampaignType() == null) {
                mktCampaignDO.setMktCampaignType(StatusCode.FRAMEWORK_CAMPAIGN.getStatusCode());
            }
            if(mktCampaignDO.getExecNum() != null) {
                mktCampaignDO.setExecNum(Integer.parseInt(openMktCampaignEntity.getExecNum().toString()));
            }
            if(mktCampaignDO.getStatusCd().equals("1000")) {
                mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
            }
            if(openMktCampaignEntity.getExecInvl() == null) {
                mktCampaignDO.setExecInvl("-");
            }
            if(openMktCampaignEntity.getRemark() == null) {
                mktCampaignDO.setRemark(openMktCampaignEntity.getRegionId().toString());
            }
            mktCampaignDO.setRegionId(AreaCodeEnum.ZHEJIAGN.getRegionId());
            mktCampaignDO.setLanId(AreaCodeEnum.ZHEJIAGN.getLanId());
            mktCampaignDO.setCreateChannel(PostEnum.ADMIN.getPostCode());
            mktCampaignDO.setServiceType("1000");
            mktCampaignDO.setIsCheckRule("不校验");
            if(openMktCampaignEntity.getCreateStaff() == null) {
                mktCampaignDO.setCreateStaff(1L);
            }
            if(openMktCampaignEntity.getUpdateStaff() == null) {
                mktCampaignDO.setUpdateStaff(1L);
            }
            mktCampaignMapper.insert(mktCampaignDO);
            mktCampaignDO.setInitId(mktCampaignDO.getMktCampaignId());
            mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);

            //新增下发地市
            List<String> cityList = new ArrayList<>(Arrays.asList("570", "571", "572", "573", "574", "575", "576", "577", "578", "579", "580"));
            for(String city : cityList) {
                MktCamCityRelDO mktCamCityRelDO = new MktCamCityRelDO();
                mktCamCityRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                mktCamCityRelDO.setCityId(Long.valueOf(city));
                mktCamCityRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                mktCamCityRelDO.setUpdateDate(mktCampaignDO.getUpdateDate());
                mktCamCityRelMapper.insert(mktCamCityRelDO);
            }

            //规则
            MktStrategyConfRuleDO mktStrategyConfRuleDO = new MktStrategyConfRuleDO();
            List<MktStrategyConfRuleDO> mktStrategyConfRuleDOList = new ArrayList<>();
            int ruleNumber = 1;
            String productList = "";
            String channelList = "";

            //新增营服活动分群规则
            List<OpenMktCamGrpRulEntity> mktCamGrpRuls = openMktCampaignEntity.getMktCamGrpRuls();
            if(mktCamGrpRuls != null && mktCamGrpRuls.size() > 0) {
                ruleNumber = mktCamGrpRuls.size();
                for (OpenMktCamGrpRulEntity openMktCamGrpRulEntity : mktCamGrpRuls) {
                    if (!openMktCamGrpRulEntity.getActType().equals("ADD")) {
                        resultObject.put("mktCampaigns", mktCampaigns);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,营服活动分群规则的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    MktCamGrpRul mktCamGrpRul = BeanUtil.create(openMktCamGrpRulEntity, new MktCamGrpRul());
                    //新增目标分群
                    OpenTarGrpEntity openTarGrpEntity = openMktCamGrpRulEntity.getTarGrp();
                    if(openTarGrpEntity != null) {
                        TarGrp tarGrp = BeanUtil.create(openTarGrpEntity, new TarGrp());
                        tarGrp.setRemark("0");
                        tarGrpMapper.createTarGrp(tarGrp);
                        mktCamGrpRul.setTarGrpId(tarGrp.getTarGrpId());
                        mktStrategyConfRuleDO.setTarGrpId(tarGrp.getTarGrpId());
                        //新增目标分群条件
                        List<OpenTarGrpConditionEntity> openTarGrpConditionList = openTarGrpEntity.getTarGrpConditions();
                        if (openTarGrpConditionList != null && openTarGrpConditionList.size() > 0) {
                            for(OpenTarGrpConditionEntity openTarGrpConditionEntity : openTarGrpConditionList) {
                                TarGrpCondition tarGrpCondition = BeanUtil.create(openTarGrpConditionEntity, new TarGrpCondition());
                                tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                                if(openTarGrpConditionEntity.getLeftParam() != null) {
                                    List<Label> labelList = injectionLabelMapper.selectByTagCode(openTarGrpConditionEntity.getLeftParam());
                                    if(labelList.size() > 0) {
                                        tarGrpCondition.setLeftParam(labelList.get(0).getInjectionLabelId().toString());
                                        for (Label label : labelList) {
                                            if (label.getInjectionLabelName().contains("集团")) {
                                                tarGrpCondition.setLeftParam(label.getInjectionLabelId().toString());
                                                break;
                                            }
                                        }
                                    }
                                }
                                tarGrpCondition.setRemark("2000");
                                tarGrpConditionMapper.insert(tarGrpCondition);
                            }
                        }
                    }
                    //关联表
                    mktCamGrpRul.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamGrpRul.setMktCamGrpRulId(null);
                    mktCamGrpRulMapper.insert(mktCamGrpRul);
                }
            }

            //新增营服活动推荐条目
            List<OpenMktCamItemEntity> mktCamItems = openMktCampaignEntity.getMktCamItems();
            if(mktCamItems != null && mktCamItems.size() > 0) {
                for (int i=0; i<mktCamItems.size(); i++) {
                    OpenMktCamItemEntity openMktCamItemEntity = mktCamItems.get(i);
                    if (!openMktCamItemEntity.getActType().equals("ADD")) {
                        resultObject.put("mktCampaigns", mktCampaigns);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,营服活动推荐条目的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    MktCamItem mktCamItem = BeanUtil.create(openMktCamItemEntity, new MktCamItem());
                    mktCamItem.setMktCamItemId(null);
                    mktCamItem.setOfferCode(openMktCamItemEntity.getItemNbr());
                    if(openMktCamItemEntity.getItemType().equals("1000")) {
                        List<Offer> offerList = offerMapper.selectByCode(openMktCamItemEntity.getItemNbr());
                        if(offerList.size() > 0) {
                            mktCamItem.setItemId(offerList.get(0).getOfferId().longValue());
                            mktCamItem.setOfferCode(openMktCamItemEntity.getItemNbr());
                            mktCamItem.setOfferName(offerList.get(0).getOfferName());
                        }
                    }else if(openMktCamItemEntity.getItemType().equals("3000")) {
                        MktResource mktResource = mktResourceMapper.selectByMktResNbr(openMktCamItemEntity.getItemNbr());
                        if(mktResource != null) {
                            mktCamItem.setItemId(mktResource.getMktResId());
                            mktCamItem.setOfferCode(openMktCamItemEntity.getItemNbr());
                            mktCamItem.setOfferName(mktResource.getMktResName());
                        }
                    }
                    mktCamItem.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamItemMapper.insert(mktCamItem);
                    if(i == 0) {
                        productList = productList + mktCamItem.getMktCamItemId();
                    }else {
                        productList = productList + "/" + mktCamItem.getMktCamItemId();
                    }
                }
            }

            //新增营服活动渠道推送配置
            List<OpenMktCamChlConfEntity> mktCamChlConfs = openMktCampaignEntity.getMktCamChlConfs();
            if(mktCamChlConfs != null && mktCamChlConfs.size() > 0) {
                for (int i=0; i<mktCamChlConfs.size(); i++) {
                    OpenMktCamChlConfEntity openMktCamChlConfEntity = mktCamChlConfs.get(i);
                    if (!openMktCamChlConfEntity.getActType().equals("ADD")) {
                        resultObject.put("mktCampaigns", mktCampaigns);
                        resultMap.put("resultCode", "1");
                        resultMap.put("resultMsg", "处理失败,营服活动渠道推送配置的数据操作类型字段的值不是ADD");
                        resultMap.put("resultObject", resultObject);
                        return resultMap;
                    }
                    MktCamChlConfDO mktCamChlConfDO = BeanUtil.create(openMktCamChlConfEntity, new MktCamChlConfDO());
                    mktCamChlConfDO.setEvtContactConfId(null);
                    mktCamChlConfDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    Channel channel = contactChannelMapper.selectByCode(mktCamChlConfDO.getContactChlId().toString());
                    mktCamChlConfDO.setContactChlId(channel.getContactChlId());
                    mktCamChlConfDO.setEvtContactConfName(channel.getContactChlName());
                    mktCamChlConfDO.setPushType("1000");
                    mktCamChlConfMapper.insert(mktCamChlConfDO);
                    if(i == 0) {
                        channelList = channelList + mktCamChlConfDO.getEvtContactConfId();
                    }else {
                        channelList = channelList + "/" + mktCamChlConfDO.getEvtContactConfId();
                    }
                    //新增营服活动脚本
                    List<OpenMktCamScriptEntity> mktCamScripts = openMktCamChlConfEntity.getMktCamScripts();
                    if(mktCamScripts != null && mktCamScripts.size() > 0) {
                        for (OpenMktCamScriptEntity openMktCamScriptEntity : mktCamScripts) {
                            if (!openMktCamScriptEntity.getActType().equals("ADD")) {
                                resultObject.put("mktCampaigns", mktCampaigns);
                                resultMap.put("resultCode", "1");
                                resultMap.put("resultMsg", "处理失败,营服活动脚本的数据操作类型字段的值不是ADD");
                                resultMap.put("resultObject", resultObject);
                                return resultMap;
                            }
                            CamScript camScript = BeanUtil.create(openMktCamScriptEntity, new CamScript());
                            camScript.setMktCampaignScptId(null);
                            camScript.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                            camScript.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                            mktCamScriptMapper.insert(camScript);
                        }
                    }
                    //新增调查问卷
                    List<OpenMktCamQuestEntity> mktCamQuests = openMktCamChlConfEntity.getMktCamQuests();
                    //新增营服活动执行渠道配置属性
                    List<OpenMktCamChlConfAttrEntity> mktCamChlConfAttrs = openMktCamChlConfEntity.getMktCamChlConfAttrs();
                    if(mktCamChlConfAttrs != null && mktCamChlConfAttrs.size() > 0) {
                        for (OpenMktCamChlConfAttrEntity openMktCamChlConfAttrEntity : mktCamChlConfAttrs) {
                            MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(openMktCamChlConfAttrEntity, new MktCamChlConfAttrDO());
                            mktCamChlConfAttrDO.setContactChlAttrRstrId(null);
                            mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                            mktCamChlConfAttrDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                            mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                        }
                    }
                }
            }

            //新增营服活动执行算法规则关联
            List<OpenMktCamRecomCalcRelEntity> mktCamRecomCalcRels = openMktCampaignEntity.getMktCamRecomCalcRels();
            if(mktCamRecomCalcRels != null && mktCamRecomCalcRels.size() > 0) {
                for(OpenMktCamRecomCalcRelEntity openMktCamRecomCalcRelEntity : mktCamRecomCalcRels) {
                    MktCamRecomCalcRelDO mktCamRecomCalcRelDO = new MktCamRecomCalcRelDO();
                    //新增算法规则
                    OpenMktAlgorithmsEntity openMktAlgorithmsEntity = openMktCamRecomCalcRelEntity.getMktAlgorithms();
                    if(openMktAlgorithmsEntity != null) {
                        MktAlgorithms mktAlgorithms = BeanUtil.create(openMktAlgorithmsEntity, new MktAlgorithms());
                        mktAlgorithms.setAlgoId(null);
                        mktAlgorithms.setStatusDate(mktCampaignDO.getCreateDate());
                        mktAlgorithms.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktAlgorithms.setCreateDate(mktCampaignDO.getCreateDate());
                        mktAlgorithms.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktAlgorithms.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktAlgorithmsMapper.saveMktAlgorithms(mktAlgorithms);
                        mktCamRecomCalcRelDO.setAlgoId(mktAlgorithms.getAlgoId());
                    }
                    //新增cpc算法规则
                    OpenMktCpcAlgorithmsRulEntity openMktCpcAlgorithmsRulEntity = openMktCamRecomCalcRelEntity.getMktCpcAlgorithmsRul();
                    if(openMktCpcAlgorithmsRulEntity != null) {
                        MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = BeanUtil.create(openMktCpcAlgorithmsRulEntity, new MktCpcAlgorithmsRulDO());
                        mktCpcAlgorithmsRulDO.setAlgorithmsRulId(null);
                        mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
                        mktCamRecomCalcRelDO.setAlgorithmsRulId(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
                    }
                    //关联表
                    mktCamRecomCalcRelDO.setEvtRecomCalcRelId(null);
                    if(mktCamRecomCalcRelDO.getAlgoId() == null) {
                        mktCamRecomCalcRelDO.setAlgoId(1L);
                    }
                    mktCamRecomCalcRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamRecomCalcRelDO.setStatusDate(mktCampaignDO.getCreateDate());
                    if(mktCampaignDO.getCreateStaff() != null) {
                        mktCamRecomCalcRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                    }
                    mktCamRecomCalcRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                    if(mktCampaignDO.getCreateStaff() != null) {
                        mktCamRecomCalcRelDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    }
                    mktCamRecomCalcRelDO.setUpdateDate(mktCampaignDO.getCreateDate());
                    mktCamRecomCalcRelMapper.insert(mktCamRecomCalcRelDO);
                }
            }

            //新增营服活动关联事件
            List<OpenMktCamEvtRelEntity> mktCamEvtRels = openMktCampaignEntity.getMktCamEvtRels();
            if(mktCamEvtRels != null && mktCamEvtRels.size() > 0) {
                for (OpenMktCamEvtRelEntity openMktCamEvtRelEntity : mktCamEvtRels) {
                    MktCamEvtRelDO mktCamEvtRelDO = BeanUtil.create(openMktCamEvtRelEntity, new MktCamEvtRelDO());
                    mktCamEvtRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    try {
                        mktCamEvtRelDO.setEventId(Long.valueOf(openMktCamEvtRelEntity.getEventNbr()));
                    }catch (Exception e) {
                        logger.info("事件编码: " + openMktCamEvtRelEntity.getEventNbr());
                        e.printStackTrace();
                    }
                    ContactEvt contactEvt = new ContactEvt();
                    if(openMktCamEvtRelEntity.getEventNbr() != null) {
                        contactEvt = contactEvtMapper.getEventByEventNbr(openMktCamEvtRelEntity.getEventNbr());
                    }
                    if(contactEvt != null) {
                        mktCamEvtRelDO.setEventId(contactEvt.getContactEvtId());
                    }
                    mktCamEvtRelDO.setCampaignSeq(0);
                    mktCamEvtRelDO.setLevelConfig(0);
                    mktCamEvtRelMapper.insert(mktCamEvtRelDO);
                }
            }

            //新增策略
            MktStrategyConfDO mktStrategyConfDO = new MktStrategyConfDO();
            mktStrategyConfDO.setMktStrategyConfName("策略1");
            mktStrategyConfDO.setChannelsId("");
            List<SysArea> sysAreas = sysAreaMapper.selectAll();
            String AreaId = sysAreas.get(0).getAreaId().toString();
            for(int i=1; i<sysAreas.size(); i++) {
                AreaId = AreaId + "/" + sysAreas.get(i).getAreaId().toString();
            }
            mktStrategyConfDO.setAreaId(AreaId);
            mktStrategyConfDO.setCreateDate(mktCampaignDO.getCreateDate());
            mktStrategyConfDO.setUpdateDate(mktCampaignDO.getUpdateDate());
            mktStrategyConfMapper.insert(mktStrategyConfDO);
            mktStrategyConfDO.setInitId(mktStrategyConfDO.getMktStrategyConfId());
            mktStrategyConfMapper.updateByPrimaryKey(mktStrategyConfDO);

            //新增策略与活动的关联
            MktCamStrategyConfRelDO mktCamStrategyConfRelDO = new MktCamStrategyConfRelDO();
            mktCamStrategyConfRelDO.setStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
            mktCamStrategyConfRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
            mktCamStrategyConfRelDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            mktCamStrategyConfRelDO.setStatusDate(mktCampaignDO.getStatusDate());
            mktCamStrategyConfRelDO.setCreateDate(mktCampaignDO.getCreateDate());
            mktCamStrategyConfRelDO.setUpdateDate(mktCampaignDO.getUpdateDate());
            mktCamStrategyConfRelMapper.insert(mktCamStrategyConfRelDO);

            //新增规则
            for(int i=1; i<=ruleNumber; i++) {
                mktStrategyConfRuleDO.setMktStrategyConfRuleId(null);
                mktStrategyConfRuleDO.setMktStrategyConfRuleName("规则" + i);
                mktStrategyConfRuleDO.setProductId(productList);
                mktStrategyConfRuleDO.setEvtContactConfId(channelList);
                mktStrategyConfRuleDO.setCreateDate(mktCampaignDO.getCreateDate());
                mktStrategyConfRuleDO.setUpdateDate(mktCampaignDO.getUpdateDate());
                mktStrategyConfRuleMapper.insert(mktStrategyConfRuleDO);
                mktStrategyConfRuleDO.setInitId(mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                mktStrategyConfRuleMapper.updateByPrimaryKey(mktStrategyConfRuleDO);
                mktStrategyConfRuleDOList.add(mktStrategyConfRuleDO);
                //新增策略与规则的关联
                MktStrategyConfRuleRelDO mktStrategyConfRuleRelDO = new MktStrategyConfRuleRelDO();
                mktStrategyConfRuleRelDO.setMktStrategyConfId(mktStrategyConfDO.getMktStrategyConfId());
                mktStrategyConfRuleRelDO.setMktStrategyConfRuleId(mktStrategyConfRuleDO.getMktStrategyConfRuleId());
                mktStrategyConfRuleRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                mktStrategyConfRuleRelDO.setUpdateDate(mktCampaignDO.getUpdateDate());
                mktStrategyConfRuleRelMapper.insert(mktStrategyConfRuleRelDO);
            }

            //新增营服活动校验规则
            List<OpenMktCamCheckruleEntity> mktCamCheckrules = openMktCampaignEntity.getMktCamCheckrules();
            if(mktCamCheckrules != null && mktCamCheckrules.size() > 0) {
                for(OpenMktCamCheckruleEntity openMktCamCheckruleEntity : mktCamCheckrules) {
                    //新增过滤规则
                    FilterRule filterRule = new FilterRule();
                    List<SysParams> sysParamsList = sysParamsMapper.selectAll(null,"OPENAPI_FILTER_RULE_MAPPING");
                    JSONObject typeList = JSON.parseObject(sysParamsList.get(0).getParamValue());
                    String filterType = typeList.getString(openMktCamCheckruleEntity.getBusiRuleId().toString());
                    filterRule.setFilterType(filterType);
                    SysParams sysParams = sysParamsMapper.findParamsByValue("FILTER_RULE_TYPE",filterType);
                    filterRule.setRuleName("集团下发-" + sysParams.getParamName() + "-" + mktCampaignDO.getMktActivityNbr());
                    filterRule.setStatusCd(openMktCamCheckruleEntity.getStatusCd());
                    filterRule.setStatusDate(openMktCamCheckruleEntity.getStatusDate());
                    filterRule.setCreateStaff(openMktCamCheckruleEntity.getCreateStaff());
                    filterRule.setCreateDate(openMktCamCheckruleEntity.getCreateDate());
                    filterRule.setUpdateStaff(openMktCamCheckruleEntity.getUpdateStaff());
                    filterRule.setUpdateDate(openMktCamCheckruleEntity.getUpdateDate());
                    filterRuleMapper.createFilterRule(filterRule);
                    //新增过滤规则与活动关系
                    MktStrategyFilterRuleRelDO mktStrategyFilterRuleRelDO = new MktStrategyFilterRuleRelDO();
                    mktStrategyFilterRuleRelDO.setStrategyId(mktCampaignDO.getMktCampaignId());
                    mktStrategyFilterRuleRelDO.setRuleId(filterRule.getRuleId());
                    mktStrategyFilterRuleRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktStrategyFilterRuleRelDO.setCreateDate(mktCampaignDO.getCreateDate());
                    mktStrategyFilterRuleRelDO.setUpdateStaff(mktCampaignDO.getUpdateStaff());
                    mktStrategyFilterRuleRelDO.setUpdateDate(mktCampaignDO.getUpdateDate());
                    mktStrategyFilterRuleRelMapper.insert(mktStrategyFilterRuleRelDO);
                }
            }

            //新增营服活动渠道执行策略
            List<OpenMktCamStrategyRelEntity> mktCamStrategyRels = openMktCampaignEntity.getMktCamStrategyRels();
            if(mktCamStrategyRels != null && mktCamStrategyRels.size() > 0) {
                for(OpenMktCamStrategyRelEntity openMktCamStrategyRelEntity : mktCamStrategyRels) {
                    //新增营销服务策略
                    OpenMktStrategyEntity openMktStrategyEntity = openMktCamStrategyRelEntity.getMktStrategy();
                    MktStrategy mktStrategy = BeanUtil.create(openMktStrategyEntity, new MktStrategy());
                    mktStrategy.setStrategyId(null);
                    mktStrategy.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                    mktStrategy.setStatusDate(mktCampaignDO.getStatusDate());
                    mktStrategy.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktStrategy.setCreateDate(mktCampaignDO.getCreateDate());
                    mktStrategy.setUpdateStaff(mktCampaignDO.getUpdateStaff());
                    mktStrategy.setUpdateDate(mktCampaignDO.getUpdateDate());
                    mktStrategyMapper.insert(mktStrategy);
                    MktCamStrategyRel mktCamStrategyRel = new MktCamStrategyRel();
                    mktCamStrategyRel.setCampStrRelId(null);
                    mktCamStrategyRel.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                    mktCamStrategyRel.setStrategyId(mktStrategy.getStrategyId());
                    mktCamStrategyRel.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                    mktCamStrategyRel.setStatusDate(mktCampaignDO.getStatusDate());
                    mktCamStrategyRel.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktCamStrategyRel.setCreateDate(mktCampaignDO.getCreateDate());
                    mktCamStrategyRel.setUpdateStaff(mktCampaignDO.getUpdateStaff());
                    mktCamStrategyRel.setUpdateDate(mktCampaignDO.getUpdateDate());
                    mktCamStrategyRelMapper.insert(mktCamStrategyRel);
                }
            } else {
                for(MktStrategyConfRuleDO mktStrategyConfRule: mktStrategyConfRuleDOList) {
                    MktStrategy mktStrategy = new MktStrategy();
                    mktStrategy.setStrategyId(mktStrategyConfRule.getMktStrategyConfRuleId());
                    mktStrategy.setStrategyName(mktStrategyConfRule.getMktStrategyConfRuleName());
                    if(StatusCode.SERVICE_CAMPAIGN.getStatusCode().equals(mktCampaignDO.getMktCampaignType())){
                        mktStrategy.setStrategyType(StatusCode.CARE_STRATEGY.getStatusCode());
                    } else{
                        mktStrategy.setStrategyType(StatusCode.SALES_STRATEGY.getStatusCode());
                    }
                    mktStrategy.setStrategyDesc(mktStrategyConfRule.getMktStrategyConfRuleName());

                    String confs = "";
                    String resultConfs = "";
                    if(mktStrategyConfRule.getEvtContactConfId() != null) {
                        String[] confList = mktStrategyConfRule.getEvtContactConfId().split("/");
                        if (confList.length > 0) {
                            confs = confList[0];
                            for (int i = 1; i < confList.length; i++) {
                                confs = confs + "||" + confList[i];
                            }
                        }
                    }
                    mktStrategy.setRuleExpression("("+ confs + ")&&(" + resultConfs + ")");
                    mktStrategy.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                    mktStrategy.setStatusDate(mktCampaignDO.getStatusDate());
                    mktStrategy.setCreateStaff(mktCampaignDO.getCreateStaff());
                    mktStrategy.setCreateDate(mktCampaignDO.getCreateDate());
                    mktStrategy.setUpdateStaff(mktCampaignDO.getCreateStaff());
                    mktStrategy.setUpdateDate(mktCampaignDO.getUpdateDate());
                    mktStrategyMapper.insert(mktStrategy);
                }
            }

            //新增营服活动关系
            List<OpenMktCampaignRelEntity> mktCampaignRels = openMktCampaignEntity.getMktCampaignRels();

            //新增事件触发活动规则
            List<OpenEvtTrigCamRulEntity> evtTrigCamRuls = openMktCampaignEntity.getEvtTrigCamRuls();

        }
        resultObject.put("mktCampaigns",mktCampaigns);
        resultMap.put("resultCode","0");
        resultMap.put("resultMsg","处理成功");
        resultMap.put("resultObject",resultObject);
        return resultMap;
    }

    @Override
    public Map<String,Object> updateMktCampaign(Object object) {
        Map<String, Object> resultMap = new HashMap<>();
        Map<String, Object> resultObject = new HashMap<>();
        Map<String, Object> singleMktCampaign = new HashMap<>();
        List<Map<String, Object>> mktCampaigns = new ArrayList<>();

        ModMktCampaignReq modMktCampaignReq = (ModMktCampaignReq)object;
        List<OpenMktCampaignEntity> openMktCampaignEntityList = modMktCampaignReq.getMktCampaignDetails();
        for(OpenMktCampaignEntity openMktCampaignEntity : openMktCampaignEntityList) {
            singleMktCampaign.put("mktActivityNbr",openMktCampaignEntity.getMktActivityNbr());
            singleMktCampaign.put("mktCampaignId",openMktCampaignEntity.getMktCampaignId());
            singleMktCampaign.put("mktCampaignName",openMktCampaignEntity.getMktCampaignName());
            mktCampaigns.add(singleMktCampaign);

            //修改营服活动
            MktCampaignDO mktCampaignDO = BeanUtil.create(openMktCampaignEntity, new MktCampaignDO());
            if(openMktCampaignEntity.getActType().equals("MOD")) {
                MktCampaignDO mktCampaign = mktCampaignMapper.selectByPrimaryKey(openMktCampaignEntity.getMktCampaignId());
                if (mktCampaign == null) {
                    resultObject.put("mktCampaigns", mktCampaigns);
                    resultMap.put("resultCode", "1");
                    resultMap.put("resultMsg", "处理失败,对应的营服活动不存在");
                    resultMap.put("resultObject", resultObject);
                    return resultMap;
                }
                mktCampaignDO.setMktCampaignCategory(openMktCampaignEntity.getManageType());
                mktCampaignDO.setLanId(AreaCodeEnum.getLandIdByRegionId(mktCampaignDO.getRegionId()));
                mktCampaignDO.setStatusCd(StatusCode.STATUS_CODE_DRAFT.getStatusCode());
                mktCampaignDO.setStatusDate(mktCampaign.getStatusDate());
                mktCampaignDO.setCreateDate(mktCampaign.getCreateDate());
                mktCampaignDO.setCreateStaff(openMktCampaignEntity.getCreateStaff());
                mktCampaignMapper.updateByPrimaryKey(mktCampaignDO);
            }
            //修改营服活动分群规则
            List<OpenMktCamGrpRulEntity> mktCamGrpRuls = openMktCampaignEntity.getMktCamGrpRuls();
            if(mktCamGrpRuls.size() > 0) {
                for (OpenMktCamGrpRulEntity openMktCamGrpRulEntity : mktCamGrpRuls) {
                    MktCamGrpRul mktCamGrpRul = BeanUtil.create(openMktCamGrpRulEntity, new MktCamGrpRul());
                    mktCamGrpRul.setTarGrpId(openMktCamGrpRulEntity.getTarGrpId());
                    //修改目标分群
                    OpenTarGrpEntity openTarGrpEntity = openMktCamGrpRulEntity.getTarGrp();
                    if(openTarGrpEntity != null) {
                        List<OpenTarGrpConditionEntity> openTarGrpConditionList = openTarGrpEntity.getTarGrpConditions();
                        TarGrp tarGrp = BeanUtil.create(openTarGrpEntity, new TarGrp());
                        for(OpenTarGrpConditionEntity openTarGrpConditionEntity : openTarGrpConditionList) {
                            if (openTarGrpEntity.getActType().equals("ADD")) {
                                tarGrpMapper.createTarGrp(tarGrp);
                                mktCamGrpRul.setTarGrpId(tarGrp.getTarGrpId());
                                //修改目标分群条件
                                if (openTarGrpConditionEntity != null) {
                                    TarGrpCondition tarGrpCondition = BeanUtil.create(openTarGrpConditionEntity, new TarGrpCondition());
                                    tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                                    tarGrpConditionMapper.insert(tarGrpCondition);
                                }
                            } else if (openTarGrpEntity.getActType().equals("MOD")) {
                                TarGrp tarGroup = tarGrpMapper.selectByPrimaryKey(openMktCamGrpRulEntity.getTarGrpId());
                                if (tarGroup == null) {
                                    resultObject.put("mktCampaigns", mktCampaigns);
                                    resultMap.put("resultCode", "1");
                                    resultMap.put("resultMsg", "处理失败,对应的目标分群不存在");
                                    resultMap.put("resultObject", resultObject);
                                    return resultMap;
                                }
                                tarGrpMapper.modTarGrp(tarGrp);
                                if (openTarGrpConditionEntity != null) {
                                    TarGrpCondition tarGrpCondition = BeanUtil.create(openTarGrpConditionEntity, new TarGrpCondition());
                                    tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                                    tarGrpConditionMapper.updateByPrimaryKey(tarGrpCondition);
                                }
                            } else if (openTarGrpEntity.getActType().equals("DEL")) {
                                TarGrp tarGroup = tarGrpMapper.selectByPrimaryKey(openMktCamGrpRulEntity.getTarGrpId());
                                if (tarGroup != null) {
                                    tarGrpMapper.deleteByPrimaryKey(openMktCamGrpRulEntity.getTarGrpId());
                                }
                            }
                        }
                    }
                    if(openMktCamGrpRulEntity.getActType().equals("ADD")) {
                        mktCamGrpRul.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamGrpRul.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                        mktCamGrpRul.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCamGrpRul.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCamGrpRul.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCamGrpRul.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCamGrpRul.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCamGrpRulMapper.insert(mktCamGrpRul);
                    }else if(openMktCamGrpRulEntity.getActType().equals("MOD")) {
                        MktCamGrpRul mktCampaignGrpRul = mktCamGrpRulMapper.selectByTarGrpId(openMktCamGrpRulEntity.getTarGrpId());
                        if(mktCampaignGrpRul == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,活动与分群关联关系不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        mktCamGrpRul.setMktCamGrpRulId(mktCampaignGrpRul.getMktCamGrpRulId());
                        mktCamGrpRulMapper.updateByPrimaryKey(mktCamGrpRul);
                    }else if(openMktCamGrpRulEntity.getActType().equals("DEL")) {
                        mktCamGrpRulMapper.deleteByTarGrpId(openMktCamGrpRulEntity.getMktCamGrpRulId());
                    }
                }
            }

            //修改营服活动推荐条目
            List<OpenMktCamItemEntity> mktCamItems = openMktCampaignEntity.getMktCamItems();
            if(mktCamItems.size() > 0) {
                for (OpenMktCamItemEntity openMktCamItemEntity : mktCamItems) {
                    MktCamItem mktCamItem = BeanUtil.create(openMktCamItemEntity, new MktCamItem());
                    if(openMktCamItemEntity.getActType().equals("ADD")) {
                        mktCamItem.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamItem.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                        mktCamItem.setStatusDate(mktCampaignDO.getCreateDate());
                        mktCamItem.setCreateStaff(mktCampaignDO.getCreateStaff());
                        mktCamItem.setCreateDate(mktCampaignDO.getCreateDate());
                        mktCamItem.setUpdateStaff(mktCampaignDO.getCreateStaff());
                        mktCamItem.setUpdateDate(mktCampaignDO.getCreateDate());
                        mktCamItemMapper.insert(mktCamItem);
                    }else if(openMktCamItemEntity.getActType().equals("MOD")) {
                        MktCamItem mktCampaignItem = mktCamItemMapper.selectByPrimaryKey(openMktCamItemEntity.getMktCamItemId());
                        if(mktCampaignItem == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应的活动推荐条目不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        mktCamItem.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamItemMapper.updateByPrimaryKey(mktCamItem);
                    }else if(openMktCamItemEntity.getActType().equals("DEL")) {
                        mktCamItemMapper.deleteByPrimaryKey(openMktCamItemEntity.getMktCamItemId());
                    }
                }
            }

            //修改营服活动渠道推送配置
            List<OpenMktCamChlConfEntity> mktCamChlConfDetails = openMktCampaignEntity.getMktCamChlConfs();
            if(mktCamChlConfDetails.size() > 0) {
                for (OpenMktCamChlConfEntity openMktCamChlConfEntity : mktCamChlConfDetails) {
                    List<OpenMktCamChlConfAttrEntity> mktCamChlConfAttrs = openMktCamChlConfEntity.getMktCamChlConfAttrs();
                    if(openMktCamChlConfEntity.getActType().equals("ADD")) {
                        MktCamChlConfDO mktCamChlConfDO = BeanUtil.create(openMktCamChlConfEntity, new MktCamChlConfDO());
                        mktCamChlConfDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamChlConfMapper.insert(mktCamChlConfDO);
                        //新增调查问卷
                        List<OpenMktCamQuestEntity> mktCamQuests = openMktCamChlConfEntity.getMktCamQuests();
                        //新增营服活动执行渠道配置属性
                        if(mktCamChlConfAttrs.size() > 0 ) {
                            for (OpenMktCamChlConfAttrEntity openMktCamChlConfAttrEntity : mktCamChlConfAttrs) {
                                MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(openMktCamChlConfAttrEntity, new MktCamChlConfAttrDO());
                                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                                mktCamChlConfAttrDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                                mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                            }
                        }
                    }else if(openMktCamChlConfEntity.getActType().equals("MOD")) {
                        MktCamChlConfDO mktCamChlConf = mktCamChlConfMapper.selectByPrimaryKey(openMktCamChlConfEntity.getEvtContactConfId());
                        if(mktCamChlConf == null) {
                            resultObject.put("mktCampaigns", mktCampaigns);
                            resultMap.put("resultCode", "1");
                            resultMap.put("resultMsg", "处理失败,对应的活动渠道推送配置不存在");
                            resultMap.put("resultObject", resultObject);
                            return resultMap;
                        }
                        MktCamChlConfDO mktCamChlConfDO = BeanUtil.create(openMktCamChlConfEntity, new MktCamChlConfDO());
                        mktCamChlConfDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                        mktCamChlConfMapper.updateByPrimaryKey(mktCamChlConfDO);
                        //修改调查问卷
                        List<OpenMktCamQuestEntity> mktCamQuests = openMktCamChlConfEntity.getMktCamQuests();
                        //修改营服活动执行渠道配置属性
                        if(mktCamChlConfAttrs.size() > 0 ) {
                            mktCamChlConfAttrMapper.deleteByEvtContactConfId(openMktCamChlConfEntity.getEvtContactConfId());
                            for (OpenMktCamChlConfAttrEntity openMktCamChlConfAttrEntity : mktCamChlConfAttrs) {
                                MktCamChlConfAttrDO mktCamChlConfAttrDO = BeanUtil.create(openMktCamChlConfAttrEntity, new MktCamChlConfAttrDO());
                                mktCamChlConfAttrDO.setEvtContactConfId(mktCamChlConfDO.getEvtContactConfId());
                                mktCamChlConfAttrDO.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                                mktCamChlConfAttrMapper.insert(mktCamChlConfAttrDO);
                            }
                        }
                    }else if(openMktCamChlConfEntity.getActType().equals("DEL")) {
                        mktCamChlConfMapper.deleteByPrimaryKey(openMktCamChlConfEntity.getEvtContactConfId());
                        mktCamChlConfAttrMapper.deleteByEvtContactConfId(openMktCamChlConfEntity.getEvtContactConfId());
                    }
                    //修改营服活动脚本
                    List<OpenMktCamScriptEntity> mktCamScripts = openMktCamChlConfEntity.getMktCamScripts();
                    if(mktCamScripts.size() > 0) {
                        for(OpenMktCamScriptEntity openMktCamScriptEntity : mktCamScripts) {
                            CamScript camScript = BeanUtil.create(openMktCamScriptEntity, new CamScript());
                            if(openMktCamScriptEntity.getActType().equals("ADD")) {
                                camScript.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                                camScript.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
                                camScript.setCreateStaff(mktCampaignDO.getCreateStaff());
                                camScript.setCreateDate(mktCampaignDO.getCreateDate());
                                camScript.setUpdateStaff(mktCampaignDO.getCreateStaff());
                                camScript.setUpdateDate(mktCampaignDO.getCreateDate());
                                mktCamScriptMapper.insert(camScript);
                            }else if(openMktCamScriptEntity.getActType().equals("MOD")) {
                                CamScript campaignScript = mktCamScriptMapper.selectByPrimaryKey(openMktCamScriptEntity.getMktCampaignScptId());
                                if(campaignScript == null) {
                                    resultObject.put("mktCampaigns", mktCampaigns);
                                    resultMap.put("resultCode", "1");
                                    resultMap.put("resultMsg", "处理失败,对应的营服活动脚本不存在");
                                    resultMap.put("resultObject", resultObject);
                                    return resultMap;
                                }
                                camScript.setMktCampaignId(mktCampaignDO.getMktCampaignId());
                                mktCamScriptMapper.updateByPrimaryKey(camScript);
                            }else if(openMktCamScriptEntity.getActType().equals("DEL")) {
                                mktCamScriptMapper.deleteByPrimaryKey(openMktCamScriptEntity.getMktCampaignScptId());
                            }
                        }
                    }
                }
            }

            //修改营服活动执行算法规则关联
//            List<OpenMktCpcAlgorithmsRulEntity> mktCpcAlgorithmsRulDetails = openMktCampaignEntity.getMktCpcAlgorithmsRulDetails();
//            if(mktCpcAlgorithmsRulDetails.size() > 0) {
//                for (OpenMktCpcAlgorithmsRulEntity openMktCpcAlgorithmsRulEntity : mktCpcAlgorithmsRulDetails) {
//                    MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO = BeanUtil.create(openMktCpcAlgorithmsRulEntity, new MktCpcAlgorithmsRulDO());
//                    if (openMktCpcAlgorithmsRulEntity.getActType().equals("ADD")) {
//                        mktCpcAlgorithmsRulDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//                        mktCpcAlgorithmsRulDO.setStatusDate(mktCampaignDO.getCreateDate());
//                        mktCpcAlgorithmsRulDO.setCreateStaff(mktCampaignDO.getCreateStaff());
//                        mktCpcAlgorithmsRulDO.setCreateDate(mktCampaignDO.getCreateDate());
//                        mktCpcAlgorithmsRulDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
//                        mktCpcAlgorithmsRulDO.setUpdateDate(mktCampaignDO.getCreateDate());
//                        mktCpcAlgorithmsRulMapper.insert(mktCpcAlgorithmsRulDO);
//                        //关联表
//                        MktCamRecomCalcRelDO mktCamRecomCalcRelDO = new MktCamRecomCalcRelDO();
//                        mktCamRecomCalcRelDO.setAlgorithmsRulId(mktCpcAlgorithmsRulDO.getAlgorithmsRulId());
//                        mktCamRecomCalcRelDO.setAlgoId(1L);
//                        mktCamRecomCalcRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
//                        mktCamRecomCalcRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//                        mktCamRecomCalcRelDO.setStatusDate(mktCampaignDO.getCreateDate());
//                        mktCamRecomCalcRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
//                        mktCamRecomCalcRelDO.setCreateDate(mktCampaignDO.getCreateDate());
//                        mktCamRecomCalcRelDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
//                        mktCamRecomCalcRelDO.setUpdateDate(mktCampaignDO.getCreateDate());
//                        mktCamRecomCalcRelMapper.insert(mktCamRecomCalcRelDO);
//                    } else if (openMktCpcAlgorithmsRulEntity.getActType().equals("MOD")) {
//                        //修改cpc算法规则
//                        MktCpcAlgorithmsRulDO mktCpcAlgorithmsRul = mktCpcAlgorithmsRulMapper.selectByPrimaryKey(openMktCpcAlgorithmsRulEntity.getAlgorithmsRulId());
//                        if (mktCpcAlgorithmsRul == null) {
//                            resultObject.put("mktCampaigns", mktCampaigns);
//                            resultMap.put("resultCode", "1");
//                            resultMap.put("resultMsg", "处理失败,对应的cpc算法规则不存在");
//                            resultMap.put("resultObject", resultObject);
//                            return resultMap;
//                        }
//                        mktCpcAlgorithmsRulMapper.updateByPrimaryKey(mktCpcAlgorithmsRulDO);
//                    } else if (openMktCpcAlgorithmsRulEntity.getActType().equals("DEL")) {
//                        mktCpcAlgorithmsRulMapper.deleteByPrimaryKey(openMktCpcAlgorithmsRulEntity.getAlgorithmsRulId());
//                    }
//                }
//            }

            //修改营服活动关联事件
//            List<OpenEvent> mktCampaignEvts = openMktCampaignEntity.getMktCampaignEvts();
//            if(mktCampaignEvts.size() > 0) {
//                for(OpenEvent openEvent : mktCampaignEvts) {
//                    if(openEvent.getActType().equals("ADD")) {
//                        MktCamEvtRelDO mktCamEvtRelDO = new MktCamEvtRelDO();
//                        mktCamEvtRelDO.setMktCampaignId(mktCampaignDO.getMktCampaignId());
//                        mktCamEvtRelDO.setEventId(openEvent.getEventId());
//                        mktCamEvtRelDO.setCampaignSeq(0);
//                        mktCamEvtRelDO.setLevelConfig(0);
//                        mktCamEvtRelDO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//                        mktCamEvtRelDO.setStatusDate(mktCampaignDO.getCreateDate());
//                        mktCamEvtRelDO.setCreateStaff(mktCampaignDO.getCreateStaff());
//                        mktCamEvtRelDO.setCreateDate(mktCampaignDO.getCreateDate());
//                        mktCamEvtRelDO.setUpdateStaff(mktCampaignDO.getCreateStaff());
//                        mktCamEvtRelDO.setUpdateDate(mktCampaignDO.getCreateDate());
//                        mktCamEvtRelMapper.insert(mktCamEvtRelDO);
//                    }else if(openEvent.getActType().equals("DEL")) {
//                        MktCamEvtRelDO mktCamEvtRelDO = mktCamEvtRelMapper.findByCampaignIdAndEvtId(mktCampaignDO.getMktCampaignId(),openEvent.getEventId());
//                        if(mktCamEvtRelDO != null) {
//                            mktCamEvtRelMapper.deleteByPrimaryKey(mktCamEvtRelDO.getMktCampEvtRelId());
//                        }
//                    }
//                }
//            }

            //修改营服活动渠道执行策略
//            List<OpenMktStrategyEntity> mktCampaignStrategyDetails = openMktCampaignEntity.getMktCampaignStrategyDetails();
//            if(mktCampaignStrategyDetails.size() > 0) {
//                for (OpenMktStrategyEntity openMktStrategyEntity : mktCampaignStrategyDetails) {
//                    MktStrategy mktStrategy = BeanUtil.create(openMktStrategyEntity, new MktStrategy());
//                    if(openMktStrategyEntity.getActType().equals("ADD")) {
//                        //新增营销服务策略
//                        mktStrategy.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//                        mktStrategy.setStatusDate(mktCampaignDO.getCreateDate());
//                        mktStrategy.setCreateStaff(mktCampaignDO.getCreateStaff());
//                        mktStrategy.setCreateDate(mktCampaignDO.getCreateDate());
//                        mktStrategy.setUpdateStaff(mktCampaignDO.getCreateStaff());
//                        mktStrategy.setUpdateDate(mktCampaignDO.getCreateDate());
//                        mktStrategyMapper.insert(mktStrategy);
//                        MktCamStrategyRel mktCamStrategyRel = new MktCamStrategyRel();
//                        mktCamStrategyRel.setMktCampaignId(mktCampaignDO.getMktCampaignId());
//                        mktCamStrategyRel.setStrategyId(mktStrategy.getStrategyId());
//                        mktCamStrategyRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
//                        mktCamStrategyRel.setStatusDate(mktCampaignDO.getCreateDate());
//                        mktCamStrategyRel.setCreateStaff(mktCampaignDO.getCreateStaff());
//                        mktCamStrategyRel.setCreateDate(mktCampaignDO.getCreateDate());
//                        mktCamStrategyRel.setUpdateStaff(mktCampaignDO.getCreateStaff());
//                        mktCamStrategyRel.setUpdateDate(mktCampaignDO.getCreateDate());
//                        mktCamStrategyRelMapper.insert(mktCamStrategyRel);
//                    }else if(openMktStrategyEntity.getActType().equals("MOD")) {
//                        MktStrategy mktCamStrategy = mktStrategyMapper.selectByPrimaryKey(openMktStrategyEntity.getStrategyId());
//                        if(mktCamStrategy == null) {
//                            resultObject.put("mktCampaigns", mktCampaigns);
//                            resultMap.put("resultCode", "1");
//                            resultMap.put("resultMsg", "处理失败,对应的营销服务策略不存在");
//                            resultMap.put("resultObject", resultObject);
//                            return resultMap;
//                        }
//                        mktStrategyMapper.updateByPrimaryKey(mktStrategy);
//                    }else if(openMktStrategyEntity.getActType().equals("DEL")) {
//                        mktStrategyMapper.deleteByPrimaryKey(mktStrategy.getStrategyId());
//                        mktCamStrategyRelMapper.deleteByStrategyId(mktStrategy.getStrategyId());
//                    }
//                }
//            }

            //修改营服活动关系
            List<OpenMktCampaignRelEntity> mktCampaignRels = openMktCampaignEntity.getMktCampaignRels();

            //修改事件触发活动规则
            List<OpenEvtTrigCamRulEntity> evtTrigCamRuls = openMktCampaignEntity.getEvtTrigCamRuls();
        }
        resultObject.put("mktCampaigns",mktCampaigns);
        resultMap.put("resultCode","0");
        resultMap.put("resultMsg","处理成功");
        resultMap.put("resultObject",resultObject);
        return resultMap;
    }
}
