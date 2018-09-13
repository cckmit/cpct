package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EvtSceneCamRelDO;
import com.zjtelcom.cpct.dto.event.EvtSceneCamRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface EvtSceneCamRelMapper {

    int deleteByPrimaryKey(Long sceneCamRelId);

    int insert(EvtSceneCamRel record);

    EvtSceneCamRel selectByPrimaryKey(Long sceneCamRelId);

    EvtSceneCamRel findByCampaignIdAndEventSceneId(@Param("campaignId")Long campaignId,@Param("eventSceneId")Long eventSceneId);

    List<EvtSceneCamRel> selectAll();
    int updateByPrimaryKey(EvtSceneCamRel record);

    //通过事件场景id查询所有关联关系
    List<EvtSceneCamRel> selectCamsByEvtSceneId(@Param("eventSceneId") Long eventSceneId);

}