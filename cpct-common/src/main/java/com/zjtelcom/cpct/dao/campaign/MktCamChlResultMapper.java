package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamChlResultDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktCamChlResultMapper {
    int deleteByPrimaryKey(Long mktCamChlResultId);

    int insert(MktCamChlResultDO mktCamChlResultDO);

    MktCamChlResultDO selectByPrimaryKey(Long mktCamChlResultId);

    List<MktCamChlResultDO> selectAll();

    int updateByPrimaryKey(MktCamChlResultDO mktCamChlResultDO);
}