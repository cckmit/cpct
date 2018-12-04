package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCamChlConfAttr;

import java.util.List;

public interface MktCamChlConfAttrMapper {
    int deleteByPrimaryKey(Long contactChlAttrRstrId);

    int insert(MktCamChlConfAttr record);

    MktCamChlConfAttr selectByPrimaryKey(Long contactChlAttrRstrId);

    List<MktCamChlConfAttr> selectAll();

    int updateByPrimaryKey(MktCamChlConfAttr record);
}