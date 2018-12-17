package com.zjtelcom.cpct.open.entity.label;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class InjectionLabel extends BaseEntity {
    private Long injectionLabelId;
    private String injectionLabelCode;
    private String injectionLabelName;
    private String injectionLabelDesc;
    private String labelType;
    private String labelValueType;
    private String labelDataType;
    private String statusCd;
    private String statusDate;
    private String remark;
    private List<InjectionLabelValue> injectionLabelValue;
    private List<InjectionLabelGrpMbr> injectionLabelGrpMbr;
}
