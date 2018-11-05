package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;
import java.util.List;

public class EventMatchRulDetail extends EventMatchRulDTO implements Serializable {

    private List<EventMatchRulCondition> eventMatchRulConditions;

    private List<EventMatchRulConditionVO> eventMatchRulConditionVOS;

    public List<EventMatchRulCondition> getEventMatchRulConditions() {
        return eventMatchRulConditions;
    }

    public void setEventMatchRulConditions(List<EventMatchRulCondition> eventMatchRulConditions) {
        this.eventMatchRulConditions = eventMatchRulConditions;
    }
    public List<EventMatchRulConditionVO> getEventMatchRulConditionVOS() {
        return eventMatchRulConditionVOS;
    }

    public void setEventMatchRulConditionVOS(List<EventMatchRulConditionVO> eventMatchRulConditionVOS) {
        this.eventMatchRulConditionVOS = eventMatchRulConditionVOS;
    }
}
