package com.zjtelcom.cpct.service.impl.grouping;

import com.sun.corba.se.spi.ior.ObjectKey;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamGrpRulMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.dao.channel.MktCamScriptMapper;
import com.zjtelcom.cpct.dao.channel.MktVerbalMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpMapper;
import com.zjtelcom.cpct.dao.org.OrgTreeMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.domain.org.OrgTreeDO;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.channel.LabelValueVO;
import com.zjtelcom.cpct.dto.channel.OperatorDetail;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.SysAreaVO;
import com.zjtelcom.cpct.dto.grouping.TarGrp;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dto.grouping.TarGrpDetail;
import com.zjtelcom.cpct.enums.*;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.MessageLabelService;
import com.zjtelcom.cpct.service.grouping.TarGrpService;
import com.zjtelcom.cpct.util.*;
import com.zjtelcom.cpct.vo.grouping.TarGrpConditionVO;
import com.zjtelcom.cpct.vo.grouping.TarGrpVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;
import static com.zjtelcom.cpct.constants.CommonConstant.STATUSCD_EFFECTIVE;

/**
 * @Description 目标分群serviceImpl
 * @Author pengy
 * @Date 2018/6/25 10:34
 */
@Service
@Transactional
public class TarGrpServiceImpl extends BaseService implements TarGrpService {

    @Autowired
    private TarGrpMapper tarGrpMapper;
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private MktCamGrpRulMapper mktCamGrpRulMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private OrgTreeMapper orgTreeMapper;
    @Autowired
    private MktCamGrpRulMapper grpRulMapper;
    @Autowired
    private MktStrategyConfRuleMapper ruleMapper;
    @Autowired
    private MktCamScriptMapper camScriptMapper;
    @Autowired
    private MktVerbalMapper verbalMapper;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private MessageLabelService messageLabelService;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private MktCamEvtRelMapper evtRelMapper;


    @Override
    public Map<String, Object> labelListByEventId(Long eventId) {
        List<Map<String,Object>> campaignDOS = evtRelMapper.listActivityByEventId(eventId);
        List<Integer> campaigns = new ArrayList<>();
        if (campaignDOS!=null && !campaignDOS.isEmpty()){
            for (Map<String,Object> cam : campaignDOS){
                if (cam.get("mktCampaginId")!=null){
                    campaigns.add(Integer.valueOf(cam.get("mktCampaginId").toString()));
                }
            }
        }
        return labelListByCampaignId(campaigns);
    }

