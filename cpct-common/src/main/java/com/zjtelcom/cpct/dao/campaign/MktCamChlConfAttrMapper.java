package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;

import java.util.List;

public interface MktCamChlConfAttrMapper {
    int deleteByPrimaryKey(Long contactChlAttrRstrId);

    int insert(MktCamChlConfAttrDO mktCamChlConfAttrDO);

    MktCamChlConfAttrDO selectByPrimaryKey(Long contactChlAttrRstrId);

    List<MktCamChlConfAttrDO> selectByEvtContactConfId(Long contactChlAttrRstrId);

    List<MktCamChlConfAttrDO> selectAll();

    int updateByPrimaryKey(MktCamChlConfAttrDO mktCamChlConfAttrDO);
}