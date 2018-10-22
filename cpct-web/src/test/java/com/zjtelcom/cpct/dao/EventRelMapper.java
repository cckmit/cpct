package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventRel;
import java.util.List;

public interface EventRelMapper {
    int deleteByPrimaryKey(Long complexEvtRelaId);

    int insert(EventRel record);

    EventRel selectByPrimaryKey(Long complexEvtRelaId);

    List<EventRel> selectAll();

    int updateByPrimaryKey(EventRel record);
}