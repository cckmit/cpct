package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventType;

import java.util.List;

public interface EventTypeMapper {
    int deleteByPrimaryKey(Long evtTypeId);

    int insert(EventType record);

    EventType selectByPrimaryKey(Long evtTypeId);

    List<EventType> selectAll();

    int updateByPrimaryKey(EventType record);
}