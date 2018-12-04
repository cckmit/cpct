package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCampaign;

import java.util.List;

public interface MktCampaignMapper {
    int deleteByPrimaryKey(Long mktCampaignId);

    int insert(MktCampaign record);

    MktCampaign selectByPrimaryKey(Long mktCampaignId);

    List<MktCampaign> selectAll();

    int updateByPrimaryKey(MktCampaign record);
}