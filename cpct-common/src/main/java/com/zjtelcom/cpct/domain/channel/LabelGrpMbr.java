package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;


@Data
public class LabelGrpMbr extends BaseEntity {
    private long grpMbrId;
    private long grpId;
    private long injectionLabelId;

}
