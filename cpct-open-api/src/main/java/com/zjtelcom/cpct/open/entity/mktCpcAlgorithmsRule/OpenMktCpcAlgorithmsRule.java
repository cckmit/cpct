package com.zjtelcom.cpct.open.entity.mktCpcAlgorithmsRule;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;


/**
 * @Auther: anson
 * @Date: 2018-11-07 15:05:02
 * @Description:CPC算法规则 实体类 匹配集团openapi规范返回
 */
public class OpenMktCpcAlgorithmsRule extends BaseEntity {

    private String statusDate;//状态时间

    /**
     * 算法规则名称
     */
    private String algorithmsRulName;

    /**
     * 规则描述
     */
    private String ruleDesc;

    private String statusCd;//记录状态。1000有效 1100无效  1200	未生效 1300已归档  1001将生效  1002待恢复  1101将失效  1102待失效 1301	待撤消

    private String remark;//备注

    /**
     * 计算表达式
     */
    private String ruleExpression;


    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }


    public String getAlgorithmsRulName() {
        return algorithmsRulName;
    }

    public void setAlgorithmsRulName(String algorithmsRulName) {
        this.algorithmsRulName = algorithmsRulName;
    }

    public String getRuleDesc() {
        return ruleDesc;
    }

    public void setRuleDesc(String ruleDesc) {
        this.ruleDesc = ruleDesc;
    }

    public String getStatusCd() {
        return statusCd;
    }

    public void setStatusCd(String statusCd) {
        this.statusCd = statusCd;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRuleExpression() {
        return ruleExpression;
    }

    public void setRuleExpression(String ruleExpression) {
        this.ruleExpression = ruleExpression;
    }
}