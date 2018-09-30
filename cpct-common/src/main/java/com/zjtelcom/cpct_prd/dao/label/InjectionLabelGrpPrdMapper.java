package com.zjtelcom.cpct_prd.dao.label;

import com.zjtelcom.cpct.domain.channel.LabelGrp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface InjectionLabelGrpPrdMapper {
    int deleteByPrimaryKey(Long grpId);

    int insert(LabelGrp record);

    LabelGrp selectByPrimaryKey(Long grpId);

    List<LabelGrp> selectAll();

    List<LabelGrp> findByParams(@Param("grpName") String grpName);

    LabelGrp findByGrpName(@Param("grpName") String grpName);

    int updateByPrimaryKey(LabelGrp record);
}