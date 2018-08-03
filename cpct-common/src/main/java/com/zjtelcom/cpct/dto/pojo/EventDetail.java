package com.zjtelcom.cpct.dto.pojo;

import com.zjtelcom.cpct.dto.event.*;

import java.util.List;

public class EventDetail extends ContactEvt {
	private List<ContactEvtItem> contactEvtItems;
	private List<ContactEvtMatchRul> contactEvtMatchRuls;
	private List<ContactEvtTrigRul> contactEvtTrigRuls;
	private InterfaceCfgDetail interfaceCfgDetail;

	public InterfaceCfgDetail getInterfaceCfgDetail() {
		return interfaceCfgDetail;
	}

	public void setInterfaceCfgDetail(InterfaceCfgDetail interfaceCfgDetail) {
		this.interfaceCfgDetail = interfaceCfgDetail;
	}

	public List<ContactEvtItem> getContactEvtItems() {
		return contactEvtItems;
	}

	public void setContactEvtItems(List<ContactEvtItem> contactEvtItems) {
		this.contactEvtItems = contactEvtItems;
	}

	public List<ContactEvtMatchRul> getContactEvtMatchRuls() {
		return contactEvtMatchRuls;
	}

	public void setContactEvtMatchRuls(
			List<ContactEvtMatchRul> contactEvtMatchRuls) {
		this.contactEvtMatchRuls = contactEvtMatchRuls;
	}

	public List<ContactEvtTrigRul> getContactEvtTrigRuls() {
		return contactEvtTrigRuls;
	}

	public void setContactEvtTrigRuls(List<ContactEvtTrigRul> contactEvtTrigRuls) {
		this.contactEvtTrigRuls = contactEvtTrigRuls;
	}


}
