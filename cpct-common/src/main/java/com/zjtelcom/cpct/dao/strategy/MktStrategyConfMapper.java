package com.zjtelcom.cpct.dao.strategy;


import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktStrategyConfMapper {
    int deleteByPrimaryKey(Long mktStrategyConfId);

    int insert(MktStrategyConfDO mktStrategyConfDO);

    MktStrategyConfDO selectByPrimaryKey(Long mktStrategyConfId);

    List<MktStrategyConfDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfDO mktStrategyConfDO);

    List<MktStrategyConfDO> selectByCampaignId(@Param("campaignId")Long campaignId);
}