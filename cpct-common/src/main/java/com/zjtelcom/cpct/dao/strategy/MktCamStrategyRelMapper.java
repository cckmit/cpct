package com.zjtelcom.cpct.dao.strategy;


import com.zjtelcom.cpct.pojo.MktCamStrategyRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamStrategyRelMapper {
    int deleteByPrimaryKey(Long campStrRelId);

    int deleteByStrategyId(Long strategyId);

    int insert(MktCamStrategyRel record);

    MktCamStrategyRel selectByPrimaryKey(Long campStrRelId);

    List<MktCamStrategyRel> selectAll();

    int updateByPrimaryKey(MktCamStrategyRel record);

    List<MktCamStrategyRel> selectByMktCampaignId(Long mktCampaignId);
}