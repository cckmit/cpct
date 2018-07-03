package com.zjtelcom.cpct.service.impl.system;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.dao.system.SysMenuMapper;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysMenuServiceImpl extends BaseService implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

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

        result.put("resultCode", "0");
        result.put("data", parentArray);

        return result;
    }


    @Override
    public Map<String, Object> listMenuByRoleId(Long roleId) {
        Map<String, Object> result = new HashMap<>();
        List<SysMenu> list = sysMenuMapper.selectByRoleId(roleId);
        result.put("resultCode", "0");
        result.put("data", list);

        return result;
    }
}
