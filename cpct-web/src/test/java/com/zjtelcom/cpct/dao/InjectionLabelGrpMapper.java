package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.InjectionLabelGrp;
import java.util.List;

public interface InjectionLabelGrpMapper {
    int deleteByPrimaryKey(Long grpId);

    int insert(InjectionLabelGrp record);

    InjectionLabelGrp selectByPrimaryKey(Long grpId);

    List<InjectionLabelGrp> selectAll();

    int updateByPrimaryKey(InjectionLabelGrp record);
}