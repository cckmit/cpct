package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCamRecomCalcRel;

import java.util.List;

public interface MktCamRecomCalcRelMapper {
    int deleteByPrimaryKey(Long evtRecomCalcRelId);

    int insert(MktCamRecomCalcRel record);

    MktCamRecomCalcRel selectByPrimaryKey(Long evtRecomCalcRelId);

    List<MktCamRecomCalcRel> selectAll();

    int updateByPrimaryKey(MktCamRecomCalcRel record);
}