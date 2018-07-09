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

    int insert(MktCamEvtRelDO mktCamEvtRelDO);

    MktCamEvtRelDO selectByPrimaryKey(Long mktCampEvtRelId);

    List<MktCamEvtRelDO> selectAll();

    int updateByPrimaryKey(MktCamEvtRelDO mktCamEvtRelDO);

    List<MktCamEvtRel> qryBycontactEvtId(@Param("contactEvtId") Long contactEvtId);
}