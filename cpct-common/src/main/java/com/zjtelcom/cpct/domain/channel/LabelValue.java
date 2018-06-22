package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;


@Data
public class LabelValue extends BaseEntity {
    private long labelValueId;
    private long injectionLabelId;
    private String labelValue;
    private String valueName;
    private String valueDesc;

}
