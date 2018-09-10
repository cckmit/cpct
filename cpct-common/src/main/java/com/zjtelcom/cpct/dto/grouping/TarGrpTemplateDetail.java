package com.zjtelcom.cpct.dto.grouping;

import java.util.List;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/06 16:14
 * @version: V1.0
 */
public class TarGrpTemplateDetail {
    private Long tarGrpTemplateId;

    private String tarGrpTemplateName;

    private String tarGrpTemplateDesc;

    private List<TarGrpTemplateCondition> tarGrpTemplateConditionList;

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

    public List<TarGrpTemplateCondition> getTarGrpTemplateConditionList() {
        return tarGrpTemplateConditionList;
    }

    public void setTarGrpTemplateConditionList(List<TarGrpTemplateCondition> tarGrpTemplateConditionList) {
        this.tarGrpTemplateConditionList = tarGrpTemplateConditionList;
    }
}