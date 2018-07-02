package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.List;

@Mapper
@Repository
public interface ContactEvtTypeMapper {
    int deleteByPrimaryKey(Long evtTypeId);

    int insert(EventTypeDO record);

    ContactEvtType selectByPrimaryKey(Long evtTypeId);

    List<EventTypeDO> selectAll();

    int modContactEvtType(ContactEvtType contactEvtType);

    List<ContactEvtType> qryContactEvtTypeList(QryContactEvtTypeReq qryContactEvtTypeReq);

    int createContactEvtType(ContactEvtType contactEvtType);
}