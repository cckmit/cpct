package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamChlResultConfRelDO;
import com.zjtelcom.cpct.domain.campaign.MktCamChlResultDO;
import com.zjtelcom.cpct.domain.campaign.MktCamResultRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamResultRelMapper {
    int deleteByPrimaryKey(Long mktCamResultRelId);

    int insert(MktCamResultRelDO mktCamResultRelDO);

    MktCamResultRelDO selectByPrimaryKey(Long mktCamResultRelId);

    List<MktCamResultRelDO> selectAll();

    int updateByPrimaryKey(MktCamResultRelDO mktCamResultRelDO);

    int changeStatusByMktCampaignId(MktCamResultRelDO mktCamResultRelDO);

    List<Long> selectAllGroupByMktCampaignId();
}