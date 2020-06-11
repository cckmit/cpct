package com.zjtelcom.cpct.service.impl.campaign;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.cooperate.service.dubbo.IReportService;
import com.ctzj.smt.bss.core.util.UUIDUtil;
import com.zjtelcom.cpct.dao.campaign.*;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.strategy.*;
import com.zjtelcom.cpct.dao.system.SysAreaMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.openApi.mktCamChlConf.OpenMktCamChlConfEntity;
import com.zjtelcom.cpct.domain.openApi.mktCamItem.OpenMktCamItemEntity;
import com.zjtelcom.cpct.domain.openApi.mktCamItem.OpenObjCatItemRelEntity;
import com.zjtelcom.cpct.domain.openApi.mktCamItem.OpenObjectLabelRelEntity;
import com.zjtelcom.cpct.domain.openApi.mktCampaignEntity.OpenMktCamEvtRelEntity;
import com.zjtelcom.cpct.domain.openApi.mktCampaignEntity.OpenMktCamGrpRulEntity;
import com.zjtelcom.cpct.domain.openApi.mktCampaignEntity.OpenMktCamScriptEntity;
import com.zjtelcom.cpct.domain.openApi.tarGrp.OpenTarGrpConditionEntity;
import com.zjtelcom.cpct.domain.openApi.tarGrp.OpenTarGrpEntity;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.service.campaign.MktDttsLogService;
import com.zjtelcom.cpct.service.campaign.OpenCampaignScheService;
import com.zjtelcom.cpct.service.dubbo.UCCPService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.RedisUtils_prd;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OpenCampaignScheServiceImpl  implements OpenCampaignScheService {

    Logger logger =  LoggerFactory.getLogger(OpenCampaignScheServiceImpl.class);
    @Autowired
    private MktCampaignMapper mktCampaignMapper;
    @Autowired
    private MktCamItemMapper mktCamItemMapper;
    @Autowired
    private MktCamStrategyConfRelMapper mktCamStrategyConfRelMapper;
    @Autowired
    private MktStrategyConfRuleRelMapper mktStrategyConfRuleRelMapper;
    @Autowired
    private MktStrategyConfRuleMapper mktStrategyConfRuleMapper;
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
    @Autowired
    private MktCampaignCompleteMapper mktCampaignCompleteMapper;
    @Autowired(required = false)
    private IReportService iReportService;
    @Autowired
    private MktDttsLogService mktDttsLogService;
    @Autowired
    private UCCPService uccpService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisUtils_prd redisUtils_prd;
    @Autowired
    private ObjCatItemRelMapper objCatItemRelMapper;
    @Autowired
    private ObjectLabelRelMapper objectLabelRelMapper;
    @Autowired
    private TopicLabelMapper topicLabelMapper;
    @Autowired
    private CatalogItemMapper catalogItemMapper;



    /**
     * 集团活动定时任务
     * 每日凌晨1点查询前一日所有新建活动生成文件放到对应ftp文件服务器
     * @return
     */
    @Override
    public Map<String, Object> openCampaignScheForDay(Long mktCampaignId) {
        Map<String,Object> result = new HashMap<>();
        MktCampaignDO campaignDO = mktCampaignMapper.selectByPrimaryKey(mktCampaignId);
        if (campaignDO==null){
            result.put("code","500");
            result.put("data","不存在");
            return result;
        }
        OpenCampaignScheEntity campaignScheEntity = BeanUtil.create(campaignDO, new OpenCampaignScheEntity());
        campaignScheEntity.setManageType("6000");
        campaignScheEntity.setRegionNbr("8330000");
        campaignScheEntity.setRegionId(100008330000L);
        //营服活动分群规则
        List<OpenMktCamGrpRulEntity> mktCamGrpRuls = new ArrayList<>();
        //营服活动推荐条目
        List<OpenMktCamItemEntity> mktCamItems = new ArrayList<>();
        //营服活动渠道推送配置
        List<OpenMktCamChlConfEntity> mktCamChlConf = new ArrayList<>();
        //营服活动关联事件
        List<OpenMktCamEvtRelEntity> mktCamEvtRels = new ArrayList<>();
        //对象区域关系
        List<ObjRegionRelEntity> objRegionRels = new ArrayList<>();
        //对象目录节点关系
        List<OpenObjCatItemRelEntity> objCatItemRels = new ArrayList<>();
        //对象关联标签
        List<OpenObjectLabelRelEntity> objectLabelRels = new ArrayList<>();

        //营服活动分群规则
        List<MktCamGrpRul> mktCamGrpRuls1 = mktCamGrpRulMapper.selectByCampaignId(mktCampaignId);
        for (MktCamGrpRul rul : mktCamGrpRuls1) {
            OpenMktCamGrpRulEntity openMktCamGrpRulEntity = BeanUtil.create(rul, new OpenMktCamGrpRulEntity());
            TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(rul.getTarGrpId());
            OpenTarGrpEntity openTarGrpEntity = BeanUtil.create(tarGrp,new OpenTarGrpEntity());
            List<TarGrpCondition> conditions = tarGrpConditionMapper.listTarGrpCondition(rul.getTarGrpId());
            List<OpenTarGrpConditionEntity> openTarGrpConditionEntityList = new ArrayList<>();
            StringBuilder expression = new StringBuilder();
            for (TarGrpCondition condition : conditions) {
                OpenTarGrpConditionEntity openTarGrpConditionEntity = BeanUtil.create(condition, new OpenTarGrpConditionEntity());
                Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                if (label!=null){
                openTarGrpConditionEntity.setLeftParam(label.getInjectionLabelCode());
                openTarGrpConditionEntity.setLeftParamName(label.getInjectionLabelName());
                openTarGrpConditionEntity.setOperType(Operator.getOperator(Integer.valueOf(condition.getOperType())).getDescription());
                    if (condition.getCreateDate() != null) {
                        openTarGrpConditionEntity.setCreateDate(DateUtil.date2St4Trial(condition.getCreateDate()));
                    }
                    if (condition.getUpdateDate() != null) {
                        openTarGrpConditionEntity.setUpdateDate(DateUtil.date2St4Trial(condition.getUpdateDate()));
                    }
                openTarGrpConditionEntityList.add(openTarGrpConditionEntity);
                expression.append(assLabel(label,condition.getOperType(),condition.getRightParam()));
                }
            }
            //todo
            openTarGrpEntity.setTarGrpConditionExpression(expression.toString());
            openTarGrpEntity.setTarGrpConditions(openTarGrpConditionEntityList);
            if (tarGrp.getCreateDate() != null) {
                openTarGrpEntity.setCreateDate(DateUtil.date2St4Trial(tarGrp.getCreateDate()));
            }
            if (tarGrp.getUpdateDate() != null) {
                openTarGrpEntity.setUpdateDate(DateUtil.date2St4Trial(tarGrp.getUpdateDate()));
            }
            if (rul.getCreateDate() != null) {
                openMktCamGrpRulEntity.setCreateDate(DateUtil.date2St4Trial(rul.getCreateDate()));
            }
            if (rul.getUpdateDate() != null) {
                openMktCamGrpRulEntity.setUpdateDate(DateUtil.date2St4Trial(rul.getUpdateDate()));
            }
            openMktCamGrpRulEntity.setTarGrp(openTarGrpEntity);
            openMktCamGrpRulEntity.setMktCampaignId(campaignDO.getMktCampaignId());
            openMktCamGrpRulEntity.setMktActivityNbr(campaignDO.getMktActivityNbr());
            mktCamGrpRuls.add(openMktCamGrpRulEntity);
        }

        ////营服活动推荐条目
        List<MktCamItem> camItemList = mktCamItemMapper.selectByCampaignId(mktCampaignId);
        for (MktCamItem item : camItemList) {
            OpenMktCamItemEntity openMktCamItemEntity = BeanUtil.create(item, new OpenMktCamItemEntity());
            openMktCamItemEntity.setItemNbr(item.getOfferCode());
            openMktCamItemEntity.setItemName(item.getOfferName());
            openMktCamItemEntity.setPriority(0L);
            openMktCamItemEntity.setMktActivityNbr(campaignDO.getMktActivityNbr());
            openMktCamItemEntity.setMktCampaignId(campaignDO.getMktCampaignId());
            if (item.getCreateDate() != null) {
                openMktCamItemEntity.setCreateDate(DateUtil.date2St4Trial(item.getCreateDate()));
            }
            if (item.getUpdateDate() != null) {
                openMktCamItemEntity.setUpdateDate(DateUtil.date2St4Trial(item.getUpdateDate()));
            }
            mktCamItems.add(openMktCamItemEntity);
        }
        //营服活动渠道推送配置
        List<MktCamChlConfDO> mktCamChlConfDOS = mktCamChlConfMapper.selectByCampaignId(mktCampaignId);
        for (MktCamChlConfDO mktCamChlConfDO : mktCamChlConfDOS) {
            OpenMktCamChlConfEntity openMktCamChlConfEntity = BeanUtil.create(mktCamChlConfDO, new OpenMktCamChlConfEntity());
            List<OpenMktCamScriptEntity> mktCamScripts = new ArrayList<>();
            CamScript camScript = mktCamScriptMapper.selectByConfId(mktCamChlConfDO.getEvtContactConfId());
            if (camScript!=null){
                OpenMktCamScriptEntity openMktCamScriptEntity = BeanUtil.create(camScript, new OpenMktCamScriptEntity());
                if (camScript.getCreateDate() != null) {
                    openMktCamScriptEntity.setCreateDate(DateUtil.date2St4Trial(camScript.getCreateDate()));
                }
                if (camScript.getUpdateDate() != null) {
                    openMktCamScriptEntity.setUpdateDate(DateUtil.date2St4Trial(camScript.getUpdateDate()));
                }
                openMktCamScriptEntity.setMktActivityNbr(campaignDO.getMktActivityNbr());
                mktCamScripts.add(openMktCamScriptEntity);
            }
            openMktCamChlConfEntity.setMktCamScripts(mktCamScripts);
            openMktCamChlConfEntity.setMktCampaignId(campaignDO.getMktCampaignId());
            openMktCamChlConfEntity.setMktActivityNbr(campaignDO.getMktActivityNbr());
            Channel channel = contactChannelMapper.selectByPrimaryKey(mktCamChlConfDO.getContactChlId());
            if (channel!=null){
                openMktCamChlConfEntity.setContactChlCode(channel.getContactChlCode());
                openMktCamChlConfEntity.setContactChlName(channel.getContactChlName());
            }
            if (mktCamChlConfDO.getCreateDate() != null) {
                openMktCamChlConfEntity.setCreateDate(DateUtil.date2St4Trial(mktCamChlConfDO.getCreateDate()));
            }
            if (mktCamChlConfDO.getUpdateDate() != null) {
                openMktCamChlConfEntity.setUpdateDate(DateUtil.date2St4Trial(mktCamChlConfDO.getUpdateDate()));
            }
            mktCamChlConf.add(openMktCamChlConfEntity);
        }
        //营服活动关联事件
        List<MktCamEvtRelDO> relDOList = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamEvtRelDO relDO : relDOList) {
            OpenMktCamEvtRelEntity openMktCamEvtRelEntity = BeanUtil.create(relDO, new OpenMktCamEvtRelEntity());
            ContactEvt eventById = contactEvtMapper.getEventById(relDO.getEventId());
            openMktCamEvtRelEntity.setEventNbr(eventById.getContactEvtCode());
            openMktCamEvtRelEntity.setEventName(eventById.getContactEvtName());
            if (eventById.getCreateDate()!=null){
                openMktCamEvtRelEntity.setCreateDate(DateUtil.date2St4Trial(eventById.getCreateDate()));
            }
            if (eventById.getUpdateDate()!=null){
                openMktCamEvtRelEntity.setUpdateDate(DateUtil.date2St4Trial(eventById.getUpdateDate()));
            }
            mktCamEvtRels.add(openMktCamEvtRelEntity);
        }
        //对象区域关系
        ObjRegionRelEntity objRegionRel = new ObjRegionRelEntity();
        objRegionRel.setObjRegionRelId(campaignDO.getMktCampaignId());
        objRegionRel.setObjId(campaignDO.getInitId());
        objRegionRel.setObjNbr(campaignDO.getMktActivityNbr());
        Long regionId = AreaCodeEnum.getRegionIdByLandId(campaignDO.getLanId());
        if(regionId != null) {
            objRegionRel.setApplyRegionNbr(regionId.toString());
            objRegionRel.setApplyRegionId(regionId);
        }
        objRegionRel.setStatusCd("1000");
        objRegionRel.setCreateDate(DateUtil.date2St4Trial(campaignDO.getCreateDate()));
        objRegionRel.setUpdateDate(DateUtil.date2St4Trial(campaignDO.getUpdateDate()));
        objRegionRels.add(objRegionRel);
        //对象目录节点关系
        ObjInfoCreate objInfoCreate = new ObjInfoCreate(campaignDO).invoke();
        objCatItemRels = objInfoCreate.getObjCatItemRels();
        objectLabelRels = objInfoCreate.getObjectLabelRels();
        campaignScheEntity.setMktCamGrpRuls(mktCamGrpRuls);
        campaignScheEntity.setMktCamItems(mktCamItems);
        campaignScheEntity.setMktCamChlConfs(mktCamChlConf);
        campaignScheEntity.setMktCamEvtRels(mktCamEvtRels);
        campaignScheEntity.setObjRegionRels(objRegionRels);
        campaignScheEntity.setObjCatItemRels(objCatItemRels);
        campaignScheEntity.setObjectLabelRels(objectLabelRels);
        if (campaignDO.getPlanBeginTime()!=null){
            campaignScheEntity.setPlanBeginTime(DateUtil.date2St4Trial(campaignDO.getPlanBeginTime()));
            campaignScheEntity.setBeginTime(campaignScheEntity.getPlanBeginTime());
        }
        if (campaignDO.getPlanEndTime()!=null){
            campaignScheEntity.setPlanEndTime(DateUtil.date2St4Trial(campaignDO.getPlanEndTime()));
            campaignScheEntity.setEndTime(campaignScheEntity.getPlanEndTime());
        }
        if (campaignDO.getCreateDate()!=null){
            campaignScheEntity.setCreateDate(DateUtil.date2St4Trial(campaignDO.getCreateDate()));
        }
        if (campaignDO.getStatusDate()!=null){
            campaignScheEntity.setStatusDate(DateUtil.date2St4Trial(campaignDO.getStatusDate()));
        }
        if (campaignDO.getUpdateDate()!=null){
            campaignScheEntity.setUpdateDate(DateUtil.date2St4Trial(campaignDO.getUpdateDate()));
        }
        result.put("code","200");
        result.put("data",campaignScheEntity);
        return result;
    }


    //表达式拼接
    public static String assLabel(Label label, String type, String rightParam) {
        StringBuilder express = new StringBuilder();
        switch (type) {
            case "1000":
                express.append(label.getInjectionLabelName());
                express.append(" 大于 ");
                express.append(rightParam).append(" ");
                break;
            case "2000":
                express.append(label.getInjectionLabelName());
                express.append(" 小于 ");
                express.append(rightParam).append(" ");
                break;
            case "3000":
                express.append(label.getInjectionLabelName());
                express.append(" 等于 ");
                express.append(rightParam).append(" ");
                break;
            case "4000":
                express.append(label.getInjectionLabelName());
                express.append(" 不等于 ");
                express.append(rightParam).append(" ");
                break;
            case "5000":
                express.append(label.getInjectionLabelName());
                express.append(" 大于等于 ");
                express.append(rightParam).append(" ");
                break;
            case "6000":
                express.append(label.getInjectionLabelName());
                express.append(" 小于等于 ");
                express.append(rightParam).append(" ");
                break;
            case "7100":
            case "7000":
                express.append(label.getInjectionLabelName());
                express.append(" 包含 ");
                String[] strArray = rightParam.split(",");
                express.append("(");
                for (int j = 0; j < strArray.length; j++) {
                    express.append("\"").append(strArray[j]).append("\"");
                    if (j != strArray.length - 1) {
                        express.append(",");
                    }
                }
                express.append(")");
                break;
            case "7200":
                express.append(label.getInjectionLabelName());
                String[] strArray2 = rightParam.split(",");
                express.append(" 大于等于 ").append(strArray2[0]);
                express.append(" && ");
                express.append(label.getInjectionLabelName());
                express.append(" 小于等于 ").append(strArray2[1]);
        }
        return express.toString();
    }

    //营销活动许可证申请
    @Override
    public Map<String, Object> openApimktCampaignBorninfoOrder(MktCampaignDO campaign) {
        Map<String,Object> result = new HashMap<>();
        //设置集团营服活动反馈接口入参
        Map<String,Object> inputMap = new HashMap<>();
        List<OpenObjCatItemRelEntity> objCatItemRels = new ArrayList<>();
        //对象关联标签
        List<OpenObjectLabelRelEntity> objectLabelRels = new ArrayList<>();
        List<OpenCampaignScheEntity> openCampaignScheEntities = new ArrayList<>();
        OpenCampaignScheEntity campaignScheEntity = BeanUtil.create(campaign, new OpenCampaignScheEntity());
        campaignScheEntity.setManageType("6000");
        campaignScheEntity.setRegionNbr("8330000");
        if (campaign.getPlanBeginTime()!=null){
            campaignScheEntity.setPlanBeginTime(DateUtil.date2St4Trial(campaign.getPlanBeginTime()));
            campaignScheEntity.setBeginTime(campaignScheEntity.getPlanBeginTime());
        }
        if (campaign.getPlanEndTime()!=null){
            campaignScheEntity.setPlanEndTime(DateUtil.date2St4Trial(campaign.getPlanEndTime()));
            campaignScheEntity.setEndTime(campaignScheEntity.getPlanEndTime());
        }
        if (campaign.getCreateDate()!=null){
            campaignScheEntity.setCreateDate(DateUtil.date2St4Trial(campaign.getCreateDate()));
        }
        if (campaign.getStatusDate()!=null){
            campaignScheEntity.setStatusDate(DateUtil.date2St4Trial(campaign.getStatusDate()));
        }
        if (campaign.getUpdateDate()!=null){
            campaignScheEntity.setUpdateDate(DateUtil.date2St4Trial(campaign.getUpdateDate()));
        }
        ObjInfoCreate objInfoCreate = new ObjInfoCreate(campaign).invoke();



        objCatItemRels = objInfoCreate.getObjCatItemRels();
        objectLabelRels = objInfoCreate.getObjectLabelRels();
        campaignScheEntity.setObjCatItemRels(objCatItemRels);
        campaignScheEntity.setObjectLabelRels(objectLabelRels);
        Map<String,Object> paramMap = new HashMap<>();
        openCampaignScheEntities.add(campaignScheEntity);
        paramMap.put("mktCampaignBorninfoOrderDetails",openCampaignScheEntities);
        inputMap.put("requestObject",paramMap);
        String jsonString = JSON.toJSONString(inputMap);
        logger.info("集团营销活动许可证：" + jsonString);

        //调用集团营服活动反馈接口地址
        CloseableHttpClient httpClient = HttpClients.createDefault();
        List<SysParams> sysParamsList = sysParamsMapper.listParamsByKeyForCampaign("OPENAPI_URL_XUKEZHENG");
        String url = sysParamsList.get(0).getParamValue();
        HttpPost ht = new HttpPost(url);
        ht.setHeader("Content-Type", "application/json");
        String uuid = UUIDUtil.getUUID();
        ht.setHeader("X-CTG-Request-ID", uuid);
        logger.info("集团营销活动许可证流水号：" + uuid);
        List<SysParams> sysParamList = sysParamsMapper.selectAll(null, "OPENAPI_HEADER");
        JSONObject json = JSON.parseObject(sysParamList.get(0).getParamValue());
        String appId = json.get("X-APP-ID").toString();
        String appKey = json.get("X-APP-KEY").toString();
        String reginId = json.get("X-CTG-Region-ID").toString();
        ht.setHeader("X-CTG-Region-ID", reginId);
        ht.setHeader("X-APP-ID", appId);
        ht.setHeader("X-APP-KEY", appKey);
        StringEntity entity = new StringEntity(jsonString, ContentType.APPLICATION_JSON);
        logger.info(entity.toString());
        ht.setEntity(entity);
        String content = "";
        try {
            HttpResponse response = httpClient.execute(ht);
            content = EntityUtils.toString(response.getEntity());
            logger.info("集团返回结果：" + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("message", "集团营销活动许可证");
        result.put("inputMap", jsonString);
        return result;
    }

    private class ObjInfoCreate {
        private MktCampaignDO campaign;
        private List<OpenObjCatItemRelEntity> catItemRelEntities;
        private List<OpenObjectLabelRelEntity> labelRelEntityList;

        public ObjInfoCreate(MktCampaignDO campaign) {
            this.campaign = campaign;
        }

        public List<OpenObjCatItemRelEntity> getObjCatItemRels() {
            return catItemRelEntities;
        }

        public List<OpenObjectLabelRelEntity> getObjectLabelRels() {

            return labelRelEntityList;
        }

        public ObjInfoCreate invoke() {
             List<OpenObjCatItemRelEntity> calList = new ArrayList<>();
             List<OpenObjectLabelRelEntity> labellist = new ArrayList<>();
            List<ObjCatItemRel> objCatItemRels = objCatItemRelMapper.selectByObjId(campaign.getMktCampaignId());
            for (ObjCatItemRel objCatItemRel : objCatItemRels) {
                OpenObjCatItemRelEntity openObjCatItemRelEntity = BeanUtil.create(objCatItemRel, new OpenObjCatItemRelEntity());
                openObjCatItemRelEntity.setObjNbr(campaign.getMktActivityNbr());
                CatalogItem catalogItem = catalogItemMapper.selectByPrimaryKey(objCatItemRel.getCatalogItemId());
                if (catalogItem!=null){
                    openObjCatItemRelEntity.setCatalogItemName(catalogItem.getCatalogItemName());
                    openObjCatItemRelEntity.setCatalogItemNbr(catalogItem.getCatalogItemNbr());
                }
                if (objCatItemRel.getCreateDate()!=null){
                    openObjCatItemRelEntity.setCreateDate(DateUtil.date2St4Trial(objCatItemRel.getCreateDate()));
                }
                if (objCatItemRel.getUpdateDate()!=null){
                    openObjCatItemRelEntity.setUpdateDate(DateUtil.date2St4Trial(objCatItemRel.getUpdateDate()));
                }
                calList.add(openObjCatItemRelEntity);
            }

            catItemRelEntities = calList;

            //对象关联标签
            List<ObjectLabelRel> objectLabelRels = objectLabelRelMapper.selectByObjId(campaign.getMktCampaignId());
            for (ObjectLabelRel objectLabelRel : objectLabelRels) {
                OpenObjectLabelRelEntity openObjectLabelRelEntity = BeanUtil.create(objectLabelRel, new OpenObjectLabelRelEntity());
                openObjectLabelRelEntity.setObjNbr(campaign.getMktActivityNbr());
                TopicLabel label = topicLabelMapper.selectByPrimaryKey(objectLabelRel.getLabelId());
                if (label!=null){
                    openObjectLabelRelEntity.setLabelName(label.getLabelName());
                    openObjectLabelRelEntity.setLabelCode(label.getLabelCode());
                }
                if (objectLabelRel.getCreateDate()!=null){
                    openObjectLabelRelEntity.setCreateDate(DateUtil.date2St4Trial(objectLabelRel.getCreateDate()));
                }
                if (objectLabelRel.getUpdateDate()!=null){
                    openObjectLabelRelEntity.setUpdateDate(DateUtil.date2St4Trial(objectLabelRel.getUpdateDate()));
                }
                labellist.add(openObjectLabelRelEntity);
            }
            labelRelEntityList = labellist;
            return this;
        }
    }
}
