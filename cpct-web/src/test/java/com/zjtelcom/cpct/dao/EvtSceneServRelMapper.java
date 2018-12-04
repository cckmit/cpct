package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EvtSceneServRel;

import java.util.List;

public interface EvtSceneServRelMapper {
    int deleteByPrimaryKey(Long sceneServRelId);

    int insert(EvtSceneServRel record);

    EvtSceneServRel selectByPrimaryKey(Long sceneServRelId);

    List<EvtSceneServRel> selectAll();

    int updateByPrimaryKey(EvtSceneServRel record);
}