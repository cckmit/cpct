package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.LabelGrp;
import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.dto.channel.LabelAddVO;
import com.zjtelcom.cpct.dto.channel.LabelEditVO;
import com.zjtelcom.cpct.dto.channel.LabelGrpParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface LabelService {

    Map<String,Object> importLabelValue(MultipartFile file) throws Exception;

    Map<String,Object> importLabel(MultipartFile file) throws Exception;
    //标签
    Map<String,Object> addLabel(Long userId, LabelAddVO addVO);

    Map<String,Object> shared(Long userId, Long labelId);


    Map<String,Object> editLabel(Long userId, LabelEditVO editVO);

    Map<String,Object> deleteLabel(Long userId,Long labelId);

    Map<String,Object> getLabelList(Long userId,String labelName,String labelCode,Integer scope,String conditionType,String fitDomain,Integer page, Integer pageSize);

    Map<String,Object> getLabelDetail(Long userId,Long scriptId);

    Map<String,Object> getLabelListByParam(Long userId, Map<String,Object> params);

    Map<String,Object> getLabelNameListByParam(Map<String,Object> params);


    //标签组
    Map<String,Object> addLabelGrp(Long userId, LabelGrp addVO);

    Map<String,Object> editLabelGrp(Long userId, LabelGrp editVO);

    Map<String,Object> deleteLabelGrp(Long userId,Long labelGrpId);

    Map<String,Object> getLabelGrpList(Long userId, Map<String,Object> params);

    Map<String,Object> getLabelGrpDetail(Long userId,Long labelGrpId);

    Map<String,Object> relateLabelGrp(LabelGrpParam param);



    //标签组成员关系
    Map<String,Object> addLabelGrpMbr(Long userId, LabelGrpMbr addVO);

    Map<String,Object> editLabelGrpMbr(Long userId, Long grpMbrId,Long grpId);

    Map<String,Object> deleteLabelGrpMbr(Long userId,Long labelGrpMbrId);

    Map<String,Object> getLabelGrpMbrDetail(Long userId,Long labelGrpMbrId);

    Map<String,Object> getLabelListByLabelGrp(Long userId,Long labelGrpId);




    //标签值规格（枚举类标签值规格）
    Map<String,Object> addLabelValue(Long userId, LabelValue addVO);

    Map<String,Object> editLabelValue(Long userId, LabelValue editVO);

    Map<String,Object> deleteLabelValue(Long userId,Long labelValueId);

    Map<String,Object> getLabelValueList(Long userId, Map<String,Object> params, Integer page, Integer pageSize);

    Map<String,Object> getLabelValueDetail(Long userId,Long labelValueId);









}
