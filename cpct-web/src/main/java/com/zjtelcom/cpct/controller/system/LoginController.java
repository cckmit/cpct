package com.zjtelcom.cpct.controller.system;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.system.SysStaffService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController extends BaseController {

    @Autowired
    private SysStaffService sysStaffService;

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseBody
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password) {

        // 获取当前的Subject
        Subject currentUser = SecurityUtils.getSubject();

        // 测试当前的用户是否已经被认证，即是否已经登陆
        // 调用Subject的isAuthenticated
        if (!currentUser.isAuthenticated()) {
            // 把用户名和密码封装为UsernamePasswordToken 对象
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            token.setRememberMe(true);
            try {
                // 执行登陆
                currentUser.login(token);

                //记录最后一次登录时间
                sysStaffService.lastLogin(username);
            } catch (AuthenticationException ae) {
                ae.printStackTrace();
                logger.info("登录失败 = {}",ae.getMessage());
                return initFailRespInfo("登录失败","0001");
            }
        }

        return initSuccRespInfo(null);
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
