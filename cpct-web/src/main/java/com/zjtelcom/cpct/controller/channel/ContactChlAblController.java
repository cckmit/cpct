package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.ContactChlAblMapper;
import com.zjtelcom.cpct.domain.channel.ContactChlAbl;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@RestController
@RequestMapping("${adminPath}/contactChlAbl")
public class ContactChlAblController extends BaseController {

    @Autowired
    private ContactChlAblMapper contactChlAblMapper;

    /*
     **创建触点渠道能力
     */
    @PostMapping("createContactChlAbl")
    @CrossOrigin
    public Map<String, Object> createContactChlAbl(@RequestBody Map<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlAbl contactChlAbles = JSON.parseObject(JSON.toJSONString(params), ContactChlAbl.class);
            contactChlAbles.setCreateStaff(userId);
            contactChlAbles.setCreateDate(new Date());
            contactChlAbles.setUpdateStaff(userId);
            contactChlAbles.setUpdateDate(new Date());
            contactChlAbles.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            contactChlAbles.setStatusDate(new Date());
            contactChlAblMapper.insert(contactChlAbles);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","添加成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createContactChlAbl",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createContactChlAbl");
            return result;
        }
        return result;
    }

    /*
     **编辑触点渠道能力
     */
    @PostMapping("editContactChlAbl")
    @CrossOrigin
    public Map<String, Object> editContactChlAbl(@RequestBody ContactChlAbl contactChlAbl) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlAbl contactChlAbles = contactChlAblMapper.selectByPrimaryKey(contactChlAbl.getContacChlAblId());
            if(contactChlAbles == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            BeanUtil.copy(contactChlAbl, contactChlAbles);
            contactChlAbles.setUpdateDate(new Date());
            contactChlAbles.setUpdateStaff(userId);
            contactChlAblMapper.updateByPrimaryKey(contactChlAbles);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","修改成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editContactChlAbl",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editContactChlAbl");
            return result;
        }
        return result;
    }

    /*
     **删除触点渠道能力
     */
    @PostMapping("delContactChlAbl")
    @CrossOrigin
    public Map<String, Object> delContactChlAbl(@RequestBody ContactChlAbl contactChlAbl) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlAbl contactChlAbles = contactChlAblMapper.selectByPrimaryKey(contactChlAbl.getContacChlAblId());
            if(contactChlAbles == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            contactChlAblMapper.deleteByPrimaryKey(contactChlAbl.getContacChlAblId());
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","删除成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delContactChlAbl",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delContactChlAbl");
            return result;
        }
        return result;
    }

    /*
     **获取触点渠道能力详情
     */
    @PostMapping("getContactChlAblDetail")
    @CrossOrigin
    public Map<String, Object> getContactChlAblDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long contacChlAblId = param.get("contacChlAblId");
            ContactChlAbl contactChlAbl = contactChlAblMapper.selectByPrimaryKey(contacChlAblId);
            if(contactChlAbl == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg",contactChlAbl);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getContactChlAblDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getContactChlAblDetail");
            return result;
        }
        return result;
    }
}
