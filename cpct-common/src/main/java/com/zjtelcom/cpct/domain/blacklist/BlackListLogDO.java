package com.zjtelcom.cpct.domain.blacklist;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class BlackListLogDO extends BaseEntity implements Serializable {
    private int logId;
    private String method;
    private String args;
    private String returnValue;
    private String assetPhone;
    private String serviceCate;
    private String maketingCate;
    private String publicBenefitCate;
    private String channel;
    private String staffId;
    private String operType;
    private String remark;
}
