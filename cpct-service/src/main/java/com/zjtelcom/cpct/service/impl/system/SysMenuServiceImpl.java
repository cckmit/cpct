package com.zjtelcom.cpct.service.impl.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.dao.system.SysMenuMapper;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.dto.system.SysStaffDTO;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysMenuServiceImpl extends BaseService implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;  //菜单mapper

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

    /**
     * 根据角色id获取权限菜单
     * @param roleId
     * @return
     */
    @Override
    public Map<String, Object> listMenuByRoleId(Long roleId) {
        Map<String, Object> result = new HashMap<>();
        List<SysMenu> list = sysMenuMapper.selectByRoleId(roleId);
        result.put("resultCode", "0");
        result.put("data", list);

        return result;
    }


//    @RequestMapping(value = "saveStaff", method = RequestMethod.POST)
//    @CrossOrigin
//    public String saveStaff(SysStaffDTO sysStaffDTO) {
//        Map result = new HashMap();
//        try {
//            result = sysStaffService.saveStaff(sysStaffDTO);
//        } catch (Exception e) {
//            logger.error("[op:SysStaffController] fail to saveStaff Exception: ", e);
//            return initFailRespInfo(ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorMsg(), ErrorCode.SEARCH_EVENT_LIST_FAILURE.getErrorCode());
//        }
//
//        return JSON.toJSON(result).toString();
//    }


}
