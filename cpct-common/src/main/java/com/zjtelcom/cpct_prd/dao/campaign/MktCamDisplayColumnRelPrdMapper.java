package com.zjtelcom.cpct_prd.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamDisplayColumnRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamDisplayColumnRelPrdMapper {

    List<MktCamDisplayColumnRel> selectAll();

    MktCamDisplayColumnRel selectByPrimaryKey(Long mktCamDisplayColumnRelId);

    int insert(MktCamDisplayColumnRel mktCamDisplayColumnRel);

    int updateByPrimaryKey(MktCamDisplayColumnRel mktCamDisplayColumnRel);

    int deleteByPrimaryKey(Long mktCamDisplayColumnRelId);

    List<MktCamDisplayColumnRel> selectLabelByCampaignIdAndDisplayId(@Param("mktCampaignId") Long mktCampaignId, @Param("displayId") Long displayId);

    int deleteByMktCampaignId(@Param("mktCampaignId") Long mktCampaignId);

    List<MktCamDisplayColumnRel> selectDisplayLabelByCamId(@Param("mktCampaignId") Long mktCampaignId);

}
