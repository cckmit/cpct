package com.zjtelcom.cpct.dao.grouping;


import com.zjtelcom.cpct.domain.grouping.TarGrpTemplateConditionDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TarGrpTemplateConditionMapper {
    int deleteByPrimaryKey(Long conditionId);

    int deleteByTarGrpTemplateId(Long tarGrpTemplateId);

    int deleteBatch(List<Long> conditionIds);

    int insert(TarGrpTemplateConditionDO tarGrpTemplateConditionDO);

    TarGrpTemplateConditionDO selectByPrimaryKey(Long conditionId);

    List<TarGrpTemplateConditionDO> selectByTarGrpTemplateId(Long tarGrpTemplateId);

    List<TarGrpTemplateConditionDO> selectAll();

    int updateByPrimaryKey(TarGrpTemplateConditionDO tarGrpTemplateConditionDO);
}