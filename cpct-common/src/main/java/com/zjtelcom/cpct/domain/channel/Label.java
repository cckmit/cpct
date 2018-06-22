package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;


@Data
public class Label extends BaseEntity {
    private long injectionLabelId;
    private String injectionLabelCode;
    private String injectionLabelName;
    private String injectionLabelDesc;
    private String labelType;
    private String labelValueType;
    private String labelDataType;
}
