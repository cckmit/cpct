package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamChlResultConfRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktCamChlResultConfRelMapper {
    int deleteByPrimaryKey(Long mktCamChlResultConfRelId);

    int deleteByMktCamChlResultId(Long mktCamChlResultId);

    int insert(MktCamChlResultConfRelDO mktCamChlResultConfRelDO);

    MktCamChlResultConfRelDO selectByPrimaryKey(Long mktCamChlResultConfRelId);

    List<MktCamChlResultConfRelDO> selectByMktCamChlResultId(Long mktCamChlResultId);

    List<MktCamChlResultConfRelDO> selectAll();

    int updateByPrimaryKey(MktCamChlResultConfRelDO mktCamChlResultConfRelDO);
}