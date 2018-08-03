package com.zjtelcom.cpct.dto.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zjtelcom.cpct.dto.event.Catalog;

import java.util.List;

public class CatalogDetail extends Catalog {

	private String actType;
	private List<CatalogItemPo> catalogItems;


	public List<CatalogItemPo> getCatalogItems() {
		return catalogItems;
	}

	public void setCatalogItems(List<CatalogItemPo> catalogItems) {
		this.catalogItems = catalogItems;
	}

	public String getActType() {
		return actType;
	}

	public void setActType(String actType) {
		this.actType = actType;
	}

}
