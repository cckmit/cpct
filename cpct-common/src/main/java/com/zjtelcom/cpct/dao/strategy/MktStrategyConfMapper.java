package com.zjtelcom.cpct.dao.strategy;


import com.zjtelcom.cpct.domain.campaign.MktStrategyConfDO;
import com.zjtelcom.cpct.dto.campaign.MktStrategyConf;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktStrategyConfMapper {
    Long deleteByPrimaryKey(Long mktStrategyConfId);

    Long insert(MktStrategyConfDO mktStrategyConfDO);

    MktStrategyConfDO selectByPrimaryKey(Long mktStrategyConfId);

    List<MktStrategyConfDO> selectAll();

    Long updateByPrimaryKey(MktStrategyConfDO mktStrategyConfDO);
}