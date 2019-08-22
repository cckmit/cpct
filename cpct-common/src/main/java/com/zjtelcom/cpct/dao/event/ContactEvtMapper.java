package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description 事件mapper
 * @Author pengy
 * @Date 2018/6/21 10:13
 */
@Mapper
@Repository
public interface ContactEvtMapper {

    List<ContactEvt> listEvents(ContactEvt contactEvt);

    int insert(ContactEvt contactEvt);

    int delEvent(@Param("contactEvtId") Long contactEvtId);

    int updateEventStatusCd(@Param("contactEvtId") Long eventId, @Param("statusCd") String statusCd);

    ContactEvt getEventById(@Param("contactEvtId") Long contactEvtId);

    int updateEvent(EventDO eventDO);

    ContactEvt getEventByEventNbr(@Param("eventNbr") String eventNbr);

    int createContactEvtJt(ContactEvt contactEvt);

    int createContactEvt(ContactEvt contactEvt);

    int modContactEvtJt(ContactEvt contactEvt);

    int modContactEvtCode(@Param("contactEvtId") Long contactEvtId , @Param("contactEvtCode") String contactEvtCode);

    int modContactEvt(ContactEvt contactEvt);

    List<ContactEvt> query();

    List<ContactEvt> findEventsByKey(ContactEvt contactEvt);

}
