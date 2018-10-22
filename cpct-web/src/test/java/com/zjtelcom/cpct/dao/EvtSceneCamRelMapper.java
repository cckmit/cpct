package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.EvtSceneCamRel;
import java.util.List;

public interface EvtSceneCamRelMapper {
    int deleteByPrimaryKey(Long sceneCamRelId);

    int insert(EvtSceneCamRel record);

    EvtSceneCamRel selectByPrimaryKey(Long sceneCamRelId);

    List<EvtSceneCamRel> selectAll();

    int updateByPrimaryKey(EvtSceneCamRel record);
}