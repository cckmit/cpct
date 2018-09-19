package com.zjtelcom.cpct_prd.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamStrategyConfRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamStrategyConfRelPrdMapper {
    int deleteByPrimaryKey(Long camStrConfRelId);

    int deleteByStrategyConfId(Long strategyConfId);

    int deleteByMktCampaignId(Long mktCampaignId);

    int insert(MktCamStrategyConfRelDO mktCamStrategyConfRelDO);

    MktCamStrategyConfRelDO selectByPrimaryKey(Long camStrConfRelId);

    List<MktCamStrategyConfRelDO> selectByMktCampaignId(Long mktCampaignId);

    List<MktCamStrategyConfRelDO> selectAll();

    int updateByPrimaryKey(MktCamStrategyConfRelDO mktCamStrategyConfRelDO);
}