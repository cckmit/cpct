package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.domain.event.EventItemDO;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ContactEvtItemMapper {

    int deleteByPrimaryKey(Long evtItemId);

    int deleteByEventId(Long contactEvtId);

    int insert(ContactEvtItem record);

    EventItem selectByPrimaryKey(Long evtItemId);

    List<EventItemDO> selectAll();

    int updateByPrimaryKey(EventItemDO record);

    int saveEventItem(EventItemDO record);

    List<EventItem> listEventItem(@Param("contactEvtId") Long contactEvtId);

    List<EventItem> listMainItem();

    EventItem viewEventItem(Long evtItemId);

    int insertContactEvtItem(EventItem record);

    int modEventItem(EventItem record);

}