package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.dto.event.EventMatchRulCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EventMatchRulConditionMapper {

    List<EventMatchRulCondition> listEventMatchRulCondition(@Param("evtMatchRulId") Long evtMatchRulId);

    int insertEventMatchRulCondition(EventMatchRulCondition eventMatchRulCondition);

    EventMatchRulCondition selectByPrimaryKey(@Param("conditionId") Long conditionId);

    int modEventMatchRulCondition(EventMatchRulCondition eventMatchRulCondition);

    int delEventMatchRulCondition(EventMatchRulCondition eventMatchRulCondition);

}
