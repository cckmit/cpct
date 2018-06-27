package com.zjtelcom.cpct.dao.strategy;

import com.zjtelcom.cpct.domain.campaign.MktStrategyConfRegionRelDO;

import java.util.List;

public interface MktStrategyConfRegionRelMapper {
    int deleteByPrimaryKey(Long mktStrategyConfRegionRelId);

    int deleteByMktStrategyConfId(Long mktStrategyConfId);

    int insert(MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO);

    MktStrategyConfRegionRelDO selectByPrimaryKey(Long mktStrategyConfRegionRelId);

    List<MktStrategyConfRegionRelDO> selectByMktStrategyConfId(Long mktStrategyConfId);

    List<MktStrategyConfRegionRelDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfRegionRelDO mktStrategyConfRegionRelDO);
}