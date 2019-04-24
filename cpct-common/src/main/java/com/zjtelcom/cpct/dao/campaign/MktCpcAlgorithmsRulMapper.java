package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCpcAlgorithmsRulDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCpcAlgorithmsRulMapper {
    int deleteByPrimaryKey(Long algorithmsRulId);

    int insert(MktCpcAlgorithmsRulDO record);

    MktCpcAlgorithmsRulDO selectByPrimaryKey(Long algorithmsRulId);

    List<MktCpcAlgorithmsRulDO> selectAll();

    int updateByPrimaryKey(MktCpcAlgorithmsRulDO record);

    List<MktCpcAlgorithmsRulDO> queryList(MktCpcAlgorithmsRulDO mktCpcAlgorithmsRulDO);
}