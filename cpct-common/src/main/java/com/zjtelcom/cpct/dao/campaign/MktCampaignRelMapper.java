package com.zjtelcom.cpct.dao.campaign;




import com.zjtelcom.cpct.dto.campaign.MktCampaignRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCampaignRelMapper {
    int deleteByPrimaryKey(Long mktCampaignRelId);

    int insert(MktCampaignRel record);

    MktCampaignRel selectByPrimaryKey(Long mktCampaignRelId);

    List<MktCampaignRel> selectAll();

    int updateByPrimaryKey(MktCampaignRel record);
}