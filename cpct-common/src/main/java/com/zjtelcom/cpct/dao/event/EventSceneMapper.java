package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.DO.EventSceneDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface EventSceneMapper {
    int deleteByPrimaryKey(Long eventSceneId);

    int insert(EventSceneDO record);

    EventSceneDO selectByPrimaryKey(Long eventSceneId);

    List<EventSceneDO> selectAll();

    int updateByPrimaryKey(EventSceneDO record);

    List<EventSceneDO> listEventSences(EventSceneDO sceneDO);

    int saveEventScene(EventSceneDO sceneDO);

    EventSceneDO getEventSceneDO(Long eventSceneId);

}