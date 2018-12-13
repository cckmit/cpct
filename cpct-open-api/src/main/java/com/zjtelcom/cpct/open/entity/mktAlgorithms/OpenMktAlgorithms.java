package com.zjtelcom.cpct.open.entity.mktAlgorithms;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

@Data
public class OpenMktAlgorithms extends BaseEntity{
    private Long algoId;
    private String algoCode;
    private String algoName;
    private String handleClass;
    private String algoDesc;
    private String statusCd;
    private String statusDate;
    private String remark;
}
