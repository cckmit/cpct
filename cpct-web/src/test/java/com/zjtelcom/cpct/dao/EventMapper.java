package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Event;
import java.util.List;

public interface EventMapper {
    int deleteByPrimaryKey(Long eventId);

    int insert(Event record);

    Event selectByPrimaryKey(Long eventId);

    List<Event> selectAll();

    int updateByPrimaryKey(Event record);
}