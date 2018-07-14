package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;

import java.util.List;

public interface MktCamStrategyConfRelMapper {
    int deleteByPrimaryKey(Long camStrConfRelId);

    int deleteByStrategyConfId(Long strategyConfId);

    int deleteByMktCampaignId(Long mktCampaignId);

    int insert(MktCamStrategyConfRelDO mktCamStrategyConfRelDO);

    MktCamStrategyConfRelDO selectByPrimaryKey(Long camStrConfRelId);

    List<MktCamStrategyConfRelDO> selectByMktCampaignId(Long mktCampaignId);

    List<MktCamStrategyConfRelDO> selectAll();

    int updateByPrimaryKey(MktCamStrategyConfRelDO mktCamStrategyConfRelDO);
}