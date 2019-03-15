package com.zjtelcom.cpct_prd.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCpcAlgorithmsRulDO;

import java.util.List;

public interface MktCpcAlgorithmsRulPrdMapper {
    int deleteByPrimaryKey(Long algorithmsRulId);

    int insert(MktCpcAlgorithmsRulDO record);

    MktCpcAlgorithmsRulDO selectByPrimaryKey(Long algorithmsRulId);

    List<MktCpcAlgorithmsRulDO> selectAll();

    int updateByPrimaryKey(MktCpcAlgorithmsRulDO record);

    List<MktCpcAlgorithmsRulDO> queryList(MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO);
}