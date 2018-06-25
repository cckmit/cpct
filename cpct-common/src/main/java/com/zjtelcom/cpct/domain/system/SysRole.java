package com.zjtelcom.cpct.domain.system;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class SysRole extends BaseEntity {

    private Long roleId;

    private String roleName;

    private String remark;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

}