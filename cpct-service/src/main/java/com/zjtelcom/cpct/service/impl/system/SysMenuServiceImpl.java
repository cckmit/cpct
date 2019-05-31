package com.zjtelcom.cpct.service.impl.system;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysMenuMapper;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysMenuService;
import com.zjtelcom.cpct.service.system.SysMenuService;
import com.zjtelcom.cpct.util.SystemParamsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysMenuServiceImpl extends BaseService implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;  //菜单mapper
    @Autowired
    private SynSysMenuService synSysMenuService;



    @Override
    public Map<String, Object> listMenu() {
        Map<String, Object> result = new HashMap<>();
        List<SysMenu> list = sysMenuMapper.selectAll();

        //获取父级菜单
        JSONArray parentArray = new JSONArray();
        JSONObject parent;
        for (SysMenu sysMenu : list) {
            if (sysMenu.getParentMenuId() == 0) {
                parent = new JSONObject();
                parent.put("id", sysMenu.getMenuId());
                parent.put("label", sysMenu.getMenuName());
                parentArray.add(parent);
            }
        }

        //获取子菜单
        JSONArray childArray;
        JSONObject child;
        for (int i = 0; i < parentArray.size(); i++) {
            childArray = new JSONArray();
            parent = (JSONObject) parentArray.get(i);
            for (SysMenu sysMenu : list) {
                if (parent.getLong("id").equals(sysMenu.getParentMenuId())) {
                    child = new JSONObject();
                    child.put("id",sysMenu.getMenuId());
                    child.put("label",sysMenu.getMenuName());
                    childArray.add(child);
                }
            }
            parent.put("children",childArray);
        }

        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data", parentArray);

        return result;
    }

    /**
     * 根据角色id获取权限菜单
     * @param roleId
     * @return
     */
    @Override
    public Map<String, Object> listMenuByRoleId(Long roleId) {
        Map<String, Object> result = new HashMap<>();
        List<SysMenu> list = sysMenuMapper.selectByRoleId(roleId);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data", list);

        return result;
    }

    @Override
    public Map<String, Object> saveMenu(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        //获取参数
        String menuName = params.get("menuName");
        Long menuType = Long.parseLong(params.get("menuType"));
        Long parentMenuId = Long.parseLong(params.get("parentMenuId"));
        Integer menuNextId = Integer.parseInt(params.get("menuNextId"));
        String menuUrl = params.get("menuUrl");
        String menuRemark = params.get("menuRemark");
        String syncUrl = params.get("syncUrl");

        final SysMenu menu = new SysMenu();
        menu.setMenuName(menuName);
        menu.setMenuType(menuType);
        menu.setParentMenuId(parentMenuId);
        menu.setMenuNextId(menuNextId);
        menu.setMenuUrl(menuUrl);
        menu.setMenuRemark(menuRemark);
        menu.setSyncUrl(syncUrl);

        sysMenuMapper.insert(menu);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "保存成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synSysMenuService.synchronizeSingleMenu(menu.getMenuId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String, Object> updateMenu(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        //获取参数
        Long menuId = Long.parseLong(params.get("menuId"));
        String menuName = params.get("menuName");
        Long menuType = Long.parseLong(params.get("menuType"));
        Long parentMenuId = Long.parseLong(params.get("parentMenuId"));
        String menuUrl = params.get("menuUrl");
        String menuRemark = params.get("menuRemark");
        String syncUrl = params.get("syncUrl");
        Integer menuNextId = Integer.parseInt(params.get("menuNextId"));

        final SysMenu menu = new SysMenu();
        menu.setMenuId(menuId);
        menu.setMenuName(menuName);
        menu.setMenuType(menuType);
        menu.setParentMenuId(parentMenuId);
        menu.setMenuUrl(menuUrl);
        menu.setMenuRemark(menuRemark);
        menu.setSyncUrl(syncUrl);
        menu.setMenuNextId(menuNextId);

        sysMenuMapper.updateByPrimaryKey(menu);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "保存成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synSysMenuService.synchronizeSingleMenu(menu.getMenuId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String, Object> delMenu(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        //获取参数
        final Long menuId = Long.parseLong(params.get("menuId"));

        sysMenuMapper.deleteByPrimaryKey(menuId);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("resultMsg", "删除成功");

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synSysMenuService.deleteSingleMenu(menuId,"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        return result;
    }

    @Override
    public Map<String, Object> listMenuById(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();
        String menuIdStr = params.get("menuId");
        Long menuId = 0L;
        if(menuIdStr != null && !"".equals(menuIdStr)) {
            menuId = Long.parseLong(params.get("menuId"));
        }

        List<SysMenu> list = sysMenuMapper.listMenuById(menuId);
        for(int i=0;i<list.size();i++) {
            if(list.get(i).getMenuType() == 1) {
                list.get(i).setMenuTypeName("系统菜单");
            }
        }
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data", list);

        return result;
    }


    @Override
    public Map<String, Object> listMenuByLevel(Map<String, String> params) {
        Map<String, Object> result = new HashMap<>();

        Long level = Long.parseLong(params.get("level"));

        //获取上一级菜单等级
        List<SysMenu> list = new ArrayList<>();
        level -= 1;
        //如果被选中的是一级菜单
        if(level == 0) {
            SysMenu sysMenu = new SysMenu();
            sysMenu.setMenuId(0L);
            sysMenu.setMenuName("系统菜单");
            list.add(sysMenu);
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("data", list);
            return result;
        }

        list = sysMenuMapper.listMenuByLevel(level);
        result.put("resultCode", CommonConstant.CODE_SUCCESS);
        result.put("data", list);

        return result;
    }

}
