package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @Author pengy
 * @Date 2018/6/21 10:13
 */
@Mapper
@Repository
public interface EventMapper {

    List<EventList> listEvents(@Param("evtSrcId") Long evtSrcId, @Param("eventName") String eventName);

}
