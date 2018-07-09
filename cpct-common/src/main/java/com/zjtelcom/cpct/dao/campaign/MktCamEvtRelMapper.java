package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamEvtRelDO;

import java.util.List;

public interface MktCamEvtRelMapper {
    int deleteByPrimaryKey(Long mktCampEvtRelId);

    int insert(MktCamEvtRelDO mktCamEvtRelDO);

    MktCamEvtRelDO selectByPrimaryKey(Long mktCampEvtRelId);

    List<MktCamEvtRelDO> selectAll();

    int updateByPrimaryKey(MktCamEvtRelDO mktCamEvtRelDO);
}