package com.zjtelcom.cpct_prd.dao.event;

import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.request.event.QryEventSceneListReq;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EventScenePrdMapper {
    int deleteByPrimaryKey(Long eventSceneId);

    int insert(EventScene record);

    EventScene selectByPrimaryKey(Long eventSceneId);

    List<EventSceneDO> selectAll();

    int updateByPrimaryKey(EventScene record);

    int updateById(EventScene record);

    List<EventScene> qryEventSceneList(QryEventSceneListReq qryEventSceneListReq);

    int saveEventScene(EventSceneDO sceneDO);

    EventScene getEventScene(Long eventSceneId);

    int createEventScene(EventScene eventScene);

    int delEventScene(EventScene eventScene);

    List<EventScene> qryEventSceneByEvtId(Long contactEvtId);

    int coEventScene(EventScene eventScene);

}