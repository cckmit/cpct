package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

/**
 * @Description 事件采集项实体类
 * @Author pengy
 * @Date 2018/6/26 13:54
 */
public class ContactEvtItem  {

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long evtItemId;//事件采集项主键
    private Long contactEvtId;//事件标识
    private Long evtTypeId;//事件类型标识
    private String evtItemName;//记录事件采集项的名称
    private String evtItemCode;//记录事件采集项的编码，主要用于格式化
    private String valueDataType;//记录事件采集项值数据类型，1000	日期型 1100	日期时间型 1200	字符型 1300	浮点型 1400	整数型 1500	布尔型 1600	计算型
    private String evtItemFormat;//记录事件采集项格式(正则表达式),用于事件采集项的合法性效验
    private Long isNullable;//记录事件采集项的内容是否可空。1是 0否
    private Integer evtItemLength;//记录事件采集项值的长度
    private Integer standardSort;//记录事件采集项的标准化的顺序
    private String isMainParam;//是否是主参
    private String isLabel;//是否为标签
    private String statusCd;//记录状态。1000有效 1100无效  1200	未生效 1300已归档  1001将生效  1002待恢复  1101将失效  1102待失效 1301	待撤消
    private Long createStaff;//创建人
    private Long updateStaff;//更新人
    private Date createDate;//创建时间
    private Date statusDate;//状态时间
    private Date updateDate;//更新时间
    private String remark;//备注
    private Long lanId;//本地网标识


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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
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

    public Long getLanId() {
        return lanId;
    }

    public void setLanId(Long lanId) {
        this.lanId = lanId;
    }

    public String getIsLabel() {
        return isLabel;
    }

    public void setIsLabel(String isLabel) {
        this.isLabel = isLabel;
    }

    public String getIsMainParam() {
        return isMainParam;
    }

    public void setIsMainParam(String isMainParam) {
        this.isMainParam = isMainParam;
    }


    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

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
}
