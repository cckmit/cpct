package com.zjtelcom.cpct_prd.dao.event;

import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CamEvtRelPrdMapper {
    int deleteByPrimaryKey(Long mktCampEvtRelId);

    int deleteByMktCampaignId(Long mktCampaignId);

    int insert(MktCamEvtRel mktCamEvtRel);

    MktCamEvtRelDO selectByPrimaryKey(Long mktCampEvtRelId);

    MktCamEvtRelDO findByCampaignIdAndEvtId(@Param("campaignId") Long campaignId, @Param("evtId") Long evtId);

    List<MktCamEvtRelDO> selectByMktCampaignId(Long mktCampaignId);

    List<MktCamEvtRelDO> findRelListByEvtId(@Param("contactEvtId") Long contactEvtId);

    List<MktCamEvtRelDO> selectAll();

    int updateByPrimaryKey(MktCamEvtRel mktCamEvtRel);

    List<MktCamEvtRel> qryBycontactEvtId(@Param("contactEvtId") Long contactEvtId);

    List<Long> listActivityByEventId(@Param("eventId") Long eventId);

    List<MktCamEvtRelDO> listActByEventId(@Param("eventId") Long eventId);



}