package com.zjtelcom.cpct.controller.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.ContactChlConvertCfgMapper;
import com.zjtelcom.cpct.domain.channel.ContactChlConvertCfg;
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
@RequestMapping("${adminPath}/contactChlConvertCfg")
public class ContactChlConvertCfgController extends BaseController {

    @Autowired
    private ContactChlConvertCfgMapper contactChlConvertCfgMapper;

    /*
     **创建触点渠道协议适配转换配置
     */
    @PostMapping("createContactChlConvertCfg")
    @CrossOrigin
    public Map<String, Object> createContactChlConvertCfg(@RequestBody Map<String,Object> params) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlConvertCfg contactChlConvert = JSON.parseObject(JSON.toJSONString(params), ContactChlConvertCfg.class);
            contactChlConvert.setCreateStaff(userId);
            contactChlConvert.setCreateDate(new Date());
            contactChlConvert.setUpdateStaff(userId);
            contactChlConvert.setUpdateDate(new Date());
            contactChlConvert.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            contactChlConvert.setStatusDate(new Date());
            contactChlConvertCfgMapper.insert(contactChlConvert);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","添加成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createContactChlConvertCfg",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createContactChlConvertCfg");
            return result;
        }
        return result;
    }

    /*
     **编辑触点渠道协议适配转换配置
     */
    @PostMapping("editContactChlConvertCfg")
    @CrossOrigin
    public Map<String, Object> editContactChlConvertCfg(@RequestBody ContactChlConvertCfg contactChlConvertCfg) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlConvertCfg contactChlConvert = contactChlConvertCfgMapper.selectByPrimaryKey(contactChlConvertCfg.getContactChlConvertCfgId());
            if(contactChlConvert == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            BeanUtil.copy(contactChlConvertCfg, contactChlConvert);
            contactChlConvert.setUpdateDate(new Date());
            contactChlConvert.setUpdateStaff(userId);
            contactChlConvertCfgMapper.updateByPrimaryKey(contactChlConvert);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","修改成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editContactChlConvertCfg",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editContactChlConvertCfg");
            return result;
        }
        return result;
    }

    /*
     **删除触点渠道协议适配转换配置
     */
    @PostMapping("delContactChlConvertCfg")
    @CrossOrigin
    public Map<String, Object> delContactChlConvertCfg(@RequestBody ContactChlConvertCfg contactChlConvertCfg) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            ContactChlConvertCfg contactChlConvert = contactChlConvertCfgMapper.selectByPrimaryKey(contactChlConvertCfg.getContactChlConvertCfgId());
            if(contactChlConvert == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            contactChlConvertCfgMapper.deleteByPrimaryKey(contactChlConvertCfg.getContactChlConvertCfgId());
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","删除成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delContactChlConvertCfg",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delContactChlConvertCfg");
            return result;
        }
        return result;
    }

    /*
     **获取触点渠道协议适配转换配置详情
     */
    @PostMapping("getContactChlConvertCfgDetail")
    @CrossOrigin
    public Map<String, Object> getContactChlConvertCfgDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long contactChlConvertCfgId = param.get("contactChlConvertCfgId");
            ContactChlConvertCfg mktKeywords = contactChlConvertCfgMapper.selectByPrimaryKey(contactChlConvertCfgId);
            if(mktKeywords == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg",mktKeywords);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getContactChlConvertCfgDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getContactChlConvertCfgDetail");
            return result;
        }
        return result;
    }
}
