package com.zjtelcom.cpct_prd.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktVerbalConditionPrdMapper {
    int deleteByPrimaryKey(Long conditionId);

    int insert(MktVerbalCondition record);

    MktVerbalCondition selectByPrimaryKey(Long conditionId);

    List<MktVerbalCondition> selectAll();

    List<MktVerbalCondition> findConditionListByVerbalId(@Param("verbalId") Long verbalId);

    List<MktVerbalCondition> findChannelConditionListByVerbalId(@Param("verbalId") Long verbalId);

    int deleteByVerbalId(@Param("conditionType") String conditionType, @Param("verbalId") Long verbalId);

    int updateByPrimaryKey(MktVerbalCondition record);
}