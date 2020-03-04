package com.zjtelcom.cpct.domain.blacklist;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.io.Serializable;

@Data
public class BlackListDO extends BaseEntity implements Serializable {
    private int blackId;
    private String assetPhone;
    private String serviceCate;
    private String maketingCate;
    private String publicBenefitCate;
    private String channel;
    private String staffId;
    private String operType;
}
