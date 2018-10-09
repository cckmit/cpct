package com.zjtelcom.cpct.dto.filter;

import com.zjtelcom.cpct.dto.channel.VerbalConditionAddVO;
import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class FilterRuleAddVO implements Serializable {

    private Long ruleId;//规则标识
    private String ruleName;//规则名称
    private String filterType;//过滤类型
    private Date effectiveDate;//生效时间
    private Date failureDate;//失效时间
    private List<Long> chooseProduct;//选择的销售品
    private String expression;
    private Date dayStart;
    private Date dayEnd;
    private String userList;
    private VerbalConditionAddVO condition;//过扰规则条件
    private String days;
    private String times;
    private String offerInfo;


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

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
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


}
