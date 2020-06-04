package com.zjtelcom.cpct.dto.channel;

import java.util.List;

public class CatalogItemDetail {

    private Long catalogItemId;

    private Long catalogId;

    private Long parCatalogItemId;

    private String catalogItemName;

    private String catalogItemDesc;

    private String catalogItemType;

    private String catalogItemNbr;

    private List<CatalogItemDetail> childList;


    public Long getCatalogItemId() {
        return catalogItemId;
    }

    public void setCatalogItemId(Long catalogItemId) {
        this.catalogItemId = catalogItemId;
    }

    public Long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(Long catalogId) {
        this.catalogId = catalogId;
    }

    public Long getParCatalogItemId() {
        return parCatalogItemId;
    }

    public void setParCatalogItemId(Long parCatalogItemId) {
        this.parCatalogItemId = parCatalogItemId;
    }

    public String getCatalogItemName() {
        return catalogItemName;
    }

    public void setCatalogItemName(String catalogItemName) {
        this.catalogItemName = catalogItemName;
    }

    public String getCatalogItemDesc() {
        return catalogItemDesc;
    }

    public void setCatalogItemDesc(String catalogItemDesc) {
        this.catalogItemDesc = catalogItemDesc;
    }

    public String getCatalogItemType() {
        return catalogItemType;
    }

    public void setCatalogItemType(String catalogItemType) {
        this.catalogItemType = catalogItemType;
    }

    public String getCatalogItemNbr() {
        return catalogItemNbr;
    }

    public void setCatalogItemNbr(String catalogItemNbr) {
        this.catalogItemNbr = catalogItemNbr;
    }

    public List<CatalogItemDetail> getChildList() {
        return childList;
    }

    public void setChildList(List<CatalogItemDetail> childList) {
        this.childList = childList;
    }
}
