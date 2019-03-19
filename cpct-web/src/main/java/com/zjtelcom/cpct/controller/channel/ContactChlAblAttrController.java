package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.ContactChlAblAttrMapper;
import com.zjtelcom.cpct.domain.channel.ContactChlAblAttr;
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
@RequestMapping("${adminPath}/contactChlAblAttr")
public class ContactChlAblAttrController extends BaseController {

    @Autowired
    private ContactChlAblAttrMapper contactChlAblAttrMapper;

    /*
     **创建触点渠道能力属性
     */
    @PostMapping("createContactChlAblAttr")
    @CrossOrigin
    public Map<String, Object> createContactChlAblAttr(@RequestBody ContactChlAblAttr contactChlAblAttr) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlAblAttr contactChlAblAttrs = BeanUtil.create(contactChlAblAttr, new ContactChlAblAttr());
            contactChlAblAttrs.setCreateStaff(userId);
            contactChlAblAttrs.setCreateDate(new Date());
            contactChlAblAttrs.setUpdateStaff(userId);
            contactChlAblAttrs.setUpdateDate(new Date());
            contactChlAblAttrs.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            contactChlAblAttrs.setStatusDate(new Date());
            contactChlAblAttrMapper.insert(contactChlAblAttrs);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","添加成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createContactChlAblAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createContactChlAblAttr");
            return result;
        }
        return result;
    }

    /*
     **编辑触点渠道能力属性
     */
    @PostMapping("editContactChlAblAttr")
    @CrossOrigin
    public Map<String, Object> editContactChlAblAttr(@RequestBody ContactChlAblAttr contactChlAblAttr) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlAblAttr contactChlAblAttrs = contactChlAblAttrMapper.selectByPrimaryKey(contactChlAblAttr.getContactChlAblAttrId());
            if(contactChlAblAttrs == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            BeanUtil.copy(contactChlAblAttr, contactChlAblAttrs);
            contactChlAblAttrs.setUpdateDate(new Date());
            contactChlAblAttrs.setUpdateStaff(userId);
            contactChlAblAttrMapper.updateByPrimaryKey(contactChlAblAttrs);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","修改成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editContactChlAblAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editContactChlAblAttr");
            return result;
        }
        return result;
    }

    /*
     **删除触点渠道能力属性
     */
    @PostMapping("delContactChlAblAttr")
    @CrossOrigin
    public Map<String, Object> delContactChlAblAttr(@RequestBody ContactChlAblAttr contactChlAblAttr) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlAblAttr contactChlAblAttrs = contactChlAblAttrMapper.selectByPrimaryKey(contactChlAblAttr.getContactChlAblAttrId());
            if(contactChlAblAttrs == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            contactChlAblAttrMapper.deleteByPrimaryKey(contactChlAblAttr.getContactChlAblAttrId());
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","删除成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delContactChlAblAttr",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delContactChlAblAttr");
            return result;
        }
        return result;
    }

    /*
     **获取触点渠道能力属性详情
     */
    @PostMapping("getContactChlAblAttrDetail")
    @CrossOrigin
    public Map<String, Object> getContactChlAblAttrDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long contactChlAblAttrId = param.get("contactChlAblAttrId");
            ContactChlAblAttr contactChlAblAttr = contactChlAblAttrMapper.selectByPrimaryKey(contactChlAblAttrId);
            if(contactChlAblAttr == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg",contactChlAblAttr);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getContactChlAblAttrDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getContactChlAblAttrDetail");
            return result;
        }
        return result;
    }
}
