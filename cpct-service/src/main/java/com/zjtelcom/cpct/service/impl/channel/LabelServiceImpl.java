package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.InjectionLabelGrpMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelGrpMbrMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.channel.LabelAddVO;
import com.zjtelcom.cpct.dto.channel.LabelVO;
import com.zjtelcom.cpct.dto.channel.LabelValueVO;
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


    @Override
    public Map<String,Object> addLabel(Long userId, LabelAddVO addVO) {
        Map<String,Object> result = new HashMap<>();
        Label label = BeanUtil.create(addVO,new Label());
        label.setCreateDate(new Date());
        label.setUpdateDate(new Date());
        label.setCreateStaff(userId);
        label.setUpdateStaff(userId);
        label.setStatusCd("1000");
        labelMapper.insert(label);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> editLabel(Long userId, Label editVO) {
        Map<String,Object> result = new HashMap<>();
        Label label = labelMapper.selectByPrimaryKey(editVO.getInjectionLabelId());
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
        result.put("resultData","添加成功");
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
        labelMapper.deleteByPrimaryKey(labelId);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> getLabelList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
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
        result.put("resultData",voList);
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
        result.put("resultData",vo);
        return result;
    }

    //标签组
    @Override
    public Map<String,Object> addLabelGrp(Long userId, LabelGrp addVO) {
        Map<String,Object> result = new HashMap<>();
        LabelGrp labelGrp = BeanUtil.create(addVO,new LabelGrp());
        labelGrp.setCreateDate(new Date());
        labelGrp.setUpdateDate(new Date());
        labelGrp.setCreateStaff(userId);
        labelGrp.setUpdateStaff(userId);
        labelGrp.setStatusCd("1000");
        labelGrpMapper.insert(labelGrp);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
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
        result.put("resultData","添加成功");
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
        result.put("resultData","添加成功");
        return result;
    }

    @Override
    public Map<String,Object> getLabelGrpList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        Map<String,Object> result = new HashMap<>();
        List<LabelGrp> grpList = new ArrayList<>();
        try {
            grpList = labelGrpMapper.selectAll();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelGrpList ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData",grpList);
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
        result.put("resultData",labelGrp);
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
        LabelGrpMbr grpMbr = BeanUtil.create(addVO,new LabelGrpMbr());
        labelGrpMbrMapper.insert(grpMbr);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultData","添加成功");
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
        result.put("resultData","添加成功");
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
        result.put("resultData","添加成功");
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
        result.put("resultData",grpMbr);
        return result;
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
        result.put("resultData","添加成功");
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
        result.put("resultData","添加成功");
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
        result.put("resultData","添加成功");
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
        result.put("resultData",labelValue);
        return result;
    }
}
