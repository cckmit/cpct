package com.zjtelcom.cpct.domain.system;

import lombok.Data;

import java.util.Date;

@Data
public class SysMenu {

    private Long menuId;

    private String menuName;

    private String menuImg;

    private Long menuType;

    private Long parentMenuId;

    private Integer menuNextId;

    private String menuUrl;

    private String menuRemark;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

}