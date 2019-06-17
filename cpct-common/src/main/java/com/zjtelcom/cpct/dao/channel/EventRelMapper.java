package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.EventRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EventRelMapper {
    int deleteByPrimaryKey(Long complexEvtRelaId);

    int insert(EventRel record);

    EventRel selectByPrimaryKey(Long complexEvtRelaId);

    List<EventRel> selectAll();

    int updateByPrimaryKey(EventRel record);

    List<EventRel> selectByZEvtId(Long zEvtId);
}