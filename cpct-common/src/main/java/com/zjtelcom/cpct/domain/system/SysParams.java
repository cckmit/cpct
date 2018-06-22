package com.zjtelcom.cpct.domain.system;

import lombok.Data;

import java.util.Date;

@Data
public class SysParams {

    private Long paramId;

    private String paramName;

    private String paramValue;

    private Long configType;

    private Long modifyFlag;

    private String description;

    private String paramKey;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;


}