package com.zjtelcom.cpct.dao.event;

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

    ContactEvtItem selectByPrimaryKey(Long evtItemId);

    List<EventItemDO> selectAll();

    int updateByPrimaryKey(EventItemDO record);

    int saveEventItem(EventItemDO record);

    List<ContactEvtItem> listEventItem(@Param("contactEvtId") Long contactEvtId);

    List<ContactEvtItem> listMainItem();

    ContactEvtItem viewEventItem(Long evtItemId);

    int insertContactEvtItem(ContactEvtItem record);

    int modEventItem(ContactEvtItem record);

}