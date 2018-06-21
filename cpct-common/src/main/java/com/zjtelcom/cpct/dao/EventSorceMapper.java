package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventSorce;
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

    List<EventSorce> listEventSorces(@Param("evtSrcId") String evtSrcCode, @Param("eventName") String evtSrcName);

}
