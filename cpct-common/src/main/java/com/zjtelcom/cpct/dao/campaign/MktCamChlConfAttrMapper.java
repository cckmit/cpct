package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamChlConfAttrDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktCamChlConfAttrMapper {
    int deleteByPrimaryKey(Long contactChlAttrRstrId);

    int insert(MktCamChlConfAttrDO mktCamChlConfAttrDO);

    int insertBatch(List<MktCamChlConfAttrDO> mktCamChlConfAttrDOList);

    MktCamChlConfAttrDO selectByPrimaryKey(Long contactChlAttrRstrId);

    List<MktCamChlConfAttrDO> selectByEvtContactConfId(Long contactChlAttrRstrId);

    int deleteByEvtContactConfId(Long evtContactConfId);

    List<MktCamChlConfAttrDO> selectAll();

    int updateByPrimaryKey(MktCamChlConfAttrDO mktCamChlConfAttrDO);
}