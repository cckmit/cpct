package com.zjtelcom.cpct.dto.system;

import lombok.Data;

import java.io.Serializable;

@Data
public class SystemParam implements Serializable {

    private Integer paramId;

    private String paramName;

    private String paramValue;

    private Integer configType;

    private Integer modifyFlag;

    private String descripTion;

    private String paramKey;

}