package com.zjtelcom.cpct.dto.filter;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 规律规则实体类
 * @Author pengy
 * @Date 2018/6/26 15:32
 */
public class CloseRule extends BaseEntity implements Serializable{

    private Long ruleId;//规则标识
    private String closeName;//关单名称
    private String closeType;//关单类型
    //受理关单	2000
    //欠费关单	3000
    //拆机关单	1000
    //账期欠费关单	4000
    //标签关单	5000
    private String productType;// 产品类型
    private String channelContacts;//渠道执行次数
    private Date effectiveDate;//生效时间
    private Date failureDate;//失效时间
    private String labelCode;//
    private String chooseProduct;//选择的销售品
    private String expression;
    private Date dayStart;
    private Date dayEnd;
    private String userList;
    private Long conditionId;
    private String days;
    private String times;
    private String offerInfo;
    private String closeCode; //成功标识
    private String noteOne;
    private String noteTwo;
    private String noteThree;


    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTimes() {
        return times;
    }

    public void setTimes(String times) {
        this.times = times;
    }

    public String getOfferInfo() {
        return offerInfo;
    }

    public void setOfferInfo(String offerInfo) {
        this.offerInfo = offerInfo;
    }

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public Date getDayStart() {
        return dayStart;
    }

    public void setDayStart(Date dayStart) {
        this.dayStart = dayStart;
    }

    public Date getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(Date dayEnd) {
        this.dayEnd = dayEnd;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getChooseProduct() {
        return chooseProduct;
    }

    public void setChooseProduct(String chooseProduct) {
        this.chooseProduct = chooseProduct;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getChannelContacts() {
        return channelContacts;
    }

    public void setChannelContacts(String channelContacts) {
        this.channelContacts = channelContacts;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getFailureDate() {
        return failureDate;
    }

    public void setFailureDate(Date failureDate) {
        this.failureDate = failureDate;
    }

    public String getCloseName() {
        return closeName;
    }

    public void setCloseName(String closeName) {
        this.closeName = closeName;
    }

    public String getCloseType() {
        return closeType;
    }

    public void setCloseType(String closeType) {
        this.closeType = closeType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getCloseCode() {
        return closeCode;
    }

    public void setCloseCode(String closeCode) {
        this.closeCode = closeCode;
    }

    public String getNoteOne() {
        return noteOne;
    }

    public void setNoteOne(String noteOne) {
        this.noteOne = noteOne;
    }

    public String getNoteTwo() {
        return noteTwo;
    }

    public void setNoteTwo(String noteTwo) {
        this.noteTwo = noteTwo;
    }

    public String getNoteThree() {
        return noteThree;
    }

    public void setNoteThree(String noteThree) {
        this.noteThree = noteThree;
    }
}