package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamDisplayColumnRel;
import com.zjtelcom.cpct.dto.channel.DisplayLabelInfo;
import com.zjtelcom.cpct.dto.channel.LabelDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamDisplayColumnRelMapper {

    List<MktCamDisplayColumnRel> selectAll();

    MktCamDisplayColumnRel selectByPrimaryKey(Long mktCamDisplayColumnRelId);

    int insert(MktCamDisplayColumnRel mktCamDisplayColumnRel);

    int updateByPrimaryKey(MktCamDisplayColumnRel mktCamDisplayColumnRel);

    int deleteByPrimaryKey(Long mktCamDisplayColumnRelId);

    List<MktCamDisplayColumnRel> selectLabelByCampaignIdAndDisplayId(@Param("mktCampaignId") Long mktCampaignId, @Param("displayId") Long displayId);

    int deleteByMktCampaignId(@Param("mktCampaignId") Long mktCampaignId);

    List<MktCamDisplayColumnRel> selectDisplayLabelByCamId(@Param("mktCampaignId") Long mktCampaignId);

    List<LabelDTO> selectLabelDisplayListByCamId(@Param("mktCampaignId") Long mktCampaignId);

    int updateDisplayLabel(MktCamDisplayColumnRel rel);

}
