package com.zjtelcom.cpct.open.entity.label;

import lombok.Data;

@Data
public class InjectionLabelGrpMbr {
    private Long grpMbrId;
    private Long grpId;
    private Long injectionLabelId;
    private String statusCd;
    private String statusDate;
    private String remark;
}
