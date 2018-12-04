package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCampaignRel;

import java.util.List;

public interface MktCampaignRelMapper {
    int deleteByPrimaryKey(Long mktCampaignRelId);

    int insert(MktCampaignRel record);

    MktCampaignRel selectByPrimaryKey(Long mktCampaignRelId);

    List<MktCampaignRel> selectAll();

    int updateByPrimaryKey(MktCampaignRel record);
}