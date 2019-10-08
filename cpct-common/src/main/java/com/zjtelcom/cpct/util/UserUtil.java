package com.zjtelcom.cpct.util;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.dataobject.SystemRoles;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
        Long userId = 1L;
        SystemUserDto userDetail = null;
        try {
            userDetail = BssSessionHelp.getCpctSystemUserDto();
        } catch (Exception e) {
            return userId;
        }
        if (userDetail != null) {
            userId = userDetail.getSysUserId();
        }
        return userId;
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



}
