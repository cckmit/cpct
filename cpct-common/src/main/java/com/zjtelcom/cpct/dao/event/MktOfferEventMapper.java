package com.zjtelcom.cpct.dao.event;

import com.zjtelcom.cpct.domain.event.MktOfferEventDO;

import java.util.List;

public interface MktOfferEventMapper {
    List<MktOfferEventDO> getEventIdByOfferNbr(int offerNbr, int eventType);
}
