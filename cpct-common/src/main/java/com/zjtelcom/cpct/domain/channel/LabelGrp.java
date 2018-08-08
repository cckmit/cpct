package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;


@Data
public class LabelGrp extends BaseEntity {
    private Long grpId;
    private String grpName;
    private String grpDesc;

}