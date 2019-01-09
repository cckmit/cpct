package com.zjtelcom.cpct.util;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.dataobject.SystemRoles;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 用户工具类
 */
@Component
public class UserUtil {


    /**
     * 获取当前登录用户id
     * @return
     */
    public static Long loginId() {

        return 1L;
    }

    /**
     * 获取用户全量信息
     * @return
     */
    public static SystemUserDto getUser(){
        SystemUserDto userDetail = null;
        try {
             userDetail = BssSessionHelp.getSystemUserDto();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return userDetail;
    }

    /**
     * 获取用户权限名称
     * @return
     */
    public static SystemUserDto getRoleCode() {
        SystemUserDto userDetail = new SystemUserDto();
        try {
            userDetail = BssSessionHelp.getSystemUserDto();
        }catch (Exception e){
            e.printStackTrace();
            return userDetail;
        }
        return userDetail;
    }

    /**
     * 获取登陆用户id
     * @return
     */
    public static Long getUserId(){
        Long userId = 1L;
        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
        if (userDetail != null) {
            userId = userDetail.getSysUserId();
        }
        return userId;
    }
}
