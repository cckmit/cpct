package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.*;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.channel.*;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class LabelServiceImpl extends BaseService implements LabelService {

    @Autowired
    private InjectionLabelMapper labelMapper;
    @Autowired
    private InjectionLabelValueMapper labelValueMapper;
    @Autowired
    private InjectionLabelGrpMapper labelGrpMapper;
    @Autowired
    private InjectionLabelGrpMbrMapper labelGrpMbrMapper;

    /**
     *共享
     * @param userId
     * @param labelId
     * @return
     */
    @Override
    public Map<String, Object> shared(Long userId, Long labelId) {
        Map<String,Object> result = new HashMap<>();
        Label label = labelMapper.selectByPrimaryKey(labelId);
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签信息不存在");
            return result;
        }
//        if (label.getIsShared().equals()){
//
//        }
        label.setUpdateDate(new Date());
        label.setUpdateStaff(userId);
        labelMapper.updateByPrimaryKey(label);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String, Object> unshared(Long userId, Long labelId) {
        return null;
    }

    @Override
    public Map<String, Object> getLabelListByParam(Long userId, Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        List<LabelVO> voList = new ArrayList<>();
        List<Label> labelList = new ArrayList<>();
        try {
            String labelName = null;
            String fitDomain = null;
            if (params.get("labelName")!=null){
                labelName = params.get("labelName").toString();
            }
            if (params.get("fitDomain")!=null){
                fitDomain = params.get("fitDomain").toString();
            }
            labelList = labelMapper.findByParam(labelName,fitDomain);
            for (Label label : labelList){
                LabelVO vo = ChannelUtil.map2LabelVO(label);
                voList.add(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelList ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        return result;
    }

    @Override
    public Map<String,Object> addLabel(Long userId, LabelAddVO addVO) {
        Map<String,Object> result = new HashMap<>();
//        List<FpcMTrigger> triggerList = triggerMapper.selectAll();
//        for (FpcMTrigger trigger : triggerList){
//
//            LabelAddVO addVO1 = new LabelAddVO();
//            addVO1.setInjectionLabelCode(trigger.getLeftOperand());
//            addVO1.setInjectionLabelDesc(trigger.getDescription());
//            addVO1.setInjectionLabelName(trigger.getConditionName());
//            addVO1.setLabelType("1000");
//            addVO1.setConditionType(trigger.getConditonType());
//            addVO1.setScope(trigger.getScope());
//            addVO1.setOperator(trigger.getOperator());
//            addVO1.setRightOperand(trigger.getRightOperand());
//            if (trigger.getValueId()!=null && !trigger.getValueId().equals("")){
//                addVO1.setLabelValueType("2000");
//            }else {
//                addVO1.setLabelValueType("1000");
//            }
//            if (trigger.getField()!=null){
//                switch(trigger.getField()){
//                    case "YD":
//                        addVO1.setFitDomain("1");
//                        break;
//                    case "KD":
//                        addVO1.setFitDomain("2");
//                        break;
//                    case "GH":
//                        addVO1.setFitDomain("3");
//                        break;
//                    case "ITV":
//                        addVO1.setFitDomain("4");
//                        break;
//                }
//            }
//        }
        Label labelValodate = labelMapper.selectByLabelCode(addVO.getInjectionLabelCode());
        if (labelValodate!=null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签已存在");
            return result;
        }
        Label label = BeanUtil.create(addVO,new Label());
        List<Integer> opValueList = new ArrayList<>();
        for (String st : addVO.getOperatorList()){
            Operator op = Operator.getOperator(st);
            if (op!=null){
                opValueList.add(op.getValue());
            }
        }
        label.setOperator(ChannelUtil.List2String(opValueList));
        label.setScope(0);
        label.setLabelType("1000");
        //todo 系统添加待确认
        label.setLabelDataType("1000");
        label.setLabelValueType("1000");

        label.setCreateDate(new Date());
        label.setUpdateDate(new Date());
        label.setCreateStaff(userId);
        label.setUpdateStaff(userId);
        label.setStatusCd("1000");
        labelMapper.insert(label);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> editLabel(Long userId, LabelEditVO editVO) {
        Map<String,Object> result = new HashMap<>();
        Label label = labelMapper.selectByPrimaryKey(editVO.getLabelId());
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签信息不存在");
            return result;
        }
        BeanUtil.copy(editVO,label);
        label.setUpdateDate(new Date());
        label.setUpdateStaff(userId);
        labelMapper.updateByPrimaryKey(label);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> deleteLabel(Long userId, Long labelId) {
        Map<String,Object> result = new HashMap<>();
        Label label = labelMapper.selectByPrimaryKey(labelId);
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签信息不存在");
            return result;
        }
        if (label.getScope().equals(1)){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","大数据标签不能删除");
            return result;
        }
        labelMapper.deleteByPrimaryKey(labelId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> getLabelList(Long userId, String labelName, String labelCode, Integer scope, String conditionType, String fitDomain, Integer page, Integer pageSize) {
        Map<String,Object> result = new HashMap<>();
        List<LabelVO> voList = new ArrayList<>();
        List<Label> labelList = new ArrayList<>();
            PageHelper.startPage(page,pageSize);
            labelList = labelMapper.findLabelList(labelName,fitDomain,labelCode,scope,conditionType);
            Page pageInfo = new Page(new PageInfo(labelList));
            for (Label label : labelList){
                LabelVO vo = ChannelUtil.map2LabelVO(label);
                voList.add(vo);
            }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        result.put("page",pageInfo);
        return result;
    }

    @Override
    public Map<String,Object> getLabelDetail(Long userId, Long labelId) {
        Map<String,Object> result = new HashMap<>();
        LabelVO vo = new LabelVO();
        try {
            Label label = labelMapper.selectByPrimaryKey(labelId);
            vo = ChannelUtil.map2LabelVO(label);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelDetail ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",vo);
        return result;
    }


    //标签组
    @Override
    public Map<String,Object> addLabelGrp(Long userId, LabelGrp addVO) {
        Map<String,Object> result = new HashMap<>();
        LabelGrp grp = labelGrpMapper.findByGrpName(addVO.getGrpName());
        if (grp!=null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","已存在同名标签组");
            return result;
        }
        LabelGrp labelGrp = BeanUtil.create(addVO,new LabelGrp());
        labelGrp.setCreateDate(new Date());
        labelGrp.setUpdateDate(new Date());
        labelGrp.setCreateStaff(userId);
        labelGrp.setUpdateStaff(userId);
        labelGrp.setStatusCd("1000");
        labelGrpMapper.insert(labelGrp);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> editLabelGrp(Long userId, LabelGrp editVO) {
        Map<String,Object> result = new HashMap<>();
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(editVO.getGrpId());
        if (labelGrp==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签组信息不存在");
            return result;
        }
        BeanUtil.copy(editVO,labelGrp);
        labelGrp.setUpdateDate(new Date());
        labelGrp.setUpdateStaff(userId);
        labelGrpMapper.updateByPrimaryKey(labelGrp);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> deleteLabelGrp(Long userId, Long labelGrpId) {
        Map<String,Object> result = new HashMap<>();
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(labelGrpId);
        if (labelGrp==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签组信息不存在");
            return result;
        }
        //todo 存在关联关系的标签组 不能删除
        labelGrpMapper.deleteByPrimaryKey(labelGrpId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> getLabelGrpList(Long userId, Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        List<LabelGrp> grpList = new ArrayList<>();
        List<LabelGrpVO> voList = new ArrayList<>();
        try {
            String grpName = null;
            if (params.get("grpName")!=null){
                grpName = params.get("grpName").toString();
            }
            grpList = labelGrpMapper.findByParams(grpName);
            for (LabelGrp grp : grpList){
                LabelGrpVO vo = BeanUtil.create(grp,new LabelGrpVO());
                List<LabelVO> labelVOList = getLabelVOList(grp.getGrpId());
                vo.setLabelList(labelVOList);
                voList.add(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelGrpList ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",voList);
        return result;
    }

    @Override
    public Map<String,Object> getLabelGrpDetail(Long userId, Long labelGrpId) {
        Map<String,Object> result = new HashMap<>();
        LabelGrp labelGrp = new LabelGrp();
        try {
            labelGrp = labelGrpMapper.selectByPrimaryKey(labelGrpId);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelGrpDetail ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",labelGrp);
        return result;
    }

    //标签组成员关系表
    @Override
    public Map<String,Object> addLabelGrpMbr(Long userId, LabelGrpMbr addVO) {
        Map<String,Object> result = new HashMap<>();
        Label label = labelMapper.selectByPrimaryKey(addVO.getInjectionLabelId());
        if (label==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签信息不存在");
            return result;
        }
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(addVO.getGrpId());
        if (labelGrp==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签组信息不存在");
            return result;
        }
        LabelGrpMbr labelGrpMbr = labelGrpMbrMapper.selectByLabelIdAndGrpId(addVO.getInjectionLabelId(),addVO.getGrpId());
        if (labelGrpMbr!=null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签已关联该标签组");
            return result;
        }
        LabelGrpMbr grpMbr = BeanUtil.create(addVO,new LabelGrpMbr());
        labelGrpMbrMapper.insert(grpMbr);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
}

    @Override
    public Map<String,Object> editLabelGrpMbr(Long userId, Long grpMbrId,Long grpId) {
        Map<String,Object> result = new HashMap<>();
        LabelGrpMbr labelGrpMbr = labelGrpMbrMapper.selectByPrimaryKey(grpMbrId);
        if (labelGrpMbr == null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","关联信息不存在");
            return result;
        }
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(grpId);
        if (labelGrp==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签组信息不存在");
            return result;
        }
        labelGrpMbr.setGrpId(grpId);
        labelGrpMbr.setUpdateDate(new Date());
        labelGrpMbr.setUpdateStaff(userId);
        labelGrpMbrMapper.updateByPrimaryKey(labelGrpMbr);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> deleteLabelGrpMbr(Long userId, Long labelGrpMbrId) {
        Map<String,Object> result = new HashMap<>();
        LabelGrpMbr labelGrpMbr = labelGrpMbrMapper.selectByPrimaryKey(labelGrpMbrId);
        if (labelGrpMbr == null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","关联信息不存在");
            return result;
        }
        labelGrpMbrMapper.deleteByPrimaryKey(labelGrpMbrId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> getLabelGrpMbrDetail(Long userId, Long labelGrpMbrId) {
        Map<String,Object> result = new HashMap<>();
        LabelGrpMbr grpMbr = new LabelGrpMbr();
        try {
            grpMbr = labelGrpMbrMapper.selectByPrimaryKey(labelGrpMbrId);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelGrpDetail ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",grpMbr);
        return result;
    }

    @Override
    public Map<String, Object> getLabelListByLabelGrp(Long userId, Long labelGrpId) {
        Map<String,Object> result = new HashMap<>();
        if (labelGrpId==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","请选择标签组");
            return result;
        }
        List<LabelVO> labelVOList = getLabelVOList(labelGrpId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",labelVOList);
        return result;
    }

    private List<LabelVO> getLabelVOList(Long labelGrpId) {
        List<LabelGrpMbr> lgmList = labelGrpMbrMapper.findListByGrpId(labelGrpId);
        List<LabelVO> labelVOList = new ArrayList<>();
        for (LabelGrpMbr grpMbr : lgmList){
            Label label = labelMapper.selectByPrimaryKey(grpMbr.getInjectionLabelId());
            if (label!=null){
                LabelVO labelVO = ChannelUtil.map2LabelVO(label);
                labelVOList.add(labelVO);
            }
        }
        return labelVOList;
    }

    //标签值规格配置
    @Override
    public Map<String,Object> addLabelValue(Long userId, LabelValue addVO) {
        Map<String,Object> result = new HashMap<>();
        LabelValue labelValue = BeanUtil.create(addVO,new LabelValue());
        labelValue.setCreateDate(new Date());
        labelValue.setUpdateDate(new Date());
        labelValue.setCreateStaff(userId);
        labelValue.setUpdateStaff(userId);
        labelValue.setStatusCd("1000");
        labelValueMapper.insert(labelValue);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> editLabelValue(Long userId, LabelValue editVO) {
        Map<String,Object> result = new HashMap<>();
        LabelValue labelValue = labelValueMapper.selectByPrimaryKey(editVO.getLabelValueId());
        if (labelValue==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签值规格不存在");
            return result;
        }
        BeanUtil.copy(editVO,labelValue);
        labelValue.setUpdateDate(new Date());
        labelValue.setUpdateStaff(userId);
        labelValueMapper.updateByPrimaryKey(labelValue);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> deleteLabelValue(Long userId, Long labelValueId) {
        Map<String,Object> result = new HashMap<>();
        LabelValue labelValue = labelValueMapper.selectByPrimaryKey(labelValueId);
        if (labelValue==null){
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","标签值规格不存在");
            return result;
        }
        labelValueMapper.deleteByPrimaryKey(labelValueId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> getLabelValueList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public Map<String,Object> getLabelValueDetail(Long userId, Long labelValueId) {
        Map<String,Object> result = new HashMap<>();
        LabelValue labelValue = new LabelValue();
        LabelValueVO vo = new LabelValueVO();
        try {
            labelValue = labelValueMapper.selectByPrimaryKey(labelValueId);
            vo = ChannelUtil.map2LabelValueVO(labelValue);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelValueDetail ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",labelValue);
        return result;
    }
}
