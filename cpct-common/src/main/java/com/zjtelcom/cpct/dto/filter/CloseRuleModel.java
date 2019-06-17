package com.zjtelcom.cpct.dto.filter;

import java.io.Serializable;

/**
 * @Description 过滤规则实体类
 * @Author linchao
 * @Date 2018/10/10 09:32
 */
public class CloseRuleModel implements Serializable {

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
}