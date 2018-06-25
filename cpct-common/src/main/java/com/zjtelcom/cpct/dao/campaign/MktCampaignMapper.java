package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.DTO.MktCampaign;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCampaignMapper {
    int deleteByPrimaryKey(Long mktCampaignId);

    int insert(MktCampaign record);

    MktCampaign selectByPrimaryKey(Long mktCampaignId);

    List<MktCampaign> selectAll();

    int updateByPrimaryKey(MktCampaign record);
}