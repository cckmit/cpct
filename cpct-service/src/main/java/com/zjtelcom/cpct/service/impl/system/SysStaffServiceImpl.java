package com.zjtelcom.cpct.service.impl.system;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.dao.system.SysStaffMapper;
import com.zjtelcom.cpct.dao.system.SysStaffRoleMapper;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.domain.system.SysStaffRole;
import com.zjtelcom.cpct.dto.system.SysStaffVO;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysStaffRoleService;
import com.zjtelcom.cpct.service.system.SysStaffService;
import com.zjtelcom.cpct.util.CopyPropertiesUtil;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Map<String, Object> queryUserByName(String userName) {
        Map<String, Object> reslutMap = new HashMap<String, Object>();
        SysStaff user = null;
        try{
            user = sysStaffMapper.queryUserByName(userName);
        } catch(Exception e){
            e.printStackTrace();
            logger.error("根据用户名查询用户信息异常,参数={},异常={}",userName);
            reslutMap.put("code", "1");
            reslutMap.put("msg", "根据用户名查询用户信息失败!");
            return reslutMap;
        }
        reslutMap.put("code", "0");
        reslutMap.put("data", user);
        logger.info("根据用户名查询用户信息,参数={},返回值={}",userName, JSON.toJSONString(user));
        return reslutMap;
    }

    @Override
    public List<SysStaff> listStaff(String staffCode, String staffName, Long status, Integer page, Integer pageSize) {
        //分页
        PageHelper.startPage(page, pageSize);
        List<SysStaff> list = sysStaffMapper.selectAll(staffCode, staffName, status);
        PageInfo pageInfo = new PageInfo(list);
        return list;
    }

    @Override
    public int saveStaff(SysStaffVO sysStaffVO) throws Exception {
        //todo 判断字段是否为空

        SysStaff sysStaff = new SysStaff();
        CopyPropertiesUtil.copyBean2Bean(sysStaff, sysStaffVO);
        //判断账号是否重复
        int count = sysStaffMapper.checkCodeRepeat(sysStaff.getStaffCode());
        if (count > 0) {
            //todo 异常 账号重复
        }
        //密码加密
        sysStaff.setPassword(new SimpleHash("md5", sysStaff.getPassword()).toHex());
        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysStaff.setCreateStaff(loginId);
        sysStaff.setCreateDate(new Date());
        int flag = sysStaffMapper.insert(sysStaff);
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
        }

        //保存角色信息
        SysStaffRole sysStaffRole = new SysStaffRole();
        sysStaffRole.setStaffId(sysStaff.getStaffId());
        sysStaffRole.setRoleId(sysStaffVO.getRoleId());
        flag = sysStaffRoleMapper.insert(sysStaffRole);
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
        }

        return flag;
    }

    @Override
    public int updateStaff(SysStaffVO sysStaffVO) throws Exception {
        //todo 判断字段是否为空
        SysStaff sysStaff = new SysStaff();
        CopyPropertiesUtil.copyBean2Bean(sysStaff, sysStaffVO);


        //todo 判断账号是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
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
        sysStaffRole.setRoleId(sysStaffVO.getRoleId());
        flag = sysStaffRoleMapper.insert(sysStaffRole);
        if (flag < 1) {
            //todo flag<1 判断失败抛出业务异常
        }

        return flag;
    }

    @Override
    public int changeStatus(Long id, Long status) {
        if (id == null || status == null) {
            // todo 异常
        }
        SysStaff params = new SysStaff();
        params.setStaffId(id);
        params.setStatus(status);

        //todo 获取当前登录用户id
        Long loginId = 1L;
        params.setUpdateStaff(loginId);
        params.setUpdateDate(new Date());
        return sysStaffMapper.changeStatus(params);
    }

    /**
     * 根据id获取员工账号信息
     *
     * @param id 员工id
     * @return
     */
    @Override
    public SysStaff getStaff(Long id) {

        if (id == null) {
            //todo 为空异常
        }
        return sysStaffMapper.selectByPrimaryKey(id);
    }

    /**
     * 修改密码
     *
     * @param id       员工id
     * @param password 密码
     * @return
     */
    @Override
    public int updatePassword(Long id, String password) {
        if (id == null) {
            //todo 为空异常
        }
        if (password == null) {

        }
        //密码加密
        password = (new SimpleHash("md5", password).toHex());

        return sysStaffMapper.updatePassword(id, password);
    }


    @Override
    public int lastLogin(String staffCode) {
        return sysStaffMapper.lastLogin(staffCode);
    }
}
