package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EventSorce;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @Description EventSorceMapper
 * @Author pengy
 * @Date 2018/6/21 10:13
 */
@Mapper
@Repository
public interface EventSorceMapper {

    List<EventSorce> listEventSorces(@Param("evtSrcCode") String evtSrcCode, @Param("evtSrcName") String evtSrcName);

    void delEventSorce(@Param("evtSrcId") Long evtSrcId);

    EventSorce editEventSorce(@Param("evtSrcId") Long evtSrcId);

    int saveEventSorce(EventSorce eventSorce);

    int updateEventSorce(EventSorce eventSorce);

}
