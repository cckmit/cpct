package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCampaignRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCampaignRelMapper {
    int deleteByPrimaryKey(Long mktCampaignRelId);

    int deleteByAmktCampaignId(Long mktCampaignRelId);

    int insert(MktCampaignRelDO mktCampaignRelDO);

    MktCampaignRelDO selectByPrimaryKey(Long mktCampaignRelId);

    List<MktCampaignRelDO> selectByAmktCampaignId(@Param("aMktCampaignId") Long aMktCampaignId, @Param("statusCd")String statusCd);

    List<MktCampaignRelDO> selectByZmktCampaignId(@Param("zMktCampaignId") Long zMktCampaignId, @Param("statusCd")String statusCd);

    int selectCountByAmktCampaignId(@Param("aMktCampaignId") Long aMktCampaignId, @Param("statusCd")String statusCd);

    List<MktCampaignRelDO> selectAll();

    int updateByPrimaryKey(MktCampaignRelDO mktCampaignRelDO);
}