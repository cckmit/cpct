package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCamGrpRul;
import java.util.List;

public interface MktCamGrpRulMapper {
    int deleteByPrimaryKey(Long mktCamGrpRulId);

    int insert(MktCamGrpRul record);

    MktCamGrpRul selectByPrimaryKey(Long mktCamGrpRulId);

    List<MktCamGrpRul> selectAll();

    int updateByPrimaryKey(MktCamGrpRul record);
}