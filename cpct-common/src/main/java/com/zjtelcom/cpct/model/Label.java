package com.zjtelcom.cpct.model;

public class Label {
    private Integer conditionId;

    private String conditionName;

    private String conditionType;

    private String description;

    private String leftOperand;

    private String operator;

    private Integer valueId;

    private String eagleName;

    private Integer isShared;

    
    public Integer getIsShared() {
		return isShared;
	}

	public void setIsShared(Integer isShared) {
		this.isShared = isShared;
	}

	public Integer getConditionId() {
        return conditionId;
    }

    public void setConditionId(Integer conditionId) {
        this.conditionId = conditionId;
    }

    public String getConditionName() {
        return conditionName;
    }

    public void setConditionName(String conditionName) {
        this.conditionName = conditionName == null ? null : conditionName.trim();
    }
    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType == null ? null : conditionType.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getLeftOperand() {
        return leftOperand;
    }

    public void setLeftOperand(String leftOperand) {
        this.leftOperand = leftOperand == null ? null : leftOperand.trim();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator == null ? null : operator.trim();
    }

    public Integer getValueId() {
        return valueId;
    }

    public void setValueId(Integer valueId) {
        this.valueId = valueId;
    }

    public String getEagleName() {
        return eagleName;
    }

    public void setEagleName(String eagleName) {
        this.eagleName = eagleName;
    }
}