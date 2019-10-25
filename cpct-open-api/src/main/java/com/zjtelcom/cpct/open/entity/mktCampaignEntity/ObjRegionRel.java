package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import java.util.Date;

public class ObjRegionRel {

    private String actType;//通用数据操作类型
    private Long objRegionRelId;//对象区域关系标识
    private String objNbr;//对象编码
    private Long objId;//对象标识
    private Long applyRegionId;//适用区域标识
    private String applyRegionNbr;//适用区域编码
    private String statusCd;//状态
    private Long createStaff;
    private Long updateStaff;
    private String createStaffCode;//创建人编码
    private String updateStaffCode;//修改人编码
    private Date statusDate;
    private Date createDate;
    private Date updateDate;
    private String remark;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getObjRegionRelId() {
        return objRegionRelId;
    }

    public void setObjRegionRelId(Long objRegionRelId) {
        this.objRegionRelId = objRegionRelId;
    }

    public String getObjNbr() {
        return objNbr;
    }

    public void setObjNbr(String objNbr) {
        this.objNbr = objNbr;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Long getApplyRegionId() {
        return applyRegionId;
    }

    public void setApplyRegionId(Long applyRegionId) {
        this.applyRegionId = applyRegionId;
    }

    public String getApplyRegionNbr() {
        return applyRegionNbr;
    }

    public void setApplyRegionNbr(String applyRegionNbr) {
        this.applyRegionNbr = applyRegionNbr;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public String getCreateStaffCode() {
        return createStaffCode;
    }

    public void setCreateStaffCode(String createStaffCode) {
        this.createStaffCode = createStaffCode;
    }

    public String getUpdateStaffCode() {
        return updateStaffCode;
    }

    public void setUpdateStaffCode(String updateStaffCode) {
        this.updateStaffCode = updateStaffCode;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
