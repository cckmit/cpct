package com.zjtelcom.cpct.dto.pojo;

import java.io.Serializable;
import java.util.List;

public class EventPo implements Serializable {
	private RequestTemplateInst requestTemplateInst;
	private EventDetail contactEvtDetails;
	
	public RequestTemplateInst getRequestTemplateInst() {
		return requestTemplateInst;
	}

	public void setRequestTemplateInst(RequestTemplateInst requestTemplateInst) {
		this.requestTemplateInst = requestTemplateInst;
	}

	public EventDetail getContactEvtDetails() {
		return contactEvtDetails;
	}

	public void setContactEvtDetails(EventDetail contactEvtDetails) {
		this.contactEvtDetails = contactEvtDetails;
	}
}
