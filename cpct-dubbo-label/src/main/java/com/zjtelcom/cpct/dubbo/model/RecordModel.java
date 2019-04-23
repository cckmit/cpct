package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class RecordModel implements Serializable {

    private Long labRowId;//标签id
    private String labName;//标签名称
    private String labEngName;//英文名称
    private String labCode;//标签编码
    private String labDataType;//标签数据类型(字符型/数值型/时间型)
    private String labManageType;//标签管理类型(A集团/B省份/C地市（可扩展）)
    private String labType;//标签类型(文本\枚举)
    private String labRelevantFlag;//标签置信度
    private String labMissRate;//标签缺失率
    private String labObjectCode;//标签对象编码
    private String labObject;//标签对象（用户级，客户级，销售品级，区域级，填写标签对应归属对象）
    private String labLevel1;//一级分类编码
    private String labLevel1Name;//一级分类编码名称
    private String labLevel2;//二级分类编码
    private String labLevel2Name;//二级分类编码名称
    private String labLevel3;//三级分类编码
    private String labLevel3Name;//三级分类编码名称
    private String labLevel4;//四级分类编码
    private String labLevel4Name;//四级分类编码名称
    private String labLevel5;//五级分类编码
    private String labLevel5Name;//五级分类编码名称
    private String labLevel6;//六级分类编码
    private String labLevel6Name;//六级分类编码名称
    private String labUpdateFeq;//更新频率（1:实时;2:日;3:周4:月）
    private String labBusiDesc;//业务口径
    private String labTechDesc;//技术口径
    private String labState;//上线：3；下线：5
    private String labExample;
    private Date labEffectiveDate;
    private Date labExpirationDate;
    private String demander	;
    private String demandDepartment	;
    private String demandDescription;
    private List<Map<String,Object>> labelValueList;


    public String getLabExample() {
        return labExample;
    }

    public void setLabExample(String labExample) {
        this.labExample = labExample;
    }

    public Date getLabEffectiveDate() {
        return labEffectiveDate;
    }

    public void setLabEffectiveDate(Date labEffectiveDate) {
        this.labEffectiveDate = labEffectiveDate;
    }

    public Date getLabExpirationDate() {
        return labExpirationDate;
    }

    public void setLabExpirationDate(Date labExpirationDate) {
        this.labExpirationDate = labExpirationDate;
    }

    public String getDemander() {
        return demander;
    }

    public void setDemander(String demander) {
        this.demander = demander;
    }

    public String getDemandDepartment() {
        return demandDepartment;
    }

    public void setDemandDepartment(String demandDepartment) {
        this.demandDepartment = demandDepartment;
    }

    public String getDemandDescription() {
        return demandDescription;
    }

    public void setDemandDescription(String demandDescription) {
        this.demandDescription = demandDescription;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public String getLabEngName() {
        return labEngName;
    }

    public void setLabEngName(String labEngName) {
        this.labEngName = labEngName;
    }

    public String getLabCode() {
        return labCode;
    }

    public void setLabCode(String labCode) {
        this.labCode = labCode;
    }

    public String getLabDataType() {
        return labDataType;
    }

    public void setLabDataType(String labDataType) {
        this.labDataType = labDataType;
    }

    public String getLabManageType() {
        return labManageType;
    }

    public void setLabManageType(String labManageType) {
        this.labManageType = labManageType;
    }

    public String getLabType() {
        return labType;
    }

    public void setLabType(String labType) {
        this.labType = labType;
    }

    public String getLabRelevantFlag() {
        return labRelevantFlag;
    }

    public void setLabRelevantFlag(String labRelevantFlag) {
        this.labRelevantFlag = labRelevantFlag;
    }

    public String getLabMissRate() {
        return labMissRate;
    }

    public void setLabMissRate(String labMissRate) {
        this.labMissRate = labMissRate;
    }

    public String getLabObjectCode() {
        return labObjectCode;
    }

    public void setLabObjectCode(String labObjectCode) {
        this.labObjectCode = labObjectCode;
    }

    public String getLabObject() {
        return labObject;
    }

    public void setLabObject(String labObject) {
        this.labObject = labObject;
    }

    public String getLabLevel1() {
        return labLevel1;
    }

    public void setLabLevel1(String labLevel1) {
        this.labLevel1 = labLevel1;
    }

    public String getLabLevel1Name() {
        return labLevel1Name;
    }

    public void setLabLevel1Name(String labLevel1Name) {
        this.labLevel1Name = labLevel1Name;
    }

    public String getLabLevel2() {
        return labLevel2;
    }

    public void setLabLevel2(String labLevel2) {
        this.labLevel2 = labLevel2;
    }

    public String getLabLevel2Name() {
        return labLevel2Name;
    }

    public void setLabLevel2Name(String labLevel2Name) {
        this.labLevel2Name = labLevel2Name;
    }

    public String getLabLevel3() {
        return labLevel3;
    }

    public void setLabLevel3(String labLevel3) {
        this.labLevel3 = labLevel3;
    }

    public String getLabLevel3Name() {
        return labLevel3Name;
    }

    public void setLabLevel3Name(String labLevel3Name) {
        this.labLevel3Name = labLevel3Name;
    }

    public String getLabLevel4() {
        return labLevel4;
    }

    public void setLabLevel4(String labLevel4) {
        this.labLevel4 = labLevel4;
    }

    public String getLabLevel4Name() {
        return labLevel4Name;
    }

    public void setLabLevel4Name(String labLevel4Name) {
        this.labLevel4Name = labLevel4Name;
    }

    public String getLabLevel5() {
        return labLevel5;
    }

    public void setLabLevel5(String labLevel5) {
        this.labLevel5 = labLevel5;
    }

    public String getLabLevel5Name() {
        return labLevel5Name;
    }

    public void setLabLevel5Name(String labLevel5Name) {
        this.labLevel5Name = labLevel5Name;
    }

    public String getLabLevel6() {
        return labLevel6;
    }

    public void setLabLevel6(String labLevel6) {
        this.labLevel6 = labLevel6;
    }

    public String getLabLevel6Name() {
        return labLevel6Name;
    }

    public void setLabLevel6Name(String labLevel6Name) {
        this.labLevel6Name = labLevel6Name;
    }

    public String getLabUpdateFeq() {
        return labUpdateFeq;
    }

    public void setLabUpdateFeq(String labUpdateFeq) {
        this.labUpdateFeq = labUpdateFeq;
    }

    public String getLabBusiDesc() {
        return labBusiDesc;
    }

    public void setLabBusiDesc(String labBusiDesc) {
        this.labBusiDesc = labBusiDesc;
    }

    public String getLabTechDesc() {
        return labTechDesc;
    }

    public void setLabTechDesc(String labTechDesc) {
        this.labTechDesc = labTechDesc;
    }

    public String getLabState() {
        return labState;
    }

    public void setLabState(String labState) {
        this.labState = labState;
    }

    public Long getLabRowId() {
        return labRowId;
    }

    public void setLabRowId(Long labRowId) {
        this.labRowId = labRowId;
    }

    public List<Map<String, Object>> getLabelValueList() {
        return labelValueList;
    }

    public void setLabelValueList(List<Map<String, Object>> labelValueList) {
        this.labelValueList = labelValueList;
    }
}
