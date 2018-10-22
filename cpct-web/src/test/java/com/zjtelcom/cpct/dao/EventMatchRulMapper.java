package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventMatchRul;
import java.util.List;

public interface EventMatchRulMapper {
    int deleteByPrimaryKey(Long evtMatchRulId);

    int insert(EventMatchRul record);

    EventMatchRul selectByPrimaryKey(Long evtMatchRulId);

    List<EventMatchRul> selectAll();

    int updateByPrimaryKey(EventMatchRul record);
}