package com.zjtelcom.cpct_prd.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamGrpRulPrdMapper {
    int deleteByPrimaryKey(Long mktCamGrpRulId);

    int insert(MktCamGrpRul record);

    MktCamGrpRul selectByPrimaryKey(Long mktCamGrpRulId);

    List<MktCamGrpRul> selectAll();

    int updateByPrimaryKey(MktCamGrpRul record);

}