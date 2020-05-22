package com.zjtelcom.cpct.domain.openApi.label;

import lombok.Data;

@Data
public class InjectionLabelValue {
    private Long labelValueId;
    private Long injectionLabelId;
    private String labelValue;
    private String valueName;
    private String valueDesc;
    private String statusCd;
    private String statusDate;
    private String remark;

}
