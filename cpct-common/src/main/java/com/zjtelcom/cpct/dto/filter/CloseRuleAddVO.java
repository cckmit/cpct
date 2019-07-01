package com.zjtelcom.cpct.dto.filter;

import com.zjtelcom.cpct.dto.channel.VerbalConditionAddVO;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CloseRuleAddVO implements Serializable {

    private Long ruleId;//规则标识
    private String closeName;//关单名称
    private String closeType;//关单类型
    private String productType;// 产品类型
    private Date effectiveDate;//生效时间
    private Date failureDate;//失效时间
    private List<Long> chooseProduct;//选择的销售品
    private String expression;
    private Date dayStart;
    private Date dayEnd;
    private VerbalConditionAddVO condition;//过扰规则条件
    private String days;
    private String times;
    private String offerInfo;
    private String closeCode; //成功标识
    private String remark;
    private String noteOne;
    private String noteTwo;
    private String noteThree;
    private String labelCode;

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public VerbalConditionAddVO getCondition() {
        return condition;
    }

    public void setCondition(VerbalConditionAddVO condition) {
        this.condition = condition;
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

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
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

    public List<Long> getChooseProduct() {
        return chooseProduct;
    }

    public void setChooseProduct(List<Long> chooseProduct) {
        this.chooseProduct = chooseProduct;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
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
