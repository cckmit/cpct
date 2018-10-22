package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCpcAlgorithmsRul;
import java.util.List;

public interface MktCpcAlgorithmsRulMapper {
    int deleteByPrimaryKey(Long algorithmsRulId);

    int insert(MktCpcAlgorithmsRul record);

    MktCpcAlgorithmsRul selectByPrimaryKey(Long algorithmsRulId);

    List<MktCpcAlgorithmsRul> selectAll();

    int updateByPrimaryKey(MktCpcAlgorithmsRul record);
}