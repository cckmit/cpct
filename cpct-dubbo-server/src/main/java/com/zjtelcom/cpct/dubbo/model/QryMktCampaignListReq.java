/**
 * @(#)QryMktCampaignListReq.java, 2018/7/17.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dubbo.model;

import com.zjtelcom.cpct.common.Page;

import java.util.Date;

/**
 * Description:
 * author: linchao
 * date: 2018/07/17 11:31
 * version: V1.0
 */
public class QryMktCampaignListReq extends BaseModel {

    /**
     * 营销活动标识
     */
    private Long mktCampaignId;
    /**
     * 营销策略标识
     */
    private Long strategyId;
    /**
     *营销活动名称
     */
    private String mktCampaignName;
    /**
     * 计划开始时间
     */
    private Date planBeginTime;
    /**
     * 计划结束时间
     */
    private Date planEndTime;
    /**
     * 实际开始时间
     */
    private Date beginTime;
    /**
     * 实际结束时间
     */
    private Date endTime;
    /**
     * 营销活动分类
     */
    private String mktCampaignType;
    /**
     * 营销活动编号
     */
    private String mktActivityNbr;
    /**
     * 营销活动目标
     */
    private String mktActivityTarget;
    /**
     * 营销活动目标
     */
    private String mktCampaignDesc;
    /**
     * 营销活动状态
     */
    private String statusCd;

    /**
     * 状态时间
     */
    private Date statusDate;
    /**
     * 创建员工
     */
    private Long createStaff;
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 更新员工
     */
    private Long updateStaff;
    /**
     * 备注
     */
    private String remark;
    /**
     * 本地网标识
     */
    private Long lanId;
    /**
     * 分页信息
     */
    private Page pageInfo;

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }

    public Long getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Long strategyId) {
        this.strategyId = strategyId;
    }

    public String getMktCampaignName() {
        return mktCampaignName;
    }

    public void setMktCampaignName(String mktCampaignName) {
        this.mktCampaignName = mktCampaignName;
    }

    public Date getPlanBeginTime() {
        return planBeginTime;
    }

    public void setPlanBeginTime(Date planBeginTime) {
        this.planBeginTime = planBeginTime;
    }

    public Date getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(Date planEndTime) {
        this.planEndTime = planEndTime;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getMktCampaignType() {
        return mktCampaignType;
    }

    public void setMktCampaignType(String mktCampaignType) {
        this.mktCampaignType = mktCampaignType;
    }

    public String getMktActivityNbr() {
        return mktActivityNbr;
    }

    public void setMktActivityNbr(String mktActivityNbr) {
        this.mktActivityNbr = mktActivityNbr;
    }

    public String getMktActivityTarget() {
        return mktActivityTarget;
    }

    public void setMktActivityTarget(String mktActivityTarget) {
        this.mktActivityTarget = mktActivityTarget;
    }

    public String getMktCampaignDesc() {
        return mktCampaignDesc;
    }

    public void setMktCampaignDesc(String mktCampaignDesc) {
        this.mktCampaignDesc = mktCampaignDesc;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }

    public Page getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Page pageInfo) {
        this.pageInfo = pageInfo;
    }
}