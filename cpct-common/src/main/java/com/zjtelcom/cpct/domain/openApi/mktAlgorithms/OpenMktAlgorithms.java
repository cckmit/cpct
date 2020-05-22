package com.zjtelcom.cpct.domain.openApi.mktAlgorithms;


import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class OpenMktAlgorithms extends BaseEntity {
    private Long algoId;
    private String algoCode;
    private String algoName;
    private String handleClass;
    private String algoDesc;
    private String statusCd;
    private String statusDate;
    private String remark;
}
