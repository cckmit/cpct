package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.LabelSaturation;

import java.util.List;

public interface LabelSaturationMapper {
    int deleteByPrimaryKey(Long labelSaturationId);

    int insert(LabelSaturation record);

    LabelSaturation selectByPrimaryKey(Long labelSaturationId);

    List<LabelSaturation> selectAll();

    int updateByPrimaryKey(LabelSaturation record);
}