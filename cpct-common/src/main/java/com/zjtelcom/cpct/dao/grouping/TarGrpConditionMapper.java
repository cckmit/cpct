package com.zjtelcom.cpct.dao.grouping;

import com.zjtelcom.cpct.domain.grouping.TarGrpConditionDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface TarGrpConditionMapper {

    int deleteByPrimaryKey(Long conditionId);

    int insert(TarGrpConditionDO record);

    TarGrpConditionDO selectByPrimaryKey(Long conditionId);

    List<TarGrpConditionDO> selectAll();

    int updateByPrimaryKey(TarGrpConditionDO record);

    TarGrpConditionDO getTarGrpCondition(Long conditionId);

    List<TarGrpConditionDO> listTarGrpCondition(@Param("tarGrpId") Long tarGrpId);

    List<TarGrpConditionDO> selectByActivityId(@Param("activityId") Long activityId);
}