package com.zjtelcom.cpct_prd.dao.event;

import com.zjtelcom.cpct.dto.event.EventMatchRulDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EventMatchRulPrdMapper {

    int createEventMatchRul(EventMatchRulDTO eventMatchRulDTO);

    int modEventMatchRul(EventMatchRulDTO eventMatchRulDTO);

    int delEventMatchRul(EventMatchRulDTO eventMatchRulDTO);

    EventMatchRulDTO listEventMatchRul(Long eventId);

    int updateByPrimaryKey(EventMatchRulDTO eventMatchRulDTO);

}
