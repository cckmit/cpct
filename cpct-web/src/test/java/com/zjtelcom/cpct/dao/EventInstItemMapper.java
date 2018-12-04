package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventInstItem;

import java.util.List;

public interface EventInstItemMapper {
    int deleteByPrimaryKey(Long evtInstItemId);

    int insert(EventInstItem record);

    EventInstItem selectByPrimaryKey(Long evtInstItemId);

    List<EventInstItem> selectAll();

    int updateByPrimaryKey(EventInstItem record);
}