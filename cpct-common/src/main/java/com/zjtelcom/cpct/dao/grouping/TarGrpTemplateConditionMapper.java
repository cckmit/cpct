package com.zjtelcom.cpct.dao.grouping;


import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateConditionDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TarGrpTemplateConditionMapper {
    int deleteByPrimaryKey(Long conditionId);

    int deleteByTarGrpTemplateId(Long tarGrpTemplateId);

    int insert(TarGrpTemplateConditionDO tarGrpTemplateConditionDO);

    TarGrpTemplateConditionDO selectByPrimaryKey(Long conditionId);

    List<TarGrpTemplateConditionDO> selectByTarGrpTemplateId(Long tarGrpTemplateId);

    List<TarGrpTemplateConditionDO> selectAll();

    int updateByPrimaryKey(TarGrpTemplateConditionDO tarGrpTemplateConditionDO);
}