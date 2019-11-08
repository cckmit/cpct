package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCamChlConfDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktCamChlConfMapper {
    int deleteByPrimaryKey(Long evtContactConfId);

    int insert(MktCamChlConfDO mktCamChlConfDO);

    MktCamChlConfDO selectByPrimaryKey(Long evtContactConfId);

    List<MktCamChlConfDO> selectAll();

    String selectforName(Long evtContactConfId);

    int updateByPrimaryKey(MktCamChlConfDO mktCamChlConfDO);

    List<MktCamChlConfDO> listByIdList(@Param("list")List<Long> idList);

    List<MktCamChlConfDO> selectByMktCamChlConf(MktCamChlConfDO mktCamChlConfDO);

    List<MktCamChlConfDO> selectByCampaignId(@Param("id")Long id);
}