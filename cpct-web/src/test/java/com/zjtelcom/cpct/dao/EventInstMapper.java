package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventInst;

import java.util.List;

public interface EventInstMapper {
    int deleteByPrimaryKey(Long evtInstId);

    int insert(EventInst record);

    EventInst selectByPrimaryKey(Long evtInstId);

    List<EventInst> selectAll();

    int updateByPrimaryKey(EventInst record);
}