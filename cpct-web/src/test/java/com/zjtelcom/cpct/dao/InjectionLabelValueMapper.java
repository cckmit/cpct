package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.InjectionLabelValue;
import java.util.List;

public interface InjectionLabelValueMapper {
    int deleteByPrimaryKey(Long labelValueId);

    int insert(InjectionLabelValue record);

    InjectionLabelValue selectByPrimaryKey(Long labelValueId);

    List<InjectionLabelValue> selectAll();

    int updateByPrimaryKey(InjectionLabelValue record);
}