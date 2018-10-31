package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.dto.event.EventMatchRulDetail;

import java.util.Map;

public interface EventMatchRulService {

    Map<String, Object> createEventMatchRul(EventMatchRulDetail eventMatchRulDetail);

    Map<String, Object> listEventMatchRulCondition(Long evtMatchRulId) throws Exception;

    Map<String, Object> modEventMatchRul(EventMatchRulDetail eventMatchRulDetail);

    Map<String, Object> delEventMatchRul(EventMatchRulDetail eventMatchRulDetail);
}
