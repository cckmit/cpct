package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.InjectionLabel;

import java.util.List;

public interface InjectionLabelMapper {
    int deleteByPrimaryKey(Long injectionLabelId);

    int insert(InjectionLabel record);

    InjectionLabel selectByPrimaryKey(Long injectionLabelId);

    List<InjectionLabel> selectAll();

    int updateByPrimaryKey(InjectionLabel record);
}