package com.zjtelcom.cpct.open.service.event;

import com.zjtelcom.cpct.open.base.service.BaseDao;
import com.zjtelcom.cpct.open.entity.event.ModEvtJt;

import java.util.Map;

public interface OpenEventService extends BaseDao {

    Map<String,Object> updateEvent(ModEvtJt modEvtJt);
}
