package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCampaignMapper {
    int deleteByPrimaryKey(Long mktCampaignId);

    int insert(MktCampaignDO mktCampaignDO);

    MktCampaignDO selectByPrimaryKey(Long mktCampaignId);

    List<MktCampaignDO> selectAll();

    int updateByPrimaryKey(MktCampaignDO mktCampaignDO);

    List<MktCampaignDO> qryMktCampaignListPage(MktCampaignDO mktCampaignDO);

    void changeMktCampaignStatus(@Param("mktCampaignId")Long mktCampaignId, @Param("statusCd")String statusCd);

    List<MktCampaignDO> qryMktCampaignListByCondition(MktCampaignDO mktCampaignDO);

}