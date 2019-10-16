package com.zjtelcom.cpct.open.entity.mktStrategy;

/**
* @Auther: anson
* @Date: 2018-11-02 14:30:02
* @Description:营销委婉策略  实体类 匹配集团openapi规范返回
 * //如果设计到时间字段  请设置为String类型
*/
//@Data
public class OpenMktStrategy {

    /**
     * 营销策略标识
     */
    private Integer strategyId;
    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 策略类型
     */
    private String strategyType;

    /**
     * 策略内容
     */
    private String strategyDesc;

    /**
     * 计算表达式
     */
    private String ruleExpression;

    private String statusDate;//状态时间
    private String remark;//备注
    private String lanId;//本地网标识

    public Integer getStrategyId() {
        return strategyId;
    }

    public void setStrategyId(Integer strategyId) {
        this.strategyId = strategyId;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public String getStrategyDesc() {
        return strategyDesc;
    }

    public void setStrategyDesc(String strategyDesc) {
        this.strategyDesc = strategyDesc;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getLanId() {
        return lanId;
    }

    public void setLanId(String lanId) {
        this.lanId = lanId;
    }
}
