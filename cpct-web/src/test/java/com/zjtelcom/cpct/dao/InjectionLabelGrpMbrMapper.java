package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.InjectionLabelGrpMbr;
import java.util.List;

public interface InjectionLabelGrpMbrMapper {
    int deleteByPrimaryKey(Long grpMbrId);

    int insert(InjectionLabelGrpMbr record);

    InjectionLabelGrpMbr selectByPrimaryKey(Long grpMbrId);

    List<InjectionLabelGrpMbr> selectAll();

    int updateByPrimaryKey(InjectionLabelGrpMbr record);
}