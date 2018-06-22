package com.zjtelcom.cpct.domain.system;

import lombok.Data;

import java.util.Date;

@Data
public class SysStaff {
    private Long staffId;

    private String staffCode;

    private String staffName;

    private String password;

    private String staffPhone;

    private String staffTelephone;

    private String staffEmail;

    private Long channelId;

    private Long cityId;

    private Long status;

    private Date lastLogin;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

}