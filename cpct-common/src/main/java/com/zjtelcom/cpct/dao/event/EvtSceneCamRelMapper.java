package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EvtSceneCamRelDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface EvtSceneCamRelMapper {

    int deleteByPrimaryKey(Long sceneCamRelId);
    int insert(EvtSceneCamRelDO record);
    EvtSceneCamRelDO selectByPrimaryKey(Long sceneCamRelId);
    List<EvtSceneCamRelDO> selectAll();
    int updateByPrimaryKey(EvtSceneCamRelDO record);

    //通过事件场景id查询所有关联关系
    List<EvtSceneCamRelDO> selectCamsByEvtSceneId(@Param("eventSceneId") Long eventSceneId);

}