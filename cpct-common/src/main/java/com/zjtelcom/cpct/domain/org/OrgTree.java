package com.zjtelcom.cpct.domain.org;

import java.io.Serializable;
import java.util.Date;

/**
 * 营销组织树
 */
public class OrgTree implements Serializable {
    /** 营销区域*/
    private Integer areaId;

    /** 营销区域名称*/
    private String areaName;

    /** 上级营销区域*/
    private Integer sumAreaId;

    /** */
    private Integer areaTypeId;

    /** 状态*/
    private String state;

    /** 状态变更时间*/
    private Date stateDate;

    /** 本地网*/
    private Integer latnid;

    /** */
    private Integer serveTypeId;

    /** 城市农村标识*/
    private String cityType;

    /** */
    private String viewTreeFlag;

    /** */
    private String gridFlg;

    /** */
    private String zjAreaFlg;

    /** */
    private String bzjdFlg;

    /** */
    private String standardCode;

    /** */
    private String xnFlag;

    /** */
    private String bmFlag;

    /** */
    private String areaType;

    /** */
    private String orderId;

    /** */
    private String srzxFlg;

    /** */
    private String cbjdFlg;

    /** */
    private String typeName;

    /** */
    private String comments;

    /** 集团编码*/
    private String groupCode;

    /** 是否结算节点*/
    private String cbjsFlg;

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public Integer getSumAreaId() {
        return sumAreaId;
    }

    public void setSumAreaId(Integer sumAreaId) {
        this.sumAreaId = sumAreaId;
    }

    public Integer getAreaTypeId() {
        return areaTypeId;
    }

    public void setAreaTypeId(Integer areaTypeId) {
        this.areaTypeId = areaTypeId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Date getStateDate() {
        return stateDate;
    }

    public void setStateDate(Date stateDate) {
        this.stateDate = stateDate;
    }

    public Integer getLatnid() {
        return latnid;
    }

    public void setLatnid(Integer latnid) {
        this.latnid = latnid;
    }

    public Integer getServeTypeId() {
        return serveTypeId;
    }

    public void setServeTypeId(Integer serveTypeId) {
        this.serveTypeId = serveTypeId;
    }

    public String getCityType() {
        return cityType;
    }

    public void setCityType(String cityType) {
        this.cityType = cityType;
    }

    public String getViewTreeFlag() {
        return viewTreeFlag;
    }

    public void setViewTreeFlag(String viewTreeFlag) {
        this.viewTreeFlag = viewTreeFlag;
    }

    public String getGridFlg() {
        return gridFlg;
    }

    public void setGridFlg(String gridFlg) {
        this.gridFlg = gridFlg;
    }

    public String getZjAreaFlg() {
        return zjAreaFlg;
    }

    public void setZjAreaFlg(String zjAreaFlg) {
        this.zjAreaFlg = zjAreaFlg;
    }

    public String getBzjdFlg() {
        return bzjdFlg;
    }

    public void setBzjdFlg(String bzjdFlg) {
        this.bzjdFlg = bzjdFlg;
    }

    public String getStandardCode() {
        return standardCode;
    }

    public void setStandardCode(String standardCode) {
        this.standardCode = standardCode;
    }

    public String getXnFlag() {
        return xnFlag;
    }

    public void setXnFlag(String xnFlag) {
        this.xnFlag = xnFlag;
    }

    public String getBmFlag() {
        return bmFlag;
    }

    public void setBmFlag(String bmFlag) {
        this.bmFlag = bmFlag;
    }

    public String getAreaType() {
        return areaType;
    }

    public void setAreaType(String areaType) {
        this.areaType = areaType;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSrzxFlg() {
        return srzxFlg;
    }

    public void setSrzxFlg(String srzxFlg) {
        this.srzxFlg = srzxFlg;
    }

    public String getCbjdFlg() {
        return cbjdFlg;
    }

    public void setCbjdFlg(String cbjdFlg) {
        this.cbjdFlg = cbjdFlg;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getCbjsFlg() {
        return cbjsFlg;
    }

    public void setCbjsFlg(String cbjsFlg) {
        this.cbjsFlg = cbjsFlg;
    }

    @Override
    public String toString() {
        return "OrgTree{" +
                "areaId=" + areaId +
                ", areaName='" + areaName + '\'' +
                ", sumAreaId=" + sumAreaId +
                ", areaTypeId=" + areaTypeId +
                ", state='" + state + '\'' +
                ", stateDate=" + stateDate +
                ", latnid=" + latnid +
                ", serveTypeId=" + serveTypeId +
                ", cityType='" + cityType + '\'' +
                ", viewTreeFlag='" + viewTreeFlag + '\'' +
                ", gridFlg='" + gridFlg + '\'' +
                ", zjAreaFlg='" + zjAreaFlg + '\'' +
                ", bzjdFlg='" + bzjdFlg + '\'' +
                ", standardCode='" + standardCode + '\'' +
                ", xnFlag='" + xnFlag + '\'' +
                ", bmFlag='" + bmFlag + '\'' +
                ", areaType='" + areaType + '\'' +
                ", orderId='" + orderId + '\'' +
                ", srzxFlg='" + srzxFlg + '\'' +
                ", cbjdFlg='" + cbjdFlg + '\'' +
                ", typeName='" + typeName + '\'' +
                ", comments='" + comments + '\'' +
                ", groupCode='" + groupCode + '\'' +
                ", cbjsFlg='" + cbjsFlg + '\'' +
                '}';
    }
}