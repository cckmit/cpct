package com.zjtelcom.cpct.domain.system;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class SysMenu extends BaseEntity {

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 菜单图标
     */
    private String menuImg;

    /**
     * 菜单级别（菜单类型）
     */
    private Long menuType;

    /**
     * 上级菜单id
     */
    private Long parentMenuId;

    /**
     * 菜单排序
     */
    private Integer menuNextId;

    /**
     * 菜单路由
     */
    private String menuUrl;

    /**
     * 菜单描述
     */
    private String menuRemark;

    private Date createDate;

    private Long createStaff;

    private Date updateDate;

    private Long updateStaff;

    /**
     * 上级菜单名称
     */
    private String parentMenuName;

}