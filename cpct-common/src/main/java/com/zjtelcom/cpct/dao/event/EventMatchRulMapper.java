package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface EventMatchRulMapper {

    int deleteByPrimaryKey(Long evtMatchRulId);

    int insert(EventMatchRulDO record);

    EventMatchRulDO selectByPrimaryKey(Long evtMatchRulId);

    List<EventMatchRulDO> selectAll();

    int updateByPrimaryKey(EventMatchRulDO record);

    List<EventMatchRulDO> list();

    List<EventMatchRulDO> listEventMatchRuls(@Param("evtRulName") String evtRulName);

}