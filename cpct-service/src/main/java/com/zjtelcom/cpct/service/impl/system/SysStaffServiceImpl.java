package com.zjtelcom.cpct.service.impl.system;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysStaffMapper;
import com.zjtelcom.cpct.dao.system.SysStaffRoleMapper;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.domain.system.SysStaffRole;
import com.zjtelcom.cpct.dto.system.SysStaffDTO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysStaffService;
import com.zjtelcom.cpct.service.system.SysStaffService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysStaffServiceImpl extends BaseService implements SysStaffService {

    @Autowired
    private SysStaffMapper sysStaffMapper;

    @Autowired
    private SysStaffRoleMapper sysStaffRoleMapper;

    @Autowired
    private SynSysStaffService synSysStaffService;

    @Value("${sync.value}")
    private String value;

    public Map<String, Object> queryUserByName(String userName) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        SysStaff user = null;
        try{

            user = sysStaffMapper.queryUserByName(userName);
        } catch(Exception e){
            e.printStackTrace();
            logger.error("根据用户名查询用户信息异常,参数={},异常={}",userName);
            resultMap.put("code", CommonConstant.CODE_FAIL);
            resultMap.put("msg", "根据用户名查询用户信息失败!");
            return resultMap;
        }
        resultMap.put("code", CommonConstant.CODE_SUCCESS);
        resultMap.put("data", user);
        logger.info("根据用户名查询用户信息,参数={},返回值={}",userName, JSON.toJSONString(user));
        return resultMap;
    }

    /**
     * 查询员工（用户）列表
     * @param params
     * @return
     */
    @Override
    public Map<String,Object> listStaff(Map<String,String> params) {
        Map<String,Object> result = new HashMap<>();

        //获取分页参数
        Integer page = Integer.parseInt(params.get("page"));
        Integer pageSize = Integer.parseInt(params.get("pageSize"));

        SysStaff sysStaff = new SysStaff();
        if(params.containsKey("staffAccount")) {
            String staffAccount = params.get("staffAccount");
            sysStaff.setStaffAccount(staffAccount);
        }
        if(params.containsKey("staffName")) {
            String staffName = params.get("staffName");
            sysStaff.setStaffName(staffName);
        }
        if(params.containsKey("status") && !"".equals(params.get("status"))) {
            Long status = Long.parseLong(params.get("status"));
            sysStaff.setStatus(status);
        }
        if(params.containsKey("cityId") && !"".equals(params.get("cityId"))) {
            Long cityId = Long.parseLong(params.get("cityId"));
            sysStaff.setCityId(cityId);
        }
        if(params.containsKey("channelId") && !"".equals(params.get("channelId"))) {
            Long channelId = Long.parseLong(params.get("channelId"));
            sysStaff.setChannelId(channelId);
        }
        if(params.containsKey("loginStatus") && !"".equals(params.get("loginStatus"))) {
            Long loginStatus = Long.parseLong(params.get("loginStatus"));
            sysStaff.setLoginStatus(loginStatus);
        }

        //分页
        PageHelper.startPage(page, pageSize);
        List<SysStaff> list = sysStaffMapper.selectAll(sysStaff);
        Page pageInfo = new Page(new PageInfo(list));

        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("data",list);
        result.put("pageInfo",pageInfo);

        return result;
    }

    @Override
    public Map<String,Object> saveStaff(SysStaffDTO sysStaffDTO) throws Exception {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空

        final SysStaff sysStaff = new SysStaff();
        CopyPropertiesUtil.copyBean2Bean(sysStaff, sysStaffDTO);
        //判断账号是否重复
        int count = sysStaffMapper.checkCodeRepeat(sysStaff.getStaffAccount());
        if (count > 0) {
            //todo 异常 账号重复
            result.put("resultCode",CommonConstant.CODE_SUCCESS);
            result.put("resultMsg","账号重复");
            return result;
        }

        //密码加密
        sysStaff.setPassword(new SimpleHash("md5", sysStaff.getPassword()).toHex());

        //初始化状态值 todo 静态
        sysStaff.setStatus(1L);
        //初始化登录状态 离线
        sysStaff.setLoginStatus(2L);

        Long loginId = UserUtil.loginId();
        sysStaff.setCreateStaff(loginId);
        sysStaff.setCreateDate(new Date());
        int flag = sysStaffMapper.insert(sysStaff);
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
            result.put("resultCode",CommonConstant.CODE_FAIL);
            result.put("resultMsg","员工信息保存失败");
            throw new Exception();
        }

        //保存角色信息
        SysStaffRole sysStaffRole = new SysStaffRole();
        sysStaffRole.setStaffId(sysStaff.getStaffId());
        sysStaffRole.setRoleId(sysStaffDTO.getRoleId());
        flag = sysStaffRoleMapper.insert(sysStaffRole);
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
            result.put("resultCode",CommonConstant.CODE_FAIL);
            result.put("resultMsg","员工角色信息保存失败");
            throw new Exception();
        }

        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","保存成功");

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synSysStaffService.synchronizeSingleStaff(sysStaff.getStaffId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String,Object> updateStaff(SysStaffDTO sysStaffDTO) throws Exception {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空
        final SysStaff sysStaff = new SysStaff();
        CopyPropertiesUtil.copyBean2Bean(sysStaff, sysStaffDTO);

        //todo 判断账号是否重复

        Long loginId = UserUtil.loginId();
        sysStaff.setUpdateStaff(loginId);
        sysStaff.setUpdateDate(new Date());
        int flag = sysStaffMapper.updateByPrimaryKey(sysStaff);
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
        }
        //删除角色信息
        flag = sysStaffRoleMapper.deleteByStaffId(sysStaff.getStaffId());
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
        }

        //保存角色信息
        SysStaffRole sysStaffRole = new SysStaffRole();
        sysStaffRole.setStaffId(sysStaff.getStaffId());
        sysStaffRole.setRoleId(sysStaffDTO.getRoleId());
        flag = sysStaffRoleMapper.insert(sysStaffRole);
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
        }

        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","保存成功");

        if (value.equals("1")){
            new Thread(){
                public void run(){
                    try {
                        synSysStaffService.synchronizeSingleStaff(sysStaff.getStaffId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String,Object> changeStatus(Long id, Long status) {
        Map<String,Object> result = new HashMap<>();
        if (id == null || status == null) {
            // todo 异常
        }
        SysStaff params = new SysStaff();
        params.setStaffId(id);
        params.setStatus(status);

        Long loginId = UserUtil.loginId();
        params.setUpdateStaff(loginId);
        params.setUpdateDate(new Date());
        sysStaffMapper.changeStatus(params);

        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","保存成功");
        return result;
    }

    /**
     * 根据id获取员工账号信息
     *
     * @param id 员工id
     * @return
     */
    @Override
    public Map<String,Object> getStaff(Long id) {
        Map<String,Object> result = new HashMap<>();

        if (id == null) {
            //todo 为空异常
        }
        SysStaff sysStaff = sysStaffMapper.selectByPrimaryKey(id);

        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("data",sysStaff);
        return result;
    }

    /**
     * 修改密码
     *
     * @param id       员工id
     * @param password 密码
     * @return
     */
    @Override
    public Map<String,Object> updatePassword(Long id, String password) {
        Map<String,Object> result = new HashMap<>();
        if (id == null) {
            //todo 为空异常
        }
        if (password == null) {

        }
        //密码加密
        password = (new SimpleHash("md5", password).toHex());

        sysStaffMapper.updatePassword(id, password);
        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","修改成功");
        return result;
    }


    @Override
    public Map<String,Object> lastLogin(String staffCode) {
        Map<String,Object> result = new HashMap<>();
        sysStaffMapper.lastLogin(staffCode);
        result.put("resultCode",CommonConstant.CODE_SUCCESS);
        return result;
    }
}
