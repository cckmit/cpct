package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.LabelGrpMbr;

import java.util.List;

public interface InjectionLabelGrpMbrMapper {
    int deleteByPrimaryKey(Long grpMbrId);

    int insert(LabelGrpMbr record);

    LabelGrpMbr selectByPrimaryKey(Long grpMbrId);

    List<LabelGrpMbr> selectAll();

    int updateByPrimaryKey(LabelGrpMbr record);
}