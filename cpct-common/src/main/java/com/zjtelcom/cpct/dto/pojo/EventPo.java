package com.zjtelcom.cpct.dto.pojo;

import java.util.List;

public class EventPo {
	private RequestTemplateInst requestTemplateInst;
	private List<EventDetail> contactEvtDetails;
	
	public RequestTemplateInst getRequestTemplateInst() {
		return requestTemplateInst;
	}

	public void setRequestTemplateInst(RequestTemplateInst requestTemplateInst) {
		this.requestTemplateInst = requestTemplateInst;
	}

	public List<EventDetail> getContactEvtDetails() {
		return contactEvtDetails;
	}

	public void setContactEvtDetails(List<EventDetail> contactEvtDetails) {
		this.contactEvtDetails = contactEvtDetails;
	}
	
}
