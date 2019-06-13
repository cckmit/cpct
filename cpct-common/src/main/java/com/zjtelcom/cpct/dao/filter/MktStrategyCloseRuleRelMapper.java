package com.zjtelcom.cpct.dao.filter;

import com.zjtelcom.cpct.domain.strategy.MktStrategyFilterRuleRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktStrategyCloseRuleRelMapper {

    List<MktStrategyFilterRuleRelDO> selectByRuleId(Long ruleId);
}
