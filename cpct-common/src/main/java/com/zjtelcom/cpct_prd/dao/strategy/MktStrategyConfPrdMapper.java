package com.zjtelcom.cpct_prd.dao.strategy;


import com.zjtelcom.cpct.domain.strategy.MktStrategyConfDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktStrategyConfPrdMapper {
    int deleteByPrimaryKey(Long mktStrategyConfId);

    int insert(MktStrategyConfDO mktStrategyConfDO);

    MktStrategyConfDO selectByPrimaryKey(Long mktStrategyConfId);

    List<MktStrategyConfDO> selectAll();

    int updateByPrimaryKey(MktStrategyConfDO mktStrategyConfDO);
}