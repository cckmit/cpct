package com.zjtelcom.cpct.dao.eagle;



import com.zjtelcom.cpct.model.Trigger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface TriggerMapper {

//    List<PageData> queryTriggerListByPage(Page page);
//
//    List<PageData> getAllTriggerByPage(Page page);

    int insertTrigger(Trigger trigger);

    int updateTriggerById(Trigger trigger);

    int deleteTriggerById(Trigger trigger);

    List<Trigger> queryTriggerByConditionName(String conditionName);

    List<Trigger> queryTriggerIsUsering(Trigger trigger);

    int deleteByPrimaryKey(Integer conditionId);

    int insert(Trigger record);

    int insertSelective(Trigger record);

    Trigger selectByPrimaryKey(Integer conditionId);

    int updateByPrimaryKeySelective(Trigger record);

    int updateByPrimaryKey(Trigger record);

    int updateValueId(@Param("record") List<Trigger> record);

    List<HashMap<String, Object>> selectByConditionId(String conditionId);

    List<Trigger> queryTriggerByleftOperand(@Param("leftOperands") List<String> leftOperands);

    List<Trigger> queryTriggerByLeftOpers(@Param("record") List<Map<String, String>> record);

    List<Trigger> queryAll();
    
    List<Trigger> queryTriggerByIds(@Param("ids") List<String> ids);
}