    @Override
    public Map<String, Object> labelListByCampaignId(List<Integer> campaignId) {
        Map<String,Object> re = new HashMap<>();
        List<String> assetCode = new ArrayList<>();//2000
        List<String> promCode = new ArrayList<>();//3000
        List<String> custCode = new ArrayList<>();//1000

        List<Long> idlIst = new ArrayList<>();
        for (Integer id : campaignId){
            MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(Long.valueOf(id.toString()));
            if (campaign==null){
                continue;
            }
            //展示列标签
            DisplayColumn req = new DisplayColumn();
            req.setDisplayColumnId(campaign.getCalcDisplay());
            Map<String, Object> labelMap = messageLabelService.queryLabelListByDisplayId(req);
            List<LabelDTO> labelDTOList = (List<LabelDTO>) labelMap.get("labels");
            for (LabelDTO labelDTO : labelDTOList) {
                Label label = injectionLabelMapper.selectByLabelCode(labelDTO.getLabelCode());
                if (label!=null) {
                    if (idlIst.contains(label.getInjectionLabelId())) {
                        continue;
                    }
                    idlIst.add(label.getInjectionLabelId());
                    codeList(assetCode, promCode, custCode, label);
                }
            }
            //过滤规则标签
            List<FilterRule> filterRules = filterRuleMapper.selectFilterRuleList(Long.valueOf(id.toString()));
            for (FilterRule filterRule : filterRules){
                if ("6000".equals(filterRule.getFilterType()) && filterRule.getConditionId()!=null){
                    Label label = injectionLabelMapper.selectByPrimaryKey(filterRule.getConditionId());
                    if (label!=null) {
                        if (idlIst.contains(label.getInjectionLabelId())) {
                            continue;
                        }
                        idlIst.add(label.getInjectionLabelId());
                        codeList(assetCode, promCode, custCode, label);
                    }
                }
                if ("3000".equals(filterRule.getFilterType()) && !promCode.contains("PROM_LIST")){
                    assetCode.add("PROM_LIST");
                }
            }
            //规则级的标签
            List<MktStrategyConfRuleDO> ruleList =ruleMapper.selectByCampaignId(Long.valueOf(id.toString()));
            for (MktStrategyConfRuleDO rule : ruleList) {
                if (rule.getTarGrpId() == null) {
                    continue;
                }
                if (rule.getEvtContactConfId()!=null && !rule.getEvtContactConfId().equals("")){
                    String[] confList = rule.getEvtContactConfId().split("/");
                    //推荐指引标签
                    for (String confId : confList){
                        CamScript camScript = camScriptMapper.selectByConfId(Long.valueOf(confId));
                        if (camScript!=null && camScript.getScriptDesc()!=null){
                            List<String> labelSc = subScript(camScript.getScriptDesc());
                            for (String code : labelSc){
                                Label label = injectionLabelMapper.selectByLabelCode(code);
                                if (label!=null){
                                    if (idlIst.contains(label.getInjectionLabelId())){
                                        continue;
                                    }
                                    idlIst.add(label.getInjectionLabelId());
                                    codeList(assetCode, promCode, custCode, label);
                                }

                            }
                        }
                        //话术标签
                        List<MktVerbal> verbalList = verbalMapper.findVerbalListByConfId(Long.valueOf(confId));
                        for (MktVerbal verbal : verbalList){
                            List<String> labelSc = subScript(verbal.getScriptDesc());
                            for (String code : labelSc){
                                Label label = injectionLabelMapper.selectByLabelCode(code);
                                if (label!=null){
                                    if (idlIst.contains(label.getInjectionLabelId())){
                                        continue;
                                    }
                                    idlIst.add(label.getInjectionLabelId());
                                    codeList(assetCode, promCode, custCode, label);
                                }
                            }
                        }
                    }
                }
                //分群标签
                List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(rule.getTarGrpId());
                for (TarGrpCondition condition : conditionList) {
                    Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                    if (idlIst.contains(label.getInjectionLabelId())){
                        continue;
                    }
                    idlIst.add(label.getInjectionLabelId());
                    if (label != null ) {
                        codeList(assetCode, promCode, custCode, label);
                    }
                }

            }
        }
        if (!promCode.isEmpty() && !assetCode.contains("PROM_INTEG_ID")){
            assetCode.add("PROM_INTEG_ID");
        }
        re.put("code",CODE_SUCCESS);
        re.put("assetCode",ChannelUtil.StringList2String(assetCode));
        re.put("promCode",ChannelUtil.StringList2String(promCode));
        re.put("custCode",ChannelUtil.StringList2String(custCode));
        return re;
    }

    private void codeList(List<String> assetCode, List<String> promCode, List<String> custCode, Label label) {
        if (label.getLabelType().equals("1000")){
            custCode.add(label.getInjectionLabelCode());
        }else if (label.getLabelType().equals("2000")){
            assetCode.add(label.getInjectionLabelCode());
        }else {
            promCode.add(label.getInjectionLabelCode());
        }
    }

    private List<String> subScript(String str) {
        List<String> result = new ArrayList<>();
//        Pattern p = Pattern.compile("\\$");
        Pattern p = Pattern.compile("(?<=\\$\\{)([^$]+)(?=\\}\\$)");
        Matcher m = p.matcher(str);
//        List<Integer> list = new ArrayList<>();

        while (m.find()) {
//            list.add(m.start());
            result.add(m.group(1));
        }

//        for (int i = 0; i < list.size(); ) {
//            result.add(str.substring(list.get(i) + 1, list.get(++i)));
//            i++;
//        }
        return result;
    }



