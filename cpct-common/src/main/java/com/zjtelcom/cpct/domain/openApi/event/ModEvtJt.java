package com.zjtelcom.cpct.domain.openApi.event;

import com.zjtelcom.cpct.open.entity.RequestTemplateInst;

import java.util.List;

public class ModEvtJt {

    private List<OpenEvent> eventDetails;
    private RequestTemplateInst requestTemplateInst;

    public List<OpenEvent> getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(List<OpenEvent> eventDetails) {
        this.eventDetails = eventDetails;
    }

    public RequestTemplateInst getRequestTemplateInst() {
        return requestTemplateInst;
    }

    public void setRequestTemplateInst(RequestTemplateInst requestTemplateInst) {
        this.requestTemplateInst = requestTemplateInst;
    }

}
