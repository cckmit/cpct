package com.zjtelcom.cpct.util;

import org.springframework.stereotype.Component;

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

//    /**
//     * 获取用户全量信息
//     * @return
//     */
//    public static SystemUserDto getUser(){
//        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
//        return userDetail;
//    }
//
//    /**
//     * 获取用户权限名称
//     * @return
//     */
//    public static String getUserRole() {
//        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
//        String roleName = "默认管理员";
//        for (SystemPostDto role : userDetail.getSystemPostDtoList()) {
//            if (0 != role.getDefaultFlag()) {
//                continue;
//            }
//            roleName = role.getSysPostName();
//        }
//        return roleName;
//    }
//
//    /**
//     * 获取登陆用户id
//     * @return
//     */
//    public static Long getUserId(){
//        SystemUserDto userDetail = BssSessionHelp.getSystemUserDto();
//        return userDetail.getSysUserId();
//    }


}
