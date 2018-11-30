package com.zjtelcom.cpct.dto.grouping;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 目标分群条件
 * @Author pengy
 * @Date 2018/6/29 16:36
 */
public class TarGrpCondition extends BaseEntity implements Serializable{

    private static final long serialVersionUID = -6960754092598592158L;
    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long conditionId; //目标分群条件标识
    private Long tarGrpId;//目标分群标识
    private Long rootFlag;//是否是根条件，1是 0	否
    private String leftParam;//左边参数，类型为注智标签时，对应为注智标签标识；类型为表达式时，为分群条件ID
    private String leftParamType;//参数类型1000	注智标签 2000	表达式 3000	固定值
    private String operType;//运算类型,1000> 2000< 3000==  4000!=   5000>=  6000<=  7000in   8000&   9000||   7100	not in
    private String rightParam;//右边参数，类型为注智标签时，对应为注智标签标识；类型为表达式时，为分群条件ID
    private String rightParamType;//参数类型1000	注智标签 2000	表达式 3000	固定值
    private String conditionText;//条件表达式的业务含义，用于前台展示
    private List<Integer> areaIdList;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public List<Integer> getAreaIdList() {
        return areaIdList;
    }

    public void setAreaIdList(List<Integer> areaIdList) {
        this.areaIdList = areaIdList;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

    public Long getConditionId() {
        return conditionId;
    }

    public void setConditionId(Long conditionId) {
        this.conditionId = conditionId;
    }

    public Long getTarGrpId() {
        return tarGrpId;
    }

    public void setTarGrpId(Long tarGrpId) {
        this.tarGrpId = tarGrpId;
    }

    public Long getRootFlag() {
        return rootFlag;
    }

    public void setRootFlag(Long rootFlag) {
        this.rootFlag = rootFlag;
    }

    public String getLeftParam() {
        return leftParam;
    }

    public void setLeftParam(String leftParam) {
        this.leftParam = leftParam;
    }

    public String getLeftParamType() {
        return leftParamType;
    }

    public void setLeftParamType(String leftParamType) {
        this.leftParamType = leftParamType;
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

    public String getRightParamType() {
        return rightParamType;
    }

    public void setRightParamType(String rightParamType) {
        this.rightParamType = rightParamType;
    }

    public String getConditionText() {
        return conditionText;
    }

    public void setConditionText(String conditionText) {
        this.conditionText = conditionText;
    }
}
