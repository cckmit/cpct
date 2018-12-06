package com.zjtelcom.cpct.dto.pojo;

import java.io.Serializable;
import java.util.List;

public class CatalogDetailPo implements Serializable {

	private List<CatalogDetail> eventCatalogDetails;

	public List<CatalogDetail> getEventCatalogDetails() {
		return eventCatalogDetails;
	}

	public void setEventCatalogDetails(List<CatalogDetail> eventCatalogDetails) {
		this.eventCatalogDetails = eventCatalogDetails;
	}
}
