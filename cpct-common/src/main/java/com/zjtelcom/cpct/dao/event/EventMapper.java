package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.DO.EventDO;
import com.zjtelcom.cpct.domain.event.EventList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description EventMapper
 * @Author pengy
 * @Date 2018/6/21 10:13
 */
@Mapper
@Repository
public interface EventMapper {

    List<EventList> listEvents(@Param("evtSrcId") Long evtSrcId, @Param("eventName") String eventName);

    int saveEvent(EventDO record);

    int delEvent(@Param("eventId") Long eventId);

    int updateEventStatusCd(@Param("eventId") Long eventId,@Param("statusCd") String statusCd);

    EventDO getEventById(@Param("eventId") Long eventId );

}
