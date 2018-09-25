package com.zjtelcom.cpct_prd.dao.template;

import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TarGrpTemplatePrdMapper {
    int deleteByPrimaryKey(Long tarGrpTemplateId);

    int insert(TarGrpTemplateDO tarGrpTemplateDO);

    TarGrpTemplateDO selectByPrimaryKey(Long tarGrpTemplateId);

    List<TarGrpTemplateDO> selectAll();

    List<TarGrpTemplateDO> selectByName(@Param("tarGrpTemplateName") String tarGrpTemplateName);

    int updateByPrimaryKey(TarGrpTemplateDO tarGrpTemplateDO);
}