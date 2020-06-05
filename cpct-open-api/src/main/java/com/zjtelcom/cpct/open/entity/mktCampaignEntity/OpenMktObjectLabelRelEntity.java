package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

public class OpenMktObjectLabelRelEntity {

    private Long objectLabelRelId; //对象标签关系标识

    private Long labelId; //标签规格标识

    private String labelCode; //标签编码

    private Long labelValueId; //标签值标识

    private  String labelValue; //标签值

    private String objType; //对象类型

    private Long objId; //对象标识

    private String objNbr;//对象编码

    private String statusCd; //状态

    public Long getObjectLabelRelId() {
        return objectLabelRelId;
    }

    public void setObjectLabelRelId(Long objectLabelRelId) {
        this.objectLabelRelId = objectLabelRelId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public Long getLabelValueId() {
        return labelValueId;
    }

    public void setLabelValueId(Long labelValueId) {
        this.labelValueId = labelValueId;
    }

    public String getLabelValue() {
        return labelValue;
    }

    public void setLabelValue(String labelValue) {
        this.labelValue = labelValue;
    }

    public String getObjType() {
        return objType;
    }

    public void setObjType(String objType) {
        this.objType = objType;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getObjNbr() {
        return objNbr;
    }

    public void setObjNbr(String objNbr) {
        this.objNbr = objNbr;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

}
