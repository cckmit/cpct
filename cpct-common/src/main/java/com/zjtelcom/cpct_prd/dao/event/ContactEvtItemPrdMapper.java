package com.zjtelcom.cpct_prd.dao.event;

import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.domain.event.EventItemDO;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ContactEvtItemPrdMapper {

    int deleteByPrimaryKey(Long evtItemId);

    int deleteByEventId(Long contactEvtId);

    int insert(EventItem record);

    EventItem selectByPrimaryKey(Long evtItemId);

    List<EventItemDO> selectAll();

    int updateByPrimaryKey(EventItem record);

    int saveEventItem(EventItem record);

    List<EventItem> listEventItem(@Param("contactEvtId") Long contactEvtId);

    List<EventItem> listMainItem();

    EventItem viewEventItem(Long evtItemId);

    int insertContactEvtItem(EventItem record);

    int modEventItem(EventItem record);

}