    /**
     * 复制客户分群 返回
     * @param tarGrpId
     * @return
     */
    @Override
    public Map<String, Object> copyTarGrp(Long tarGrpId,boolean isCopy) {
        Map<String,Object> result = new HashMap<>();
//        TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpId);
//        if (tarGrp==null){
//            result.put("resultCode", CODE_FAIL);
//            result.put("resultMsg", "请选择下拉框运算类型");
//            return result;
//        }
//        List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
//        TarGrpDetail detail = BeanUtil.create(tarGrp,new TarGrpDetail());
//        detail.setTarGrpConditions(conditionList);
        TarGrpDetail detail = (TarGrpDetail)redisUtils.get("TAR_GRP_"+tarGrpId);
        if (detail==null){
            TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpId);
            if (tarGrp!=null){
                detail = BeanUtil.create(tarGrp,new TarGrpDetail());
                List<TarGrpCondition> conditions = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
                detail.setTarGrpConditions(conditions);
            }else {
                return null;
            }
        }
        result = createTarGrp(detail,isCopy);
        return result;
    }


    /**
     * 模板创建客户分群
     * @param templateId
     * @return‘
     */
    @Override
    public Map<String, Object> createTarGrpByTemplateId(Long templateId,Long oldTarGrpId,String needDeleted) {
        Map<String, Object> result = new HashMap<>();
        TarGrp template = tarGrpMapper.selectByPrimaryKey(templateId);
        if (template==null){
            result.put("resultCode", CODE_FAIL);
            result.put("resultMsg", "模板不存在");
            return result;
        }
        List<TarGrpCondition> conditionDOList = tarGrpConditionMapper.listTarGrpCondition(templateId);

        TarGrpDetail addVO = BeanUtil.create(template,new TarGrpDetail());

        if (needDeleted .equals("0")){
            tarGrpConditionMapper.deleteByTarGrpTemplateId(templateId);
            tarGrpMapper.deleteByPrimaryKey(templateId);
        }
        addVO.setTarGrpId(null);
        addVO.setRemark(null);
        List<TarGrpCondition> conditionAdd = new ArrayList<>();
        for (TarGrpCondition conditionDO : conditionDOList){
            TarGrpCondition con = BeanUtil.create(conditionDO,new TarGrpCondition());
            if (needDeleted.equals("0")){
                con.setStatusCd("1100");
            }
            conditionAdd.add(con);
        }
        addVO.setTarGrpConditions(conditionAdd);
        Map<String,Object> crMap = createTarGrp(addVO,false);

        if (crMap.get("resultCode").equals(CODE_SUCCESS)){
            if (oldTarGrpId!=0){
                TarGrp grp = tarGrpMapper.selectByPrimaryKey(oldTarGrpId);
                if (grp!=null){
                    tarGrpMapper.deleteByPrimaryKey(oldTarGrpId);
                    List<TarGrpCondition> conditions = tarGrpConditionMapper.listTarGrpCondition(oldTarGrpId);
                    for (TarGrpCondition condition : conditions){
                        tarGrpConditionMapper.deleteByPrimaryKey(condition.getConditionId());
                    }
                    MktCamGrpRul rul = grpRulMapper.selectByTarGrpId(oldTarGrpId);
                    if (rul!=null){
                        grpRulMapper.deleteByTarGrpId(oldTarGrpId);
                    }
                }
            }
            result = crMap;
            return result;
        }else {
            result = crMap;
        }
        return result;
    }


    /**
     * 新增目标分群
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> createTarGrp(TarGrpDetail tarGrpDetail, boolean isCopy) {
        Map<String, Object> maps = new HashMap<>();
        try {
            //插入客户分群记录
            TarGrp tarGrp = new TarGrp();
            tarGrp = tarGrpDetail;
            tarGrp.setTarGrpType(tarGrpDetail.getTarGrpType()==null ? "1000" : tarGrpDetail.getTarGrpType());
            tarGrp.setCreateDate(DateUtil.getCurrentTime());
            tarGrp.setUpdateDate(DateUtil.getCurrentTime());
            tarGrp.setStatusDate(DateUtil.getCurrentTime());
            tarGrp.setUpdateStaff(UserUtil.loginId());
            tarGrp.setCreateStaff(UserUtil.loginId());
            if (isCopy){
                tarGrp.setStatusCd(StatusCode.STATUS_CODE_FAILURE.getStatusCode());
            }else {
                tarGrp.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            }
            tarGrpMapper.createTarGrp(tarGrp);
            List<TarGrpCondition> tarGrpConditions = tarGrpDetail.getTarGrpConditions();
            List<TarGrpCondition> conditionList = new ArrayList<>();
            if(tarGrpConditions!=null && tarGrpConditions.size()>0){
                for (TarGrpCondition tarGrpCondition : tarGrpConditions) {
                    if (tarGrpCondition.getOperType()==null || tarGrpCondition.getOperType().equals("")){
                        maps.put("resultCode", CODE_FAIL);
                        maps.put("resultMsg", "请选择下拉框运算类型");
                        return maps;
                    }
                    if (tarGrpCondition.getAreaIdList()!=null){
                        area2RedisThread(tarGrp, tarGrpCondition);
                    }
                    tarGrpCondition.setConditionId(null);
                    tarGrpCondition.setRootFlag(0L);
                    tarGrpCondition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                    tarGrpCondition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                    tarGrpCondition.setTarGrpId(tarGrp.getTarGrpId());
                    tarGrpCondition.setCreateDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setUpdateDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setStatusDate(DateUtil.getCurrentTime());
                    tarGrpCondition.setUpdateStaff(UserUtil.loginId());
                    tarGrpCondition.setCreateStaff(UserUtil.loginId());
                    if (isCopy){
                        tarGrpCondition.setStatusCd(StatusCode.STATUS_CODE_FAILURE.getStatusCode());
                    }else if (tarGrpCondition.getStatusCd()==null){
                        tarGrpCondition.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                    }
                    conditionList.add(tarGrpCondition);
                }
                tarGrpConditionMapper.insertByBatch(conditionList);
            }
            //数据加入redis
            TarGrpDetail detail = BeanUtil.create(tarGrp,new TarGrpDetail());
            detail.setTarGrpConditions(conditionList);
            redisUtils.set("TAR_GRP_"+tarGrp.getTarGrpId(),detail);
            //插入客户分群条件
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("tarGrp", tarGrp);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return maps;
    }

    private void area2RedisThread(TarGrp tarGrp, final TarGrpCondition tarGrpCondition) {
        final Long targrpId = tarGrp.getTarGrpId();
        List<OrgTreeDO> sysAreaList = new ArrayList<>();
        for (Integer id : tarGrpCondition.getAreaIdList()){
            OrgTreeDO orgTreeDO = orgTreeMapper.selectByAreaId(id);
            if (orgTreeDO!=null){
                sysAreaList.add(orgTreeDO);
            }
        }
        redisUtils.set("AREA_RULE_ENTITY_"+targrpId,sysAreaList);
        new Thread() {
            public void run() {
                areaList2Redis(targrpId,tarGrpCondition.getAreaIdList());
            }
        }.start();
    }


    public void areaList2Redis(Long targrpId,List<Integer> areaIdList){
        List<String> resultList = new ArrayList<>();
        List<OrgTreeDO> sysAreaList = new ArrayList<>();
        for (Integer id : areaIdList){
            areaList(id,resultList,sysAreaList);
        }
        redisUtils.set("AREA_RULE_"+targrpId,resultList.toArray(new String[resultList.size()]));
    }

    public List<String> areaList(Integer parentId,List<String> resultList,List<OrgTreeDO> areas){
        List<OrgTreeDO> sysAreaList = orgTreeMapper.selectBySumAreaId(parentId);
        if (sysAreaList.isEmpty()){
            return resultList;
        }
        for (OrgTreeDO area : sysAreaList){
            resultList.add(area.getAreaName());
            areas.add(area);
            areaList(area.getAreaId(),resultList,areas);
        }
        return resultList;
    }

    /**
     * 新增目标分群(暂时废弃)
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveTagNumFetch(Long mktCamGrpRulId, List<TarGrpCondition> tarGrpConditionDTOList) {
        Map<String, Object> maps = new HashMap<>();
        TarGrpDetail tarGrpDetail = new TarGrpDetail();
        try {
            //生成客户分群
            tarGrpDetail = new TarGrpDetail();
            tarGrpDetail.setStatusCd("1000");
            tarGrpMapper.insert(tarGrpDetail);
            //添加客户分群条件
            for (int i = 0; i < tarGrpConditionDTOList.size(); i++) {
//                TarGrpConditionDO tarGrpConditionDO = tarGrpConditionDTOList.get(i);
                TarGrpConditionDO tarGrpConditionDO = null;
                tarGrpConditionDO.setTarGrpId(tarGrpDetail.getTarGrpId());
//                tarGrpConditionMapper.insert(tarGrpCondition);
            }
            //更新营销活动分群规则表
            MktCamGrpRul mktCamGrpRul = new MktCamGrpRul();
            mktCamGrpRul.setMktCamGrpRulId(mktCamGrpRulId);
            mktCamGrpRul.setTarGrpId(tarGrpDetail.getTarGrpId());
            mktCamGrpRulMapper.updateByPrimaryKey(mktCamGrpRul);

        } catch (Exception e) {
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg", ErrorCode.SAVE_TAR_GRP_FAILURE.getErrorMsg());
            maps.put("tarGrp", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to saveTagNumFetch ", e);
            return maps;
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", tarGrpDetail);
        return maps;
    }

    /**
     * 删除目标分群条件
     */
    @Override
    public Map<String, Object> delTarGrpCondition(Long conditionId) {
        Map<String, Object> mapsT = new HashMap<>();
        Long tarGrpId = null;
        boolean isDeleted = false;
        try {
            TarGrpCondition condition = tarGrpConditionMapper.selectByPrimaryKey(conditionId);
            if (condition==null){
                mapsT.put("resultCode", CODE_FAIL);
                mapsT.put("resultMsg", ErrorCode.DEL_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
                return mapsT;
            }
            tarGrpId = condition.getTarGrpId();
            tarGrpConditionMapper.deleteByPrimaryKey(conditionId);
            TarGrp tarGrp = tarGrpMapper.selectByPrimaryKey(tarGrpId);
            if (tarGrp==null){
                mapsT.put("resultCode", CODE_FAIL);
                mapsT.put("resultMsg", "分群不存在");
                return mapsT;
            }
            TarGrpDetail detail = (TarGrpDetail)redisUtils.get("TAR_GRP_"+tarGrpId);
            List<TarGrpCondition> conditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
            if (detail!=null){
                 detail = BeanUtil.create(tarGrp,new TarGrpDetail());
                detail.setTarGrpConditions(conditionList);
                redisUtils.set("TAR_GRP_"+tarGrp.getTarGrpId(),detail);
            }
            if (conditionList.isEmpty()){
                tarGrpMapper.deleteByPrimaryKey(tarGrpId);
                mktCamGrpRulMapper.deleteByTarGrpId(tarGrpId);
                redisUtils.remove("TAR_GRP_"+tarGrp.getTarGrpId());
                isDeleted = true;
            }
        } catch (Exception e) {
            mapsT.put("resultCode", CODE_FAIL);
            mapsT.put("resultMsg", ErrorCode.DEL_TAR_GRP_CONDITION_FAILURE.getErrorMsg());
            mapsT.put("resultObject", StringUtils.EMPTY);
            logger.error("[op:TarGrpServiceImpl] fail to delTarGrpCondition ", e);
            return mapsT;
        }
        mapsT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapsT.put("resultMsg", StringUtils.EMPTY);
        if (isDeleted){
            mapsT.put("resultObject", null);
        }else {
            mapsT.put("resultObject", tarGrpId);
        }
        return mapsT;
    }

    /**
     * 编辑目标分群条件
     */
    @Override
    public Map<String, Object> editTarGrpConditionDO(Long conditionId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            TarGrpCondition tarGrpCondition = new TarGrpCondition();
            tarGrpCondition = tarGrpConditionMapper.getTarGrpCondition(conditionId);
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", StringUtils.EMPTY);
            maps.put("tarGrpCondition", tarGrpCondition);
        } catch (Exception e) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
        }
        return maps;
    }


    /**
     * 修改目标分群
     */
    @Override
    public Map<String, Object> modTarGrp(TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        try {
            TarGrp tarGrp = new TarGrp();
            tarGrp = tarGrpDetail;
            tarGrp.setUpdateDate(DateUtil.getCurrentTime());
            tarGrp.setUpdateStaff(UserUtil.loginId());
            tarGrpMapper.modTarGrp(tarGrp);
            List<TarGrpCondition> tarGrpConditions = tarGrpDetail.getTarGrpConditions();
            List<TarGrpCondition> insertConditions = new ArrayList<>();
            List<TarGrpCondition> allCondition = new ArrayList<>();
            List<TarGrpCondition> oldConditionList = tarGrpConditionMapper.listTarGrpCondition(tarGrp.getTarGrpId());
            List<Long> delList = new ArrayList<>();

            for (TarGrpCondition tarGrpCondition : tarGrpConditions) {
                TarGrpCondition tarGrpCondition1 = tarGrpConditionMapper.selectByPrimaryKey(tarGrpCondition.getConditionId());
                if (tarGrpCondition1 == null) {
                    if (tarGrpCondition.getOperType()==null || tarGrpCondition.getOperType().equals("")){
                        maps.put("resultCode", CODE_FAIL);
                        maps.put("resultMsg", "请选择下拉框运算类型");
                        return maps;
                    }
                    TarGrpCondition condition = BeanUtil.create(tarGrpCondition,new TarGrpCondition());
                    if (tarGrpCondition.getAreaIdList()!=null){
                        area2RedisThread(tarGrp, tarGrpCondition);
                    }
                    condition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                    condition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                    condition.setRootFlag(0L);
                    condition.setTarGrpId(tarGrp.getTarGrpId());
                    condition.setUpdateDate(DateUtil.getCurrentTime());
                    condition.setCreateDate(DateUtil.getCurrentTime());
                    condition.setStatusDate(DateUtil.getCurrentTime());
                    condition.setUpdateStaff(UserUtil.loginId());
                    condition.setCreateStaff(UserUtil.loginId());
                    condition.setStatusCd(tarGrpCondition.getStatusCd()==null ? STATUSCD_EFFECTIVE : tarGrpCondition.getStatusCd());
                    insertConditions.add(condition);
                } else {
                    BeanUtil.copy(tarGrpCondition,tarGrpCondition1);
                    tarGrpCondition1.setUpdateDate(DateUtil.getCurrentTime());
                    tarGrpCondition1.setUpdateStaff(UserUtil.loginId());
                    tarGrpConditionMapper.modTarGrpCondition(tarGrpCondition1);
                    allCondition.add(tarGrpCondition1);
                }
            }
            if (!insertConditions.isEmpty()){
                tarGrpConditionMapper.insertByBatch(insertConditions);
            }
            allCondition.addAll(insertConditions);

            //不存在的删除
            List<Long> allList = new ArrayList<>();
            for (TarGrpCondition condition : allCondition){
                allList.add(condition.getConditionId());
            }
            for (TarGrpCondition condition : oldConditionList){
                if (allList.contains(condition.getConditionId())){
                    continue;
                }
                delList.add(condition.getConditionId());
            }
            if (!delList.isEmpty()){
                tarGrpConditionMapper.deleteBatch(delList);
            }
            //更新redis分群数据
            TarGrpDetail detail = BeanUtil.create(tarGrp,new TarGrpDetail());
            detail.setTarGrpConditions(allCondition);
            redisUtils.set("TAR_GRP_"+tarGrp.getTarGrpId(),detail);

            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", "修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "修改失败！");
        }
        return maps;
    }

    /**
     * 删除目标分群
     */
    @Override
    public Map<String, Object> delTarGrp(TarGrpDetail tarGrpDetail) {
        Map<String, Object> maps = new HashMap<>();
        final TarGrp tarGrp = tarGrpDetail;
        tarGrpMapper.delTarGrp(tarGrp);
        List<TarGrpCondition> tarGrpConditions = tarGrpDetail.getTarGrpConditions();
        for (TarGrpCondition tarGrpCondition : tarGrpConditions) {
            tarGrpConditionMapper.delTarGrpCondition(tarGrpCondition);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }


    /**
     * 修改目标分群条件
     */
    @Override
    public Map<String, Object> updateTarGrpCondition(TarGrpCondition tarGrpCondition) {
        Map<String, Object> mapsT = new HashMap<>();
        tarGrpCondition.setUpdateDate(DateUtil.getCurrentTime());
        tarGrpCondition.setUpdateStaff(UserUtil.loginId());
        tarGrpConditionMapper.modTarGrpCondition(tarGrpCondition);
        mapsT.put("resultCode", CommonConstant.CODE_SUCCESS);
        mapsT.put("resultMsg", StringUtils.EMPTY);
        return mapsT;
    }

    /**
     * 新增大数据模型
     */
    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> saveBigDataModel(Long mktCamGrpRulId) {
        Map<String, Object> maps = new HashMap<>();
        //从大数据获取信息返回前台
        return maps;
    }

    /**
     * 获取目标分群条件信息
     */
    @Override
    public Map<String, Object> listTarGrpCondition(Long tarGrpId){
        Map<String, Object> maps = new HashMap<>();
        if (tarGrpId==null){
            maps.put("resultCode", CODE_FAIL);
            maps.put("resultMsg","");
            return maps;
        }
        List<TarGrpCondition> listTarGrpCondition = tarGrpConditionMapper.listTarGrpCondition(tarGrpId);
        List<TarGrpConditionVO> grpConditionList = new ArrayList<>();
        List<TarGrpVO> tarGrpVOS = new ArrayList<>();//传回前端展示信息
        for (TarGrpCondition tarGrpCondition : listTarGrpCondition) {
            List<OperatorDetail> operatorList = new ArrayList<>();
            TarGrpConditionVO tarGrpConditionVO = new TarGrpConditionVO();
            BeanUtil.copy(tarGrpCondition,tarGrpConditionVO);
            //塞入左参中文名
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(tarGrpConditionVO.getLeftParam()));
            if (label==null){
                continue;
            }
            List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(label.getInjectionLabelId());
            List<LabelValueVO> valueList = ChannelUtil.valueList2VOList(labelValues);
            tarGrpConditionVO.setLeftParamName(label.getInjectionLabelName());
            tarGrpConditionVO.setLabelCode(label.getInjectionLabelCode());
            //将操作符转为中文
            if (tarGrpConditionVO.getOperType()!=null && !tarGrpConditionVO.getOperType().equals("")){
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
            tarGrpConditionVO.setConditionType(label.getConditionType());
            tarGrpConditionVO.setValueList(valueList);
            tarGrpConditionVO.setOperatorList(operatorList);
            if ("PROM_LIST".equals(label.getInjectionLabelCode()) && tarGrpConditionVO.getRightParam()!=null){
                FilterRule filterRule = filterRuleMapper.selectByPrimaryKey(Long.valueOf(tarGrpConditionVO.getRightParam()));
                if (filterRule!=null){
                    tarGrpConditionVO.setPromListName(filterRule.getRuleName());
                }
            }
            grpConditionList.add(tarGrpConditionVO);
        }
        List<OrgTreeDO> sysAreaList = (List<OrgTreeDO>)redisUtils.get("AREA_RULE_ENTITY_"+tarGrpId);
        if (sysAreaList!=null){
            List<SysAreaVO> voList = new ArrayList<>();
            for (OrgTreeDO area : sysAreaList){
                SysAreaVO vo = BeanUtil.create(area,new SysAreaVO());
                voList.add(vo);
            }
            TarGrpConditionVO tarGrpConditionVO = new TarGrpConditionVO();
            tarGrpConditionVO.setSysAreaList(voList);
            grpConditionList.add(tarGrpConditionVO);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("listTarGrpCondition", grpConditionList);
        maps.put("conditionList",listTarGrpCondition);
        return maps;
    }

    /**
     * 获取大数据模型
     */
    @Override
    public Map<String, Object> listBigDataModel(Long mktCamGrpRulId) {
        //通过传入参数获取大数据模型返回前台 todo

        return null;
    }


}
