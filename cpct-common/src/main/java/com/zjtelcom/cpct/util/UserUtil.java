package com.zjtelcom.cpct.util;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.dataobject.SystemRoles;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;

import java.util.List;

/**
 * 用户工具类
 */
public class UserUtil {

    /**
     * 获取当前登录用户id
     * @return
     */
    public static Long loginId() {

        return 1L;
    }

    public static SystemUserDto getUser(){
        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
        return userDetail;
    }

    public static String getUserRole() {
        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
        for (SystemPostDto role : userDetail.getSystemPostDtoList()) {
            if (role.getDefaultFlag() == 0) {

            }
        }
        return "";
    }


}
