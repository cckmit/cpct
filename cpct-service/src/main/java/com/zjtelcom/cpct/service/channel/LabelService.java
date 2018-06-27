package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelGrp;
import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.channel.LabelAddVO;
import com.zjtelcom.cpct.dto.channel.LabelVO;

import java.util.List;
import java.util.Map;

public interface LabelService {

    //标签
    RespInfo addLabel(Long userId, LabelAddVO addVO);

    RespInfo editLabel(Long userId, Label editVO);

    RespInfo deleteLabel(Long userId,Long labelId);

    List<LabelVO> getLabelList(Long userId, Map<String,Object> params, Integer page, Integer pageSize);

    LabelVO getLabelDetail(Long userId,Long scriptId);


    //标签组
    RespInfo addLabelGrp(Long userId, LabelGrp addVO);

    RespInfo editLabelGrp(Long userId, LabelGrp editVO);

    RespInfo deleteLabelGrp(Long userId,Long labelGrpId);

    List<LabelGrp> getLabelGrpList(Long userId, Map<String,Object> params, Integer page, Integer pageSize);

    LabelGrp getLabelGrpDetail(Long userId,Long labelGrpId);


    //标签组成员关系
    RespInfo addLabelGrpMbr(Long userId, LabelGrpMbr addVO);

    RespInfo editLabelGrpMbr(Long userId, Long grpMbrId,Long grpId);

    RespInfo deleteLabelGrpMbr(Long userId,Long labelGrpMbrId);

    LabelGrpMbr getLabelGrpMbrDetail(Long userId,Long labelGrpMbrId);


    //标签值规格（枚举类标签值规格）
    RespInfo addLabelValue(Long userId, LabelValue addVO);

    RespInfo editLabelValue(Long userId, LabelValue editVO);

    RespInfo deleteLabelValue(Long userId,Long labelValueId);

    List<LabelValue> getLabelValueList(Long userId, Map<String,Object> params, Integer page, Integer pageSize);

    LabelValue getLabelValueDetail(Long userId,Long labelValueId);





}
