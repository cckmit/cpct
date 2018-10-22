package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EventScene;
import java.util.List;

public interface EventSceneMapper {
    int deleteByPrimaryKey(Long eventSceneId);

    int insert(EventScene record);

    EventScene selectByPrimaryKey(Long eventSceneId);

    List<EventScene> selectAll();

    int updateByPrimaryKey(EventScene record);
}