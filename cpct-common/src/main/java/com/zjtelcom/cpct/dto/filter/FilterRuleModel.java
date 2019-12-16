package com.zjtelcom.cpct.dto.filter;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description 过滤规则实体类
 * @Author linchao
 * @Date 2018/10/10 09:32
 */
public class FilterRuleModel implements Serializable {

    /**
     * 规则标识
     */
    private Long ruleId;
    /**
     * 规则名称
     */
    private String ruleName;
    /**
     * 过滤类型
     */
    private String filterType;
    /**
     * 标签编码
     */
    private String labelCode;
    /**
     * 标签名称
     */
    private String labelName;
    /**
     * 运算类型,1000> 2000< 3000==  4000!=   5000>=  6000<=  7000in   8000&   9000||   7100	not in
     */
    private String operType;
    /**
     * 右参数（参考值)
     */
    private String rightParam;
    /**
     * 推送天数
     */
    private String days;
    /**
     * 推送次数
     */
    private String times;

    private String executionChannel;// 关单销售品类型（1000：销售品；2000：主产品；3000 子产品） --冗余字段再使用
    private String channelContacts;//渠道执行次数
    private Date effectiveDate;//生效时间
    private Date failureDate;//失效时间
    private String chooseProduct;//选择的销售品
    private String expression;
    private Date dayStart;
    private Date dayEnd;
    private String userList;
    private Long conditionId;
    private String offerInfo;
    private String operator;

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

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getOperType() {
        return operType;
    }

    public void setOperType(String operType) {
        this.operType = operType;
    }

    public String getRightParam() {
        return rightParam;
    }

    public void setRightParam(String rightParam) {
        this.rightParam = rightParam;
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

    public String getExecutionChannel() {
        return executionChannel;
    }

    public void setExecutionChannel(String executionChannel) {
        this.executionChannel = executionChannel;
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

    public String getUserList() {
        return userList;
    }

    public void setUserList(String userList) {
        this.userList = userList;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public String getOfferInfo() {
        return offerInfo;
    }

    public void setOfferInfo(String offerInfo) {
        this.offerInfo = offerInfo;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}