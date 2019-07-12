package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamCityRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktCamCityRelMapper {
    int deleteByPrimaryKey(Long mktCamCityRelId);

    int deleteByMktCampaignId(Long mktCampaignId);

    int insert(MktCamCityRelDO mktCamCityRelDO);

    int insertBatch(List<MktCamCityRelDO> mktCamCityRelDOList);

    MktCamCityRelDO selectByPrimaryKey(Long mktCamCityRelId);

    List<MktCamCityRelDO> selectByMktCampaignId(Long mktCampaignId);

    List<MktCamCityRelDO> selectAll();

    int updateByPrimaryKey(MktCamCityRelDO mktCamCityRelDO);
}