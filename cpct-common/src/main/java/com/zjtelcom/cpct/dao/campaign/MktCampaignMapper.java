package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
import com.zjtelcom.cpct.request.campaign.QryMktCampaignListReq;
import org.apache.ibatis.annotations.Mapper;
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

    List<MktCampaign> qryMktCampaignListPage(MktCampaignDO mktCampaignDO);
}