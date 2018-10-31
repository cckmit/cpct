package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;
import java.util.List;

public class EventMatchRulVO implements Serializable {

    private String fitDomainName;//领域中文名
    private List<EventMatchRulConditionVO> eventMatchRulConditionVOList;//规则条件

    public String getFitDomainName() {
        return fitDomainName;
    }

    public void setFitDomainName(String fitDomainName) {
        this.fitDomainName = fitDomainName;
    }

    public List<EventMatchRulConditionVO> getEventMatchRulConditionVOList() {
        return eventMatchRulConditionVOList;
    }

    public void setEventMatchRulConditionVOList(List<EventMatchRulConditionVO> eventMatchRulConditionVOList) {
        this.eventMatchRulConditionVOList = eventMatchRulConditionVOList;
    }
}
