package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamRecomCalcRelDO;

import java.util.List;

public interface MktCamRecomCalcRelMapper {
    int deleteByPrimaryKey(Long evtRecomCalcRelId);

    int insert(MktCamRecomCalcRelDO record);

    MktCamRecomCalcRelDO selectByPrimaryKey(Long evtRecomCalcRelId);

    List<MktCamRecomCalcRelDO> selectAll();

    int updateByPrimaryKey(MktCamRecomCalcRelDO record);
}