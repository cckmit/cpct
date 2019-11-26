package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCampaignComplete;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCampaignCompleteMapper {
    int insert(MktCampaignComplete mktCampaignComplete);

    int update(MktCampaignComplete mktCampaignComplete);

    List<MktCampaignComplete> selectByCampaignId(Long mktCampaignId);

    MktCampaignComplete selectByCampaignIdAndTacheCd(Long mktCampaignId, String tacheCd);

    MktCampaignComplete selectByCampaignIdAndTacheCdAndTacheValueCd(Long mktCampaignId, String tacheCd, String tacheValueCd);

}
