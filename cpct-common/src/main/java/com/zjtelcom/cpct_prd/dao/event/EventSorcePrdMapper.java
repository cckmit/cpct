package com.zjtelcom.cpct_prd.dao.event;

import com.zjtelcom.cpct.domain.event.EventSorceDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EventSorcePrdMapper {
    int deleteByPrimaryKey(Long evtSrcId);

    int insert(EventSorceDO eventSorceDO);

    EventSorceDO selectByPrimaryKey(Long evtSrcId);

    List<EventSorceDO> selectAll();

    int updateByPrimaryKey(EventSorceDO eventSorceDO);

    List<EventSorceDO> selectForPage(EventSorceDO eventSorceDO);
}