package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCamStrategyRel;
import java.util.List;

public interface MktCamStrategyRelMapper {
    int deleteByPrimaryKey(Long campStrRelId);

    int insert(MktCamStrategyRel record);

    MktCamStrategyRel selectByPrimaryKey(Long campStrRelId);

    List<MktCamStrategyRel> selectAll();

    int updateByPrimaryKey(MktCamStrategyRel record);
}