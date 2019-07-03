package com.zjtelcom.cpct.open.entity.mktCampaignEntity;

import com.zjtelcom.cpct.open.entity.tarGrp.OpenTarGrpEntity;

import java.util.Date;

public class OpenMktCamGrpRulEntity {

    private String actType;
    private Long mktCamGrpRulId;
    private Long mktCampaignId;
    private String mktActivityNbr;
    private Long tarGrpId;
    private OpenTarGrpEntity tarGrp;
    private String statusCd;
    private Date statusDate;
    private Long createStaff;
    private Date createDate;
    private Long updateStaff;
    private Date updateDate;
    private Long lanId;

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getMktCamGrpRulId() {
        return mktCamGrpRulId;
    }

    public void setMktCamGrpRulId(Long mktCamGrpRulId) {
        this.mktCamGrpRulId = mktCamGrpRulId;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
    }

    public Long getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public OpenTarGrpEntity getTarGrp() {
        return tarGrp;
    }

    public void setTarGrp(OpenTarGrpEntity tarGrp) {
        this.tarGrp = tarGrp;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Long getCreateStaff() {
        return createStaff;
    }

    public void setCreateStaff(Long createStaff) {
        this.createStaff = createStaff;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Long getUpdateStaff() {
        return updateStaff;
    }

    public void setUpdateStaff(Long updateStaff) {
        this.updateStaff = updateStaff;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }
}
