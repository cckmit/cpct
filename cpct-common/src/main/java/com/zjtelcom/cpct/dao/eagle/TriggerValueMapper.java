package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.TriggerValue;
import org.apache.ibatis.annotations.Param;
import java.util.List;


public interface TriggerValueMapper {
    int insert(TriggerValue record);

    int insertBatch(@Param("record") List<TriggerValue> record);

    int insertSelective(TriggerValue record);

    List<TriggerValue> queryAll();

    TriggerValue queryByLeftAndValue(@Param("showValue") String showValue,
                                     @Param("domain") String domain,
                                     @Param("valueId") String valueId);
}