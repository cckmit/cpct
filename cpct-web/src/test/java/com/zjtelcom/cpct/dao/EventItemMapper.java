package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventItem;

import java.util.List;

public interface EventItemMapper {
    int deleteByPrimaryKey(Long evtItemId);

    int insert(EventItem record);

    EventItem selectByPrimaryKey(Long evtItemId);

    List<EventItem> selectAll();

    int updateByPrimaryKey(EventItem record);
}