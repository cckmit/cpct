package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.MktOfferEventDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MktOfferEventMapper {
    List<MktOfferEventDO> getEventIdByOfferNbr(@Param("offerNbr") String offerNbr, @Param("eventType") int eventType);
    Long selectInitIdByOfferNbr(@Param("offerNbr") String offerNbr);
    Long selectMktIdByInitId(@Param("initId") Long initId);
    List<MktOfferEventDO> getEventIdByCamId(@Param("mktCamId") Long mktCamId, @Param("eventType") int eventType );

}
