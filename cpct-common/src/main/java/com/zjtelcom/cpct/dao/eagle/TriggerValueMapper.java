package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.TriggerValue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface TriggerValueMapper {
    int insert(TriggerValue record);

    int insertBatch(@Param("record") List<TriggerValue> record);

    int insertSelective(TriggerValue record);

    List<TriggerValue> queryAll();

    TriggerValue queryByLeftAndValue(@Param("showValue") String showValue,
                                     @Param("domain") String domain,
                                     @Param("valueId") String valueId);
}