package com.zjtelcom.cpct.vo.grouping;

import com.zjtelcom.cpct.dto.grouping.TarGrpCondition;

/**
 * @Description 目标分群条件 VO
 * @Author pengy
 * @Date 2018/6/29 16:36
 */
public class TarGrpConditionVO extends TarGrpCondition{

    private String leftParamName;//左参名字
    private String operTypeName;//运算类型,1000> 2000< 3000==  4000!=   5000>=  6000<=  7000in   8000&   9000||   7100	not in
    private String fitDomainName;//领域对象中文

    public String getLeftParamName() {
        return leftParamName;
    }

    public void setLeftParamName(String leftParamName) {
        this.leftParamName = leftParamName;
    }

    public String getOperTypeName() {
        return operTypeName;
    }

    public void setOperTypeName(String operTypeName) {
        this.operTypeName = operTypeName;
    }

    public String getFitDomainName() {
        return fitDomainName;
    }

    public void setFitDomainName(String fitDomainName) {
        this.fitDomainName = fitDomainName;
    }
}
