package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.request.event.QryEventSceneListReq;
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

    int updateByPrimaryKey(EventScene record);

    List<EventScene> qryEventSceneList(QryEventSceneListReq qryEventSceneListReq);

    int saveEventScene(EventSceneDO sceneDO);

    EventScene getEventScene(Long eventSceneId);

    int createEventScene(EventScene eventScene);

    int delEventScene(EventScene eventScene);

}