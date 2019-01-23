package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.dao.grouping.TrialOperationMapper;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.DisplayColumn;
import com.zjtelcom.cpct.domain.channel.DisplayColumnLabel;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.Message;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import com.zjtelcom.cpct.dto.channel.MessageLabelInfo;
import com.zjtelcom.cpct.dubbo.service.TrialRedisService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class TrialRedisServiceImpl implements TrialRedisService {
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TrialOperationMapper trialOperationMapper;
    @Autowired
    private MktCampaignMapper campaignMapper;

    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private DisplayColumnLabelMapper displayColumnLabelMapper;



    @Override
    public Map<String, Object> searchFromRedis(String key) {
        Map<String,Object> result = new HashMap<>();
        try {
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","查询成功");
            if (key.contains("ISSURE_")){
                if (redisUtils.hgetAllRedisList(key) == null){
                    String keyString = key.substring(7,key.length());
                    String[] keyList = keyString.split("_");
                    String batchNum = keyList[0];
                    String ruleId = keyList[1];

                }
                result.put("result",redisUtils.hgetAllRedisList(key));
            }else if (key.contains("LABEL_DETAIL_")){
                if (redisUtils.get(key)==null){
                    String  batchNum = key.substring(13,key.length());
                    TrialOperation operation = trialOperationMapper.selectByBatchNum(batchNum);
                    if (operation==null){
                        result.put("resultCode",CODE_SUCCESS);
                        result.put("resultMsg","查询失败");
                        return result;
                    }
                    // 通过活动id获取关联的标签字段数组
                    MktCampaignDO campaignDO = campaignMapper.selectByPrimaryKey(operation.getCampaignId());
                    if (campaignDO==null){
                        result.put("resultCode", CODE_FAIL);
                        result.put("resultMsg", "活动不存在");
                        return result;
                    }
                    // 通过活动id获取关联的标签字段数组
                    DisplayColumn req = new DisplayColumn();
                    req.setDisplayColumnId(campaignDO.getCalcDisplay());
                    Map<String,Object> labelMap = queryLabelListByDisplayId(req);
                    List<LabelDTO> labelDTOList = (List<LabelDTO>)labelMap.get("labels");
                    String[] fieldList = new String[labelDTOList.size()];
                    List<Map<String,Object>> labelList = new ArrayList<>();
                    for (int i = 0 ; i< labelDTOList.size();i++){
                        fieldList[i] = labelDTOList.get(i).getLabelCode();
                        Map<String,Object> label = new HashMap<>();
                        label.put("code",labelDTOList.get(i).getLabelCode());
                        label.put("name",labelDTOList.get(i).getInjectionLabelName());
                        labelList.add(label);
                    }
                    redisUtils.set("LABEL_DETAIL_"+operation.getBatchNum(),labelList);
                }
                result.put("result",redisUtils.get(key));
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","查询失败");
        }
        return result;
    }




    private Map<String, Object> queryLabelListByDisplayId(DisplayColumn req) {
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

}
