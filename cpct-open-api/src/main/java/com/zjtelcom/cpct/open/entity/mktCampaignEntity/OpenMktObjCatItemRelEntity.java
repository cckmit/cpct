package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

public class OpenMktObjCatItemRelEntity {

    private String catalogItemId;//目录节点标识

    private String objType;//对象类型

    private Long relId;//对象目录节点关系标识

    private String statusCd;//状态






    public String getCatalogItemId() {
        return catalogItemId;
    }

    public void setCatalogItemId(String catalogItemId) {
        this.catalogItemId = catalogItemId;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }
}
