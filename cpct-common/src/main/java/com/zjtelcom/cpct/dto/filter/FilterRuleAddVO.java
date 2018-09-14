package com.zjtelcom.cpct.dto.filter;

import java.io.Serializable;
import java.util.Date;

public class FilterRuleAddVO implements Serializable {

    private String ruleName;//规则名称
    private String filterType;//过滤类型
    private Date effectiveDate;//生效时间
    private Date failureDate;//失效时间
    private String[] chooseProduct;//选择的销售品
    private String expression;
    private String dayStart;
    private String dayEnd;

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

    public String[] getChooseProduct() {
        return chooseProduct;
    }

    public void setChooseProduct(String[] chooseProduct) {
        this.chooseProduct = chooseProduct;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getDayStart() {
        return dayStart;
    }

    public void setDayStart(String dayStart) {
        this.dayStart = dayStart;
    }

    public String getDayEnd() {
        return dayEnd;
    }

    public void setDayEnd(String dayEnd) {
        this.dayEnd = dayEnd;
    }
}
