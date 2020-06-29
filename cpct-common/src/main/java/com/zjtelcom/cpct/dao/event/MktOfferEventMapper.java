package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.MktOfferEventDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MktOfferEventMapper {
    List<MktOfferEventDO> getEventIdByOfferNbr(@Param("offerNbr") String offerNbr, @Param("eventType") int eventType);
}