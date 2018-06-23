package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;


@Data
public class LabelGrpMbr extends BaseEntity {
    private Long grpMbrId;
    private Long grpId;
    private Long injectionLabelId;

}
