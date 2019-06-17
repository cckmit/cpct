package com.zjtelcom.cpct.dao.strategy;


import com.zjtelcom.cpct.dto.strategy.MktStrategy;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktStrategyMapper {
    int deleteByPrimaryKey(Long strategyId);

    int insert(MktStrategy mktStrategy);

    MktStrategy selectByPrimaryKey(Long strategyId);

    List<MktStrategy> selectAll();

    int updateByPrimaryKey(MktStrategy mktStrategy);

    List<MktStrategy> queryList(MktStrategy mktStrategy);
}