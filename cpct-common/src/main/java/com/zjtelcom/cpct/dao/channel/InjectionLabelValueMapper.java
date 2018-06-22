package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.LabelValue;

import java.util.List;

public interface InjectionLabelValueMapper {
    int deleteByPrimaryKey(Long labelValueId);

    int insert(LabelValue record);

    LabelValue selectByPrimaryKey(Long labelValueId);

    List<LabelValue> selectAll();

    int updateByPrimaryKey(LabelValue record);
}