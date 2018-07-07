package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCampaignRelMapper {
    int deleteByPrimaryKey(Long mktCampaignRelId);

    int insert(MktCampaignRelDO record);

    MktCampaignRelDO selectByPrimaryKey(Long mktCampaignRelId);

    List<MktCampaignRelDO> selectAll();

    int updateByPrimaryKey(MktCampaignRelDO record);
}