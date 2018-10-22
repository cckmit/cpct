package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventSorce;
import java.util.List;

public interface EventSorceMapper {
    int deleteByPrimaryKey(Long evtSrcId);

    int insert(EventSorce record);

    EventSorce selectByPrimaryKey(Long evtSrcId);

    List<EventSorce> selectAll();

    int updateByPrimaryKey(EventSorce record);
}