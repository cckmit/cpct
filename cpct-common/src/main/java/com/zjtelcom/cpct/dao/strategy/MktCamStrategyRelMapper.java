package com.zjtelcom.cpct.dao.strategy;


import com.zjtelcom.cpct.pojo.MktCamStrategyRel;

import java.util.List;

public interface MktCamStrategyRelMapper {
    int deleteByPrimaryKey(Long campStrRelId);

    int insert(MktCamStrategyRel record);

    MktCamStrategyRel selectByPrimaryKey(Long campStrRelId);

    List<MktCamStrategyRel> selectAll();

    int updateByPrimaryKey(MktCamStrategyRel record);
}