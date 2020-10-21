package com.zjtelcom.cpct.util;

import com.ctzj.smt.bss.centralized.web.util.BssSessionHelp;
import com.ctzj.smt.bss.sysmgr.model.dataobject.SystemRoles;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemPostDto;
import com.ctzj.smt.bss.sysmgr.model.dto.SystemUserDto;
import com.zjtelcom.cpct.enums.AreaCodeEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
             userDetail = BssSessionHelp.getCpctSystemUserDto();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return userDetail;
    }

    /**
     * 获取用户全量信息
     * @return
     */
    public static Long getStaffId(){
     Long staffId = 0L;
        try {
           SystemUserDto userDetail = BssSessionHelp.getCpctSystemUserDto();
            if (userDetail!=null){
                staffId =  userDetail.getStaffId();
            }
        }catch (Exception e){
            e.printStackTrace();
            return staffId;
        }
        return staffId;
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
     * 获取用户权限名称
     * @return
     */
    public static String getSysUserLevel() {
        String sysPostCode = "";
        try {
            SystemUserDto userDetail = BssSessionHelp.getCpctSystemUserDto();
            ArrayList<String> arrayList = new ArrayList<>();
            List<SystemPostDto> systemPostDtoList = userDetail.getSystemPostDtoList();
            //岗位信息查看最大权限作为岗位信息
            if (systemPostDtoList.size()>0 && systemPostDtoList!=null){
                for (SystemPostDto systemPostDto : systemPostDtoList) {
                    arrayList.add(systemPostDto.getSysPostCode());
                }
            }
            if (arrayList.contains(AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.SHENGJI.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.SHENGJI.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.FENGONGSI.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.FENGONGSI.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.FENGJU.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.FENGJU.getSysArea();
            }else if (arrayList.contains(AreaCodeEnum.sysAreaCode.ZHIJU.getSysPostCode())){
                sysPostCode = AreaCodeEnum.sysAreaCode.ZHIJU.getSysArea();
            }else {
                sysPostCode = AreaCodeEnum.sysAreaCode.CHAOGUAN.getSysArea();
            }
        }catch (Exception e){
            e.printStackTrace();
            return sysPostCode;
        }
        return sysPostCode;
    }



}
