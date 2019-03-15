package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamRecomCalcRelDO;

import java.util.List;

public interface MktCamRecomCalcRelMapper {
    int deleteByPrimaryKey(Long evtRecomCalcRelId);

    int deleteByRuleId(Long mktStrategyConfRuleId);

    int insert(MktCamRecomCalcRelDO record);

    MktCamRecomCalcRelDO selectByPrimaryKey(Long evtRecomCalcRelId);

    MktCamRecomCalcRelDO selectByRuleId(Long mktStrategyConfRuleId);

    List<MktCamRecomCalcRelDO> selectAll();

    int updateByPrimaryKey(MktCamRecomCalcRelDO record);
}