package com.zjtelcom.cpct.dto.system;

import lombok.Data;

@Data
public class SystemParam {

    private Integer paramId;

    private String paramName;

    private String paramValue;

    private Integer configType;

    private Integer modifyFlag;

    private String descripTion;

    private String paramKey;

}