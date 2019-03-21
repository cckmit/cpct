/**
 * @(#)TarGrpTemConditionVO.java, 2018/9/11.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.dto.channel.LabelValueVO;
import com.zjtelcom.cpct.dto.channel.OperatorDetail;

import java.util.List;

/**
 * Description:
 * author: linchao
 * date: 2018/09/11 18:49
 * version: V1.0
 */
public class TarGrpTemConditionVO extends TarGrpTemplateCondition {

    private String leftParamName;//左参名字

    private String operTypeName;//运算类型,1000> 2000< 3000==  4000!=   5000>=  6000<=  7000in   8000&   9000||   7100	not in

    private Long fitDomainId;//领域对象id

    private String promListName;
    private String labelCode;

    private String fitDomainName;//领域对象名字

    private String conditionType;

    private List<LabelValueVO> valueList;

    private List<OperatorDetail> operatorList;//运算符

    private String labelDataType;


    public String getLabelDataType() {
        return labelDataType;
    }

    public void setLabelDataType(String labelDataType) {
        this.labelDataType = labelDataType;
    }

    public String getPromListName() {
        return promListName;
    }

    public void setPromListName(String promListName) {
        this.promListName = promListName;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

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

    public Long getFitDomainId() {
        return fitDomainId;
    }

    public void setFitDomainId(Long fitDomainId) {
        this.fitDomainId = fitDomainId;
    }

    public String getFitDomainName() {
        return fitDomainName;
    }

    public void setFitDomainName(String fitDomainName) {
        this.fitDomainName = fitDomainName;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public List<LabelValueVO> getValueList() {
        return valueList;
    }

    public void setValueList(List<LabelValueVO> valueList) {
        this.valueList = valueList;
    }

    public List<OperatorDetail> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<OperatorDetail> operatorList) {
        this.operatorList = operatorList;
    }
}