package com.zjtelcom.cpct_prd.dao.grouping;

import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TarGrpConditionPrdMapper {

    int deleteByPrimaryKey(Long conditionId);

    int deleteByTarGrpId(Long tarGrpId);

    int insert(TarGrpCondition record);

    TarGrpCondition selectByPrimaryKey(Long conditionId);

    List<TarGrpConditionDO> selectAll();

    int updateByPrimaryKey(TarGrpCondition record);

    TarGrpCondition getTarGrpCondition(Long conditionId);

    List<TarGrpCondition> listTarGrpCondition(@Param("tarGrpId") Long tarGrpId);

    List<TarGrpConditionDO> selectByActivityId(@Param("activityId") Long activityId);

    int modTarGrpCondition(TarGrpCondition record);

    int delTarGrpCondition(TarGrpCondition tarGrpCondition);

    int insertByBatch(@Param("list") List<TarGrpCondition> record);

    int deleteByTarGrpTemplateId(Long tarGrpTemplateId);
}