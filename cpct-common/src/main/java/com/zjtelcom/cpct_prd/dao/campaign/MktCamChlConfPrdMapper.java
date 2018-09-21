package com.zjtelcom.cpct_prd.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamChlConfPrdMapper {
    int deleteByPrimaryKey(Long evtContactConfId);

    int insert(MktCamChlConfDO mktCamChlConfDO);

    MktCamChlConfDO selectByPrimaryKey(Long evtContactConfId);

    List<MktCamChlConfDO> selectAll();

    String selectforName(Long evtContactConfId);

    int updateByPrimaryKey(MktCamChlConfDO mktCamChlConfDO);
}