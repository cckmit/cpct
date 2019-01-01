package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

public class EventItem extends BaseEntity {
    private Long evtItemId;

    private Long contactEvtId;

    private Long evtTypeId;

    private String evtItemName;

    private String evtItemCode;

    private String valueDataType;

    private String evtItemFormat;

    private Long isNullable;

    private Integer evtItemLength;

    private Integer standardSort;

    private String isMainParam;

    private String evtItemDesc;


    private String isLabel;

    private String remark;

    public Long getEvtItemId() {
        return evtItemId;
    }

    public void setEvtItemId(Long evtItemId) {
        this.evtItemId = evtItemId;
    }

    public Long getContactEvtId() {
        return contactEvtId;
    }

    public void setContactEvtId(Long contactEvtId) {
        this.contactEvtId = contactEvtId;
    }

    public Long getEvtTypeId() {
        return evtTypeId;
    }

    public void setEvtTypeId(Long evtTypeId) {
        this.evtTypeId = evtTypeId;
    }

    public String getEvtItemName() {
        return evtItemName;
    }

    public void setEvtItemName(String evtItemName) {
        this.evtItemName = evtItemName;
    }

    public String getEvtItemCode() {
        return evtItemCode;
    }

    public void setEvtItemCode(String evtItemCode) {
        this.evtItemCode = evtItemCode;
    }

    public String getValueDataType() {
        return valueDataType;
    }

    public void setValueDataType(String valueDataType) {
        this.valueDataType = valueDataType;
    }

    public String getEvtItemFormat() {
        return evtItemFormat;
    }

    public void setEvtItemFormat(String evtItemFormat) {
        this.evtItemFormat = evtItemFormat;
    }

    public Long getIsNullable() {
        return isNullable;
    }

    public void setIsNullable(Long isNullable) {
        this.isNullable = isNullable;
    }

    public Integer getEvtItemLength() {
        return evtItemLength;
    }

    public void setEvtItemLength(Integer evtItemLength) {
        this.evtItemLength = evtItemLength;
    }

    public Integer getStandardSort() {
        return standardSort;
    }

    public void setStandardSort(Integer standardSort) {
        this.standardSort = standardSort;
    }

    public String getIsMainParam() {
        return isMainParam;
    }

    public void setIsMainParam(String isMainParam) {
        this.isMainParam = isMainParam;
    }

    public String getEvtItemDesc() {
        return evtItemDesc;
    }

    public void setEvtItemDesc(String evtItemDesc) {
        this.evtItemDesc = evtItemDesc;
    }

    public String getIsLabel() {
        return isLabel;
    }

    public void setIsLabel(String isLabel) {
        this.isLabel = isLabel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}