package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.campaign.MktCamChlConfAttrMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamDisplayColumnRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.CamScript;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.MktVerbal;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.enums.ConfAttrEnum;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.SearchLabelService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SearchLabelServiceImpl extends BaseService implements SearchLabelService {
    @Autowired
    private TarGrpConditionMapper tarGrpConditionMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private MktStrategyConfRuleMapper ruleMapper;
    @Autowired
    private MktCamScriptMapper camScriptMapper;
    @Autowired
    private MktVerbalMapper verbalMapper;
    @Autowired
    private MktCampaignMapper campaignMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private DisplayColumnLabelMapper displayColumnLabelMapper;
    @Autowired
    private FilterRuleMapper filterRuleMapper;
    @Autowired
    private MktCamEvtRelMapper evtRelMapper;
    @Autowired
    private MktCamChlConfAttrMapper confAttrMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private MktCamDisplayColumnRelMapper mktCamDisplayColumnRelMapper;
    @Autowired
    private MktVerbalConditionMapper verbalConditionMapper;


    @Override
    public Map<String, String> labelListByCampaignId(List<Long> campaignId) {
        Map<String,String> re = new HashMap<>();
        List<String> assetCode = new ArrayList<>();//2000
        List<String> promCode = new ArrayList<>();//3000
        List<String> custCode = new ArrayList<>();//1000

        try {
            List<Long> idlIst = new ArrayList<>();
            for (Long id : campaignId){

                MktCampaignDO campaign = (MktCampaignDO) redisUtils.get("MKT_CAMPAIGN_" + id);
                if (campaign == null) {
                    campaign = campaignMapper.selectByPrimaryKey(id);
                    redisUtils.set("MKT_CAMPAIGN_" + id, campaign);
                }
                if (campaign==null){
                    continue;
                }
                //展示列
                List<LabelDTO> labelDTOList = (List<LabelDTO>) redisUtils.get("CAM_LABEL_DTO_LIST" + id);
                if (labelDTOList == null) {
                    labelDTOList = mktCamDisplayColumnRelMapper.selectLabelDisplayListByCamId(campaign.getMktCampaignId());
                    redisUtils.set("CAM_LABEL_DTO_LIST" + id, labelDTOList);
                }
                for (LabelDTO labelDTO : labelDTOList) {
                    Label label = null;
                    if (redisUtils.get("LABEL_LIB_"+labelDTO.getInjectionLabelId())!=null){
                        label = (Label) redisUtils.get("LABEL_LIB_"+labelDTO.getInjectionLabelId());
                    }else {
                       label = injectionLabelMapper.selectByLabelCode(labelDTO.getLabelCode());
                        redisUtils.set("LABEL_LIB_"+label.getInjectionLabelId(),label);
                    }
                    if (label!=null) {
                        if (idlIst.contains(label.getInjectionLabelId())) {
                            continue;
                        }
                        idlIst.add(label.getInjectionLabelId());
                        codeList(assetCode, promCode, custCode, label);
                    }
                }
                //过滤规则标签
                List<FilterRule> filterRules = (List<FilterRule>)redisUtils.get("CAM_FILTER_LIST_"+id);
                if (filterRules==null){
                    filterRules = filterRuleMapper.selectFilterRuleList(Long.valueOf(id.toString()));
                    redisUtils.set("CAM_FILTER_LIST_"+id,filterRules);
                }
                for (FilterRule filterRule : filterRules){
                    if ("6000".equals(filterRule.getFilterType()) && filterRule.getConditionId()!=null){
                        Label label = null;
                        MktVerbalCondition condition = verbalConditionMapper.selectByPrimaryKey(filterRule.getConditionId());
                        if (condition!=null){
                            if (redisUtils.get("LABEL_LIB_"+filterRule.getConditionId())!=null){
                                label = (Label) redisUtils.get("LABEL_LIB_"+filterRule.getConditionId());
                            }else {
                                label = injectionLabelMapper.selectByPrimaryKey(filterRule.getConditionId());
                                if (label!=null){
                                    redisUtils.set("LABEL_LIB_"+label.getInjectionLabelId(),label);
                                }else {
                                    System.out.println("conditionId:"+filterRule.getRuleId()+"label:"+filterRule.getConditionId());
                                }
                            }
                        }
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
                List<MktStrategyConfRuleDO> ruleList = (List<MktStrategyConfRuleDO>)redisUtils.get("CAM_RULE_LIST_"+id);
                if (ruleList==null){
                    ruleList = ruleMapper.selectByCampaignId(Long.valueOf(id.toString()));
                    redisUtils.set("CAM_RULE_LIST_"+id,ruleList);
                }
                for (MktStrategyConfRuleDO rule : ruleList) {
                    if (rule.getTarGrpId() == null) {
                        continue;
                    }
                    if (rule.getEvtContactConfId()!=null && !rule.getEvtContactConfId().equals("")){
                        String[] confList = rule.getEvtContactConfId().split("/");
                        //推荐指引标签
                        for (String confId : confList){
                            CamScript camScript = null;
                            if (redisUtils.get("CAM_SCRIPT_"+confId)!=null){
                                camScript = (CamScript) redisUtils.get("CAM_SCRIPT_"+confId);
                            }else {
                                camScript =  camScriptMapper.selectByConfId(Long.valueOf(confId));
                                redisUtils.set("CAM_SCRIPT_"+confId,camScript);
                            }
                            if (camScript!=null && camScript.getScriptDesc()!=null){
                                List<String> labelSc = subScript(camScript.getScriptDesc());
                                for (String code : labelSc){
                                    Label label = null;
                                    if (redisUtils.get("LABEL_LIB_CODE"+code)!=null){
                                        label = (Label) redisUtils.get("LABEL_LIB_CODE"+code);
                                    }else {
                                        label = injectionLabelMapper.selectByLabelCode(code);
                                        redisUtils.set("LABEL_LIB_CODE"+code,label);
                                    }
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
                            List<MktVerbal> verbalList = (List<MktVerbal>)redisUtils.get("CAM_VERBAL_LIST_"+confId);
                            if (verbalList==null){
                                verbalList = verbalMapper.findVerbalListByConfId(Long.valueOf(confId));
                                redisUtils.set("CAM_VERBAL_LIST_"+confId,verbalList);
                            }
                            for (MktVerbal verbal : verbalList){
                                List<String> labelSc = subScript(verbal.getScriptDesc());
                                for (String code : labelSc){
                                    Label label = null;
                                    if (redisUtils.get("LABEL_LIB_CODE"+code)!=null){
                                        label = (Label) redisUtils.get("LABEL_LIB_CODE"+code);
                                    }else {
                                        label = injectionLabelMapper.selectByLabelCode(code);
                                        redisUtils.set("LABEL_LIB_CODE"+code,label);
                                    }
                                    if (label!=null){
                                        if (idlIst.contains(label.getInjectionLabelId())){
                                            continue;
                                        }
                                        idlIst.add(label.getInjectionLabelId());
                                        codeList(assetCode, promCode, custCode, label);
                                    }
                                }
                            }
                            List<MktCamChlConfAttrDO> confAttrDOList = (List<MktCamChlConfAttrDO>)redisUtils.get("CAM_CONF_ATTR_LIST_"+confId);
                            if (confAttrDOList==null){
                                confAttrDOList = confAttrMapper.selectByEvtContactConfId(Long.valueOf(confId));
                                redisUtils.set("CAM_CONF_ATTR_LIST_"+confId,confAttrDOList);
                            }
                            for (MktCamChlConfAttrDO confAttrDO : confAttrDOList){
                                if (ConfAttrEnum.ACCOUNT.getArrId().equals(confAttrDO.getAttrId())){
                                    Label label = null;
                                    if (redisUtils.get("LABEL_LIB_CODE"+confAttrDO.getAttrValue())!=null){
                                        label = (Label) redisUtils.get("LABEL_LIB_CODE"+confAttrDO.getAttrValue());
                                    }else {
                                        label = injectionLabelMapper.selectByLabelCode(confAttrDO.getAttrValue());
                                        redisUtils.set("LABEL_LIB_CODE"+confAttrDO.getAttrValue(),label);
                                    }
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
                    List<TarGrpCondition> conditionList = (List<TarGrpCondition>)redisUtils.get("CAM_TAR_CONDITION_LIST_"+rule.getTarGrpId());
                    if (conditionList==null){
                        conditionList = tarGrpConditionMapper.listTarGrpCondition(rule.getTarGrpId());
                        redisUtils.set("CAM_TAR_CONDITION_LIST_"+rule.getTarGrpId(),conditionList);
                    }

                    for (TarGrpCondition condition : conditionList) {
                        Label label = null;
                        if (redisUtils.get("LABEL_LIB_"+condition.getLeftParam())!=null){
                            label = (Label) redisUtils.get("LABEL_LIB_"+condition.getLeftParam());
                        }else {
                            label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(condition.getLeftParam()));
                            redisUtils.set("LABEL_LIB_"+condition.getLeftParam(),label);
                        }
                        if (label != null) {
                            if (idlIst.contains(label.getInjectionLabelId())) {
                                continue;
                            }
                            idlIst.add(label.getInjectionLabelId());
                            codeList(assetCode, promCode, custCode, label);
                        }
                    }

                }
            }
            if (!promCode.isEmpty() && !assetCode.contains("PROM_INTEG_ID")){
                assetCode.add("PROM_INTEG_ID");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            logger.error("************所有活动标签查询异常*************");
        }
//        re.put("code",CODE_SUCCESS);
        re.put("assetLabels",ChannelUtil.StringList2String(assetCode));
        re.put("promLabels",ChannelUtil.StringList2String(promCode));
        re.put("custLabels",ChannelUtil.StringList2String(custCode));
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

    @Override
    public Map<String, String> labelListByEventId(Long eventId) {
        List<Map<String,Object>> campaignDOS = evtRelMapper.listActivityByEventId(eventId);
        List<Long> campaigns = new ArrayList<>();
        if (campaignDOS!=null && !campaignDOS.isEmpty()){
            for (Map<String,Object> cam : campaignDOS){
                if (cam.get("mktCampaginId")!=null){
                    campaigns.add(Long.valueOf(cam.get("mktCampaginId").toString()));
                }
            }
        }
        return labelListByCampaignId(campaigns);
    }
}
