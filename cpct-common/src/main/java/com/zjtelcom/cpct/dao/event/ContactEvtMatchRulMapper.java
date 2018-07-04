package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ContactEvtMatchRulMapper {

    int deleteByPrimaryKey(Long evtMatchRulId);

    int insert(EventMatchRulDO record);

    EventMatchRulDO selectByPrimaryKey(Long evtMatchRulId);

    List<EventMatchRulDO> selectAll();

    int updateByPrimaryKey(EventMatchRulDO record);

    List<ContactEvtMatchRul> listEventMatchRuls(ContactEvtMatchRul contactEvtMatchRul);

    int createContactEvtMatchRul(ContactEvtMatchRul contactEvtMatchRul);

    int modContactEvtMatchRul(ContactEvtMatchRul contactEvtMatchRul);

}