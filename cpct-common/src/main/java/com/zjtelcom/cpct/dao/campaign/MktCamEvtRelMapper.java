package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamEvtRelMapper {
    int deleteByPrimaryKey(Long mktCampEvtRelId);

    int deleteByMktCampaignId(Long mktCampaignId);

    int insert(MktCamEvtRelDO mktCamEvtRelDO);

    MktCamEvtRelDO selectByPrimaryKey(Long mktCampEvtRelId);

    MktCamEvtRelDO findByCampaignIdAndEvtId(@Param("campaignId")Long campaignId,@Param("evtId")Long evtId);

    List<MktCamEvtRelDO> qryByMktCampaignId(Long mktCampaignId);

    List<MktCamEvtRelDO> findRelListByEvtId(@Param("contactEvtId") Long contactEvtId);

    List<MktCamEvtRelDO> selectAll();

    int updateByPrimaryKey(MktCamEvtRelDO mktCamEvtRelDO);

    List<MktCamEvtRel> qryBycontactEvtId(@Param("contactEvtId") Long contactEvtId);

    List<Long> listActivityByEventId(@Param("eventId") Long eventId);

}