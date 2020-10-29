package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCampaignComplete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCampaignCompleteMapper {
    int insert(MktCampaignComplete mktCampaignComplete);

    int update(MktCampaignComplete mktCampaignComplete);

    List<MktCampaignComplete> selectByCampaignId(Long mktCampaignId);

    MktCampaignComplete selectByCampaignIdAndTacheCd(@Param("mktCampaignId") Long mktCampaignId, @Param("tacheCd") String tacheCd);

    MktCampaignComplete selectByCampaignIdAndTacheCdAndTacheValueCd(Long mktCampaignId, String tacheCd, String tacheValueCd);

}
