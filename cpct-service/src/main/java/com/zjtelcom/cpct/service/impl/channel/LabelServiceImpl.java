package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.dao.channel.InjectionLabelGrpMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelGrpMbrMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.domain.channel.*;
import com.zjtelcom.cpct.dto.LabelAddVO;
import com.zjtelcom.cpct.dto.LabelVO;
import com.zjtelcom.cpct.dto.LabelValueVO;
import com.zjtelcom.cpct.dto.ScriptVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.LabelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public RespInfo addLabel(Long userId, LabelAddVO addVO) {
        Label label = BeanUtil.create(addVO,new Label());
        label.setCreateDate(new Date());
        label.setUpdateDate(new Date());
        label.setCreateStaff(userId);
        label.setUpdateStaff(userId);
        label.setStatusCd("1000");
        labelMapper.insert(label);
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    @Override
    public RespInfo editLabel(Long userId, Label editVO) {
        Label label = labelMapper.selectByPrimaryKey(editVO.getInjectionLabelId());
        if (label==null){
            return RespInfo.build(CODE_FAIL,"标签信息不存在");
        }
        BeanUtil.copy(editVO,label);
        label.setUpdateDate(new Date());
        label.setUpdateStaff(userId);
        labelMapper.updateByPrimaryKey(label);
        return RespInfo.build(CODE_SUCCESS,"修改成功");
    }

    @Override
    public RespInfo deleteLabel(Long userId, Long labelId) {
        Label label = labelMapper.selectByPrimaryKey(labelId);
        if (label==null){
            return RespInfo.build(CODE_FAIL,"标签信息不存在");
        }
        labelMapper.deleteByPrimaryKey(labelId);
        return RespInfo.build(CODE_SUCCESS,"删除成功");
    }

    @Override
    public List<LabelVO> getLabelList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        List<LabelVO> voList = new ArrayList<>();
        List<Label> labelList = new ArrayList<>();
        try {
            labelList = labelMapper.selectAll();
            for (Label label : labelList){
                LabelVO vo = ChannelUtil.map2LabelVO(label);
                voList.add(vo);
            }
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelList ", e);
        }
        return voList;
    }

    @Override
    public LabelVO getLabelDetail(Long userId, Long labelId) {
        LabelVO vo = new LabelVO();
        try {
            Label label = labelMapper.selectByPrimaryKey(labelId);
            vo = ChannelUtil.map2LabelVO(label);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelDetail ", e);
        }
        return vo;
    }

    //标签组
    @Override
    public RespInfo addLabelGrp(Long userId, LabelGrp addVO) {
        LabelGrp labelGrp = BeanUtil.create(addVO,new LabelGrp());
        labelGrp.setCreateDate(new Date());
        labelGrp.setUpdateDate(new Date());
        labelGrp.setCreateStaff(userId);
        labelGrp.setUpdateStaff(userId);
        labelGrp.setStatusCd("1000");
        labelGrpMapper.insert(labelGrp);
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    @Override
    public RespInfo editLabelGrp(Long userId, LabelGrp editVO) {
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(editVO.getGrpId());
        if (labelGrp==null){
            return RespInfo.build(CODE_FAIL,"标签组信息不存在");
        }
        BeanUtil.copy(editVO,labelGrp);
        labelGrp.setUpdateDate(new Date());
        labelGrp.setUpdateStaff(userId);
        labelGrpMapper.updateByPrimaryKey(labelGrp);
        return RespInfo.build(CODE_SUCCESS,"修改成功");
    }

    @Override
    public RespInfo deleteLabelGrp(Long userId, Long labelGrpId) {
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(labelGrpId);
        if (labelGrp==null){
            return RespInfo.build(CODE_FAIL,"标签组信息不存在");
        }
        //todo 存在关联关系的标签组 不能删除
        labelGrpMapper.deleteByPrimaryKey(labelGrpId);
        return RespInfo.build(CODE_SUCCESS,"删除成功");
    }

    @Override
    public List<LabelGrp> getLabelGrpList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        List<LabelGrp> grpList = new ArrayList<>();
        try {
            grpList = labelGrpMapper.selectAll();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelGrpList ", e);
        }
        return grpList;
    }

    @Override
    public LabelGrp getLabelGrpDetail(Long userId, Long labelGrpId) {
        LabelGrp labelGrp = new LabelGrp();
        try {
            labelGrp = labelGrpMapper.selectByPrimaryKey(labelGrpId);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelGrpDetail ", e);
        }
        return labelGrp;
    }

    //标签组成员关系表
    @Override
    public RespInfo addLabelGrpMbr(Long userId, LabelGrpMbr addVO) {
        Label label = labelMapper.selectByPrimaryKey(addVO.getInjectionLabelId());
        if (label==null){
            return RespInfo.build(CODE_FAIL,"标签信息不存在");
        }
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(addVO.getGrpId());
        if (labelGrp==null){
            return RespInfo.build(CODE_FAIL,"标签组信息不存在");
        }
        LabelGrpMbr grpMbr = BeanUtil.create(addVO,new LabelGrpMbr());
        labelGrpMbrMapper.insert(grpMbr);
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    @Override
    public RespInfo editLabelGrpMbr(Long userId, Long grpMbrId,Long grpId) {
        LabelGrpMbr labelGrpMbr = labelGrpMbrMapper.selectByPrimaryKey(grpMbrId);
        if (labelGrpMbr == null){
            return RespInfo.build(CODE_FAIL,"关联信息不存在");
        }
        LabelGrp labelGrp = labelGrpMapper.selectByPrimaryKey(grpId);
        if (labelGrp==null){
            return RespInfo.build(CODE_FAIL,"标签组信息不存在");
        }
        labelGrpMbr.setGrpId(grpId);
        labelGrpMbr.setUpdateDate(new Date());
        labelGrpMbr.setUpdateStaff(userId);
        labelGrpMbrMapper.updateByPrimaryKey(labelGrpMbr);
        return  RespInfo.build(CODE_SUCCESS,"修改成功");
    }

    @Override
    public RespInfo deleteLabelGrpMbr(Long userId, Long labelGrpMbrId) {
        LabelGrpMbr labelGrpMbr = labelGrpMbrMapper.selectByPrimaryKey(labelGrpMbrId);
        if (labelGrpMbr == null){
            return RespInfo.build(CODE_FAIL,"关联信息不存在");
        }
        labelGrpMbrMapper.deleteByPrimaryKey(labelGrpMbrId);
        return  RespInfo.build(CODE_SUCCESS,"删除成功");
    }

    @Override
    public LabelGrpMbr getLabelGrpMbrDetail(Long userId, Long labelGrpMbrId) {
        LabelGrpMbr grpMbr = new LabelGrpMbr();
        try {
            grpMbr = labelGrpMbrMapper.selectByPrimaryKey(labelGrpMbrId);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelGrpDetail ", e);
        }
        return grpMbr;
    }

    //标签值规格配置
    @Override
    public RespInfo addLabelValue(Long userId, LabelValue addVO) {
        LabelValue labelValue = BeanUtil.create(addVO,new LabelValue());
        labelValue.setCreateDate(new Date());
        labelValue.setUpdateDate(new Date());
        labelValue.setCreateStaff(userId);
        labelValue.setUpdateStaff(userId);
        labelValue.setStatusCd("1000");
        labelValueMapper.insert(labelValue);
        return RespInfo.build(CODE_SUCCESS,"添加成功");
    }

    @Override
    public RespInfo editLabelValue(Long userId, LabelValue editVO) {
        LabelValue labelValue = labelValueMapper.selectByPrimaryKey(editVO.getLabelValueId());
        if (labelValue==null){
            return RespInfo.build(CODE_FAIL,"标签值规格不存在");
        }
        BeanUtil.copy(editVO,labelValue);
        labelValue.setUpdateDate(new Date());
        labelValue.setUpdateStaff(userId);
        labelValueMapper.updateByPrimaryKey(labelValue);
        return RespInfo.build(CODE_SUCCESS,"修改成功");
    }

    @Override
    public RespInfo deleteLabelValue(Long userId, Long labelValueId) {
        LabelValue labelValue = labelValueMapper.selectByPrimaryKey(labelValueId);
        if (labelValue==null){
            return RespInfo.build(CODE_FAIL,"标签值规格不存在");
        }
        labelValueMapper.deleteByPrimaryKey(labelValueId);
        return RespInfo.build(CODE_SUCCESS,"删除成功");
    }

    @Override
    public List<LabelValue> getLabelValueList(Long userId, Map<String, Object> params, Integer page, Integer pageSize) {
        return null;
    }

    @Override
    public LabelValue getLabelValueDetail(Long userId, Long labelValueId) {
        LabelValue labelValue = new LabelValue();
        LabelValueVO vo = new LabelValueVO();
        try {
            labelValue = labelValueMapper.selectByPrimaryKey(labelValueId);
            vo = ChannelUtil.map2LabelValueVO(labelValue);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:LabelServiceImpl] fail to getLabelValueDetail ", e);
        }
        return labelValue;
    }
}
