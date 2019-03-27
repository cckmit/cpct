package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.dao.strategy.MktStrategyConfRuleMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.domain.strategy.MktStrategyConfRuleDO;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.channel.MessageLabelInfo;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import com.zjtelcom.cpct.dubbo.service.SearchLabelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class SearchLabelServiceImpl implements SearchLabelService {
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


    @Override
    public Map<String, String> labelListByCampaignId(List<Long> campaignId) {
        Map<String,String> re = new HashMap<>();
        List<String> assetCode = new ArrayList<>();//2000
        List<String> promCode = new ArrayList<>();//3000
        List<String> custCode = new ArrayList<>();//1000

        List<Long> idlIst = new ArrayList<>();
        for (Long id : campaignId){
            MktCampaignDO campaign = campaignMapper.selectByPrimaryKey(id);
            if (campaign==null){
                continue;
            }
            //展示列标签
            DisplayColumn req = new DisplayColumn();
            req.setDisplayColumnId(campaign.getIsaleDisplay());
            Map<String, Object> labelMap = queryLabelListByDisplayId(req);
            List<LabelDTO> labelDTOList = (List<LabelDTO>) labelMap.get("labels");
            for (LabelDTO labelDTO : labelDTOList) {
                Label label = injectionLabelMapper.selectByLabelCode(labelDTO.getLabelCode());
                if (label!=null) {
                    if (idlIst.contains(label.getInjectionLabelId())) {
                        continue;
                    }
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
//        re.put("code",CODE_SUCCESS);
        re.put("assetLabels",ChannelUtil.StringList2String(assetCode));
        re.put("promLabels",ChannelUtil.StringList2String(promCode));
        re.put("custLabels",ChannelUtil.StringList2String(custCode));
        return re;
    }


    public Map<String, Object> queryLabelListByDisplayId(DisplayColumn req) {
        Map<String, Object> maps = new HashMap<>();
        List<DisplayColumnLabel> realList = displayColumnLabelMapper.findListByDisplayId(req.getDisplayColumnId());
        List<LabelDTO> labelList = new ArrayList<>();
        List<Long> messageTypes = new ArrayList<>();

        for (DisplayColumnLabel real : realList){
            Label label = injectionLabelMapper.selectByPrimaryKey(real.getInjectionLabelId());
            if (label==null){
                continue;
            }
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setInjectionLabelId(label.getInjectionLabelId());
            labelDTO.setInjectionLabelName(label.getInjectionLabelName());
            labelDTO.setMessageType(real.getMessageType());
            labelDTO.setLabelCode(label.getInjectionLabelCode());
            labelList.add(labelDTO);
            if (!messageTypes.contains(real.getMessageType())){
                messageTypes.add(real.getMessageType());
            }
        }
        List<MessageLabelInfo> mlInfoList = new ArrayList<>();
        for (int i = 0;i<messageTypes.size();i++){

            Long messageType = messageTypes.get(i);
            Message messages = messageMapper.selectByPrimaryKey(messageType);
            MessageLabelInfo info = BeanUtil.create(messages,new MessageLabelInfo());
            List<LabelDTO> dtoList = new ArrayList<>();
            for (LabelDTO dto : labelList){
                if (messageType.equals(dto.getMessageType())){
                    dtoList.add(dto);
                }
            }
            info.setLabelDTOList(dtoList);
            //判断是否选中
            if (dtoList.isEmpty()){
                info.setChecked("1");//false
            }else {
                info.setChecked("0");//true
            }
            mlInfoList.add(info);
        }
        maps.put("resultCode", CODE_SUCCESS);
        maps.put("resultMsg",mlInfoList);
        maps.put("labels",labelList);
        return maps;

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
