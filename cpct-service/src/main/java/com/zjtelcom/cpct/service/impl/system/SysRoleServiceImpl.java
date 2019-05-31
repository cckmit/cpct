package com.zjtelcom.cpct.service.impl.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysRoleMapper;
import com.zjtelcom.cpct.dao.system.SysRoleMenuMapper;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.domain.system.SysRoleMenu;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysRoleService;
import com.zjtelcom.cpct.service.system.SysRoleService;
import com.zjtelcom.cpct.util.SystemParamsUtil;
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
public class SysRoleServiceImpl extends BaseService implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SynSysRoleService synSysRoleService;


    @Override
    public Map<String, Object> listRole(String RoleName, int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        PageHelper.startPage(page, pageSize);
        List<SysRole> list = sysRoleMapper.selectByParams(RoleName);
        Page pageInfo = new Page(new PageInfo(list));

        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data", list);
        result.put("pageInfo", pageInfo);

        return result;
    }

    @Override
    public Map<String, Object> saveRole(final SysRole sysRole) {
        Map<String, Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断角色名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysRole.setCreateStaff(loginId);
        sysRole.setCreateDate(new Date());

        sysRoleMapper.insert(sysRole);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","保存成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synSysRoleService.synchronizeSingleRole(sysRole.getRoleId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String, Object> updateRole(final SysRole sysRole) {
        Map<String, Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断角色名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysRole.setUpdateStaff(loginId);
        sysRole.setUpdateDate(new Date());
        sysRoleMapper.updateByPrimaryKey(sysRole);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg","保存成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synSysRoleService.synchronizeSingleRole(sysRole.getRoleId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String, Object> getRole(Long id) {
        Map<String, Object> result = new HashMap<>();
        if (id == null) {
            //todo 为空异常
        }
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(id);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data", sysRole);
        return result;
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @Override
    public Map<String, Object> delRole(final Long id) {
        Map<String, Object> result = new HashMap<>();
        sysRoleMapper.deleteByPrimaryKey(id);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "删除成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synSysRoleService.deleteSingleRole(id,"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    /**
     * 保存角色权限
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> saveAuthority(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();

        Long roleId = Long.parseLong(((Integer) params.get("roleId")).toString());
        List<Integer> list = (List<Integer>) params.get("list");

        //先删除所有权限
        sysRoleMenuMapper.deleteByPrimaryKey(roleId);
        //保存新权限
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setRoleId(roleId);
        for (Integer menuId : list) {
            sysRoleMenu.setMenuId(Long.parseLong(menuId.toString()));
            sysRoleMenuMapper.insert(sysRoleMenu);
        }

        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "保存成功");
        return result;
    }

    @Override
    public Map<String, Object> listRoleAll() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> list = sysRoleMapper.selectAll();

        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data", list);

        return result;
    }
}
