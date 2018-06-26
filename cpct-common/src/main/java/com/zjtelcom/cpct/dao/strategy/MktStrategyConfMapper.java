package com.zjtelcom.cpct.dao.strategy;


import com.zjtelcom.cpct.dto.campaign.MktStrategyConf;

import java.util.List;

public interface MktStrategyConfMapper {
    int deleteByPrimaryKey(Long mktStrategyConfId);

    int insert(MktStrategyConf mktStrategyConf);

    MktStrategyConf selectByPrimaryKey(Long mktStrategyConfId);

    List<MktStrategyConf> selectAll();

    int updateByPrimaryKey(MktStrategyConf mktStrategyConf);
}