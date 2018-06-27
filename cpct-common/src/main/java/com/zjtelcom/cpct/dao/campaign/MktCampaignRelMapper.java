package com.zjtelcom.cpct.dao.campaign;




import com.zjtelcom.cpct.dto.campaign.MktCampaignRel;

import java.util.List;

public interface MktCampaignRelMapper {
    int deleteByPrimaryKey(Long mktCampaignRelId);

    int insert(MktCampaignRel record);

    MktCampaignRel selectByPrimaryKey(Long mktCampaignRelId);

    List<MktCampaignRel> selectAll();

    int updateByPrimaryKey(MktCampaignRel record);
}