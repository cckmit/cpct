package com.zjtelcom.cpct.dto.grouping;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/06 16:14
 * @version: V1.0
 */
public class TarGrpTemplateDetail implements Serializable {
    private Long tarGrpTemplateId;

    private String tarGrpTemplateName;

    private String tarGrpTemplateDesc;

    private String tarGrpType;

    private String tarGrpTypeName;

    private List<TarGrpTemConditionVO> tarGrpTemConditionVOList;



    public String getTarGrpTypeName() {
        return tarGrpTypeName;
    }

    public void setTarGrpTypeName(String tarGrpTypeName) {
        this.tarGrpTypeName = tarGrpTypeName;
    }

    public String getTarGrpType() {
        return tarGrpType;
    }

    public void setTarGrpType(String tarGrpType) {
        this.tarGrpType = tarGrpType;
    }

    public Long getTarGrpTemplateId() {
        return tarGrpTemplateId;
    }

    public void setTarGrpTemplateId(Long tarGrpTemplateId) {
        this.tarGrpTemplateId = tarGrpTemplateId;
    }

    public String getTarGrpTemplateName() {
        return tarGrpTemplateName;
    }

    public void setTarGrpTemplateName(String tarGrpTemplateName) {
        this.tarGrpTemplateName = tarGrpTemplateName;
    }

    public String getTarGrpTemplateDesc() {
        return tarGrpTemplateDesc;
    }

    public void setTarGrpTemplateDesc(String tarGrpTemplateDesc) {
        this.tarGrpTemplateDesc = tarGrpTemplateDesc;
    }

    public List<TarGrpTemConditionVO> getTarGrpTemConditionVOList() {
        return tarGrpTemConditionVOList;
    }

    public void setTarGrpTemConditionVOList(List<TarGrpTemConditionVO> tarGrpTemConditionVOList) {
        this.tarGrpTemConditionVOList = tarGrpTemConditionVOList;
    }
}