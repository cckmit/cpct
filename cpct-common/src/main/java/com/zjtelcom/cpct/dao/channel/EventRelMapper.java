package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.EventRel;

import java.util.List;

public interface EventRelMapper {
    int deleteByPrimaryKey(Long complexEvtRelaId);

    int insert(EventRel record);

    EventRel selectByPrimaryKey(Long complexEvtRelaId);

    List<EventRel> selectAll();

    int updateByPrimaryKey(EventRel record);

    List<EventRel> selectByZEvtId(Long zEvtId);
}