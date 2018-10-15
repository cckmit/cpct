package com.zjtelcom.cpct.dto.pojo;

import com.zjtelcom.cpct.dto.event.EventSceneDetail;

import java.io.Serializable;
import java.util.List;

public class EventScenePo implements Serializable {

	
	private RequestTemplateInst requestTemplateInst;
	
	private List<EventSceneDetail> eventSceneDetails;

	public RequestTemplateInst getRequestTemplateInst() {
		return requestTemplateInst;
	}

	public void setRequestTemplateInst(RequestTemplateInst requestTemplateInst) {
		this.requestTemplateInst = requestTemplateInst;
	}

	public List<EventSceneDetail> getEventSceneDetails() {
		return eventSceneDetails;
	}

	public void setEventSceneDetails(List<EventSceneDetail> eventSceneDetails) {
		this.eventSceneDetails = eventSceneDetails;
	}

	
}
