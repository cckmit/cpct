/*
 * 文件名：AuthenticationInfo.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

import java.io.Serializable;

/**
 * 安全控制信息对象</br>
 * 安全控制信息对象,包含系统用户工号等信息
 * @author taowenwu
 * @version 1.0
 * @see AuthenticationInfo
 * @since
 */

public class AuthenticationInfo implements Serializable {
    private String sysUserId;

    private String sysUserPostId;

    public String getSysUserId() {
        return sysUserId;
    }

    public void setSysUserId(String sysUserId) {
        this.sysUserId = sysUserId;
    }

    public String getSysUserPostId() {
        return sysUserPostId;
    }

    public void setSysUserPostId(String sysUserPostId) {
        this.sysUserPostId = sysUserPostId;
    }

}
