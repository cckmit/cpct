package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamGrpRulMapper {
    int deleteByPrimaryKey(Long mktCamGrpRulId);

    int deleteByTarGrpId(Long tarGrpId);

    int insert(MktCamGrpRul record);

    MktCamGrpRul selectByPrimaryKey(Long mktCamGrpRulId);

    MktCamGrpRul selectByRuleId(Long mktStrategyConfRuleId);

    int countByTarGrpId(Long tarGrpId);

    List<MktCamGrpRul> selectAll();

    MktCamGrpRul selectByTarGrpId(Long tarGrpId);

    int updateByPrimaryKey(MktCamGrpRul record);

}