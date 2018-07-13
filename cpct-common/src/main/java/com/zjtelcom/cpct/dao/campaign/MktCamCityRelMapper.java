package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamCityRelDO;

import java.util.List;

public interface MktCamCityRelMapper {
    int deleteByPrimaryKey(Long mktCamCityRelId);

    int insert(MktCamCityRelDO mktCamCityRelDO);

    MktCamCityRelDO selectByPrimaryKey(Long mktCamCityRelId);

    List<MktCamCityRelDO> selectByMktCampaignId(Long mktCampaignId);

    List<MktCamCityRelDO> selectAll();

    int updateByPrimaryKey(MktCamCityRelDO mktCamCityRelDO);
}