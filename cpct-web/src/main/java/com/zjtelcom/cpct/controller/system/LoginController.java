package com.zjtelcom.cpct.controller.system;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.system.SysMenuMapper;
import com.zjtelcom.cpct.dao.system.SysRoleMapper;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.domain.system.UserInfo;
import com.zjtelcom.cpct.service.system.SysStaffService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}")
public class LoginController extends BaseController {

    @Autowired
    private SysStaffService sysStaffService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @RequestMapping("/getSysUser")
    @ResponseBody
    @CrossOrigin
    public Map<String,Object> getSysUser() {
        Map<String,Object> result = new HashMap<>();
        SystemUserDetail userDetail = new SystemUserDetail();
        try{
            userDetail = BssSessionHelp.getSystemUserDetail();
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",userDetail);
        return result;
    }

    /**
     * 登录
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    public String doLogin(@RequestBody Map<String, String> params) {

        String username = params.get("username");
        String password = params.get("password");

        // 获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();

        Map<String, Object> result = new HashMap<>();

        // 测试当前的用户是否已经被认证，即是否已经登陆
        // 调用Subject的isAuthenticated
        if (!currentUser.isAuthenticated()) {
            // 把用户名和密码封装为UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(true);
            try {
                // 执行登陆
                currentUser.login(token);

                SysStaff sysStaff = (SysStaff) SecurityUtils.getSubject().getPrincipal();
                UserInfo userInfo = new UserInfo();
                CopyPropertiesUtil.copyBean2Bean(userInfo, sysStaff);
                //查询用户角色
                List<SysRole> listRole = sysRoleMapper.selectByStaffId(userInfo.getStaffId());
                userInfo.setRoleList(listRole);
                //查询用户菜单
                List<SysMenu> menuList = new ArrayList<>();
                for(SysRole sysRole : listRole) {
                    menuList = sysMenuMapper.selectByRoleId(sysRole.getRoleId());
                }
                userInfo.setMenuList(menuList);
                result.put("user", userInfo);

                //记录最后一次登录时间
                sysStaffService.lastLogin(username);

            } catch (AuthenticationException ae) {
                ae.printStackTrace();
                logger.info("登录失败 = {}", ae.getMessage());
                return initFailRespInfo("用户名或密码错误", "0001");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("登录失败 = {}", e.getMessage());
                return initFailRespInfo("登录失败", "0001");
            }
        } else {
            try {
                SysStaff sysStaff = (SysStaff) SecurityUtils.getSubject().getPrincipal();
                UserInfo userInfo = new UserInfo();
                CopyPropertiesUtil.copyBean2Bean(userInfo, sysStaff);
                //查询用户角色
                List<SysRole> listRole = sysRoleMapper.selectByStaffId(userInfo.getStaffId());
                userInfo.setRoleList(listRole);
                //查询用户菜单
                List<SysMenu> menuList = new ArrayList<>();
                for(SysRole sysRole : listRole) {
                    menuList = sysMenuMapper.selectByRoleId(sysRole.getRoleId());
                }
                userInfo.setMenuList(menuList);
                result.put("user", userInfo);

            } catch (Exception e) {
                e.printStackTrace();
                logger.info("登录失败 = {}", e.getMessage());
                return initFailRespInfo("查询用户信息失败", "0001");
            }

        }

        return initSuccRespInfo(result);
    }

    /**
     * 登出
     *
     * @return
     */
    @RequestMapping(value = "logout", method = RequestMethod.POST)
    @ResponseBody
    public String doLogout() {

        Subject subject = SecurityUtils.getSubject();
        try {
            subject.logout();
        } catch (SessionException se) {
            se.printStackTrace();
            logger.info("登出出错 msg={}", se.getMessage());
        }

        logger.info("用户已登出");

        return initSuccRespInfo(null);
    }


}
