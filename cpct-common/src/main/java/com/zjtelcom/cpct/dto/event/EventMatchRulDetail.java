package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;
import java.util.List;

public class EventMatchRulDetail extends EventMatchRulDTO implements Serializable {

    private List<EventMatchRulCondition> EventMatchRulConditions;

    public List<EventMatchRulCondition> getEventMatchRulConditions() {
        return EventMatchRulConditions;
    }

    public void setEventMatchRulConditions(List<EventMatchRulCondition> eventMatchRulConditions) {
        EventMatchRulConditions = eventMatchRulConditions;
    }
}
