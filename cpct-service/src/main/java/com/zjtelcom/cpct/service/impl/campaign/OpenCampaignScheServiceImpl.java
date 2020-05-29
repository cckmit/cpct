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
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
        List<ObjCatItemRel> objCatItemRels = new ArrayList<>();
        //对象关联标签
        List<ObjectLabelRel> objectLabelRels = new ArrayList<>();

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
                openTarGrpConditionEntityList.add(openTarGrpConditionEntity);
                expression.append(assLabel(label,condition.getOperType(),condition.getRightParam()));
                }
            }
            //todo
            openTarGrpEntity.setTarGrpConditionExpression(expression.toString());
            openTarGrpEntity.setTarGrpConditions(openTarGrpConditionEntityList);
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
            mktCamChlConf.add(openMktCamChlConfEntity);
        }
        ////营服活动关联事件
        List<MktCamEvtRelDO> relDOList = mktCamEvtRelMapper.selectByMktCampaignId(mktCampaignId);
        for (MktCamEvtRelDO relDO : relDOList) {
            OpenMktCamEvtRelEntity openMktCamEvtRelEntity = BeanUtil.create(relDO, new OpenMktCamEvtRelEntity());
            ContactEvt eventById = contactEvtMapper.getEventById(relDO.getEventId());
            openMktCamEvtRelEntity.setEventNbr(eventById.getContactEvtCode());
            openMktCamEvtRelEntity.setEventName(eventById.getContactEvtName());
            mktCamEvtRels.add(openMktCamEvtRelEntity);
        }
        //对象区域关系
        ObjRegionRelEntity objRegionRel = new ObjRegionRelEntity();
        objRegionRel.setObjId(campaignDO.getInitId());
        objRegionRel.setObjNbr(campaignDO.getMktActivityNbr());
        Long regionId = AreaCodeEnum.getRegionIdByLandId(campaignDO.getLanId());
        if(regionId != null) {
            objRegionRel.setApplyRegionNbr(regionId.toString());
            objRegionRel.setApplyRegionId(regionId);
        }
        objRegionRel.setStatusCd("1000");
        objRegionRels.add(objRegionRel);
        //对象目录节点关系
        objCatItemRels = objCatItemRelMapper.selectByObjId(mktCampaignId);
        //对象关联标签
        objectLabelRels = objectLabelRelMapper.selectByObjId(mktCampaignId);
        campaignScheEntity.setMktCamGrpRuls(mktCamGrpRuls);
        campaignScheEntity.setMktCamItems(mktCamItems);
        campaignScheEntity.setMktCamChlConf(mktCamChlConf);
        campaignScheEntity.setMktCamEvtRels(mktCamEvtRels);
        campaignScheEntity.setObjRegionRels(objRegionRels);
        campaignScheEntity.setObjCatItemRels(objCatItemRels);
        campaignScheEntity.setObjectLabelRels(objectLabelRels);
        if (campaignDO.getPlanBeginTime()!=null){
            campaignScheEntity.setPlanBeginTime(DateUtil.Date2String(campaignDO.getPlanBeginTime()));
            campaignScheEntity.setBeginTime(campaignScheEntity.getPlanBeginTime());
        }
        if (campaignDO.getPlanEndTime()!=null){
            campaignScheEntity.setPlanEndTime(DateUtil.Date2String(campaignDO.getPlanEndTime()));
            campaignScheEntity.setEndTime(campaignScheEntity.getPlanEndTime());
        }
        if (campaignDO.getCreateDate()!=null){
            campaignScheEntity.setCreateDate(DateUtil.Date2String(campaignDO.getCreateDate()));
        }
        if (campaignDO.getStatusDate()!=null){
            campaignScheEntity.setStatusDate(DateUtil.Date2String(campaignDO.getStatusDate()));
        }
        if (campaignDO.getUpdateDate()!=null){
            campaignScheEntity.setUpdateDate(DateUtil.Date2String(campaignDO.getUpdateDate()));
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
        //对象目录节点关系
        List<ObjCatItemRel> objCatItemRels = new ArrayList<>();
        //对象关联标签
        List<ObjectLabelRel> objectLabelRels = new ArrayList<>();
        List<OpenCampaignScheEntity> openCampaignScheEntities = new ArrayList<>();
        OpenCampaignScheEntity campaignScheEntity = BeanUtil.create(campaign, new OpenCampaignScheEntity());
        if (campaign.getPlanBeginTime()!=null){
            campaignScheEntity.setPlanBeginTime(DateUtil.Date2String(campaign.getPlanBeginTime()));
            campaignScheEntity.setBeginTime(campaignScheEntity.getPlanBeginTime());
        }
        if (campaign.getPlanEndTime()!=null){
            campaignScheEntity.setPlanEndTime(DateUtil.Date2String(campaign.getPlanEndTime()));
            campaignScheEntity.setEndTime(campaignScheEntity.getPlanEndTime());
        }
        if (campaign.getCreateDate()!=null){
            campaignScheEntity.setCreateDate(DateUtil.Date2String(campaign.getCreateDate()));
        }
        if (campaign.getStatusDate()!=null){
            campaignScheEntity.setStatusDate(DateUtil.Date2String(campaign.getStatusDate()));
        }
        if (campaign.getUpdateDate()!=null){
            campaignScheEntity.setUpdateDate(DateUtil.Date2String(campaign.getUpdateDate()));
        }
        //对象目录节点关系
        objCatItemRels = objCatItemRelMapper.selectByObjId(campaign.getMktCampaignId());
        //对象关联标签
        objectLabelRels = objectLabelRelMapper.selectByObjId(campaign.getMktCampaignId());
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
        logger.info("集团活动反馈接口流水号：" + uuid);
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
}
