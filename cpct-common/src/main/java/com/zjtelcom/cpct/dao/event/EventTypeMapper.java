package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.DO.EventTypeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EventTypeMapper {
    int deleteByPrimaryKey(Long evtTypeId);

    int insert(EventTypeDO record);

    EventTypeDO selectByPrimaryKey(Long evtTypeId);

    List<EventTypeDO> selectAll();

    int updateByPrimaryKey(EventTypeDO record);

    List<EventTypeDO> listEventTypes(@Param("evtTypeId") Long evtTypeId, @Param("parEvtTypeId") Long parEvtTypeId);

    int saveEventType(EventTypeDO eventTypeDO);
}