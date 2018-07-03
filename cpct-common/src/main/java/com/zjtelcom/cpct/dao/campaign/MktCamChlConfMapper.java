package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;

import java.util.List;

public interface MktCamChlConfMapper {
    int deleteByPrimaryKey(Long evtContactConfId);

    int insert(MktCamChlConfDO mktCamChlConfDO);

    MktCamChlConfDO selectByPrimaryKey(Long evtContactConfId);

    List<MktCamChlConfDO> selectAll();

    int updateByPrimaryKey(MktCamChlConfDO mktCamChlConfDO);
}