package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.channel.PushCtrlRulMapper;
import com.zjtelcom.cpct.domain.channel.MktKeywords;
import com.zjtelcom.cpct.domain.channel.PushCtrlRul;
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
@RequestMapping("${adminPath}/pushCtrlRul")
public class PushCtrlRulController extends BaseController {

    @Autowired
    private PushCtrlRulMapper pushCtrlRulMapper;

    /*
     **创建推送控制策略规则
     */
    @PostMapping("createPushCtrlRul")
    @CrossOrigin
    public Map<String, Object> createPushCtrlRul(@RequestBody PushCtrlRul pushCtrlRul) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            PushCtrlRul pushCtrlRules = BeanUtil.create(pushCtrlRul, new PushCtrlRul());
            pushCtrlRules.setCreateStaff(userId);
            pushCtrlRules.setCreateDate(new Date());
            pushCtrlRules.setUpdateStaff(userId);
            pushCtrlRules.setUpdateDate(new Date());
            pushCtrlRules.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
            pushCtrlRules.setStatusDate(new Date());
            pushCtrlRulMapper.insert(pushCtrlRules);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","添加成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createPushCtrlRul",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createPushCtrlRul");
            return result;
        }
        return result;
    }

    /*
     **编辑推送控制策略规则
     */
    @PostMapping("editPushCtrlRul")
    @CrossOrigin
    public Map<String, Object> editPushCtrlRul(@RequestBody PushCtrlRul pushCtrlRul) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            PushCtrlRul pushCtrlRules = pushCtrlRulMapper.selectByPrimaryKey(pushCtrlRul.getPushCtrlRulId());
            if(pushCtrlRules == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            BeanUtil.copy(pushCtrlRul, pushCtrlRules);
            pushCtrlRules.setUpdateDate(new Date());
            pushCtrlRules.setUpdateStaff(userId);
            pushCtrlRulMapper.updateByPrimaryKey(pushCtrlRules);
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","修改成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to editPushCtrlRul",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to editPushCtrlRul");
            return result;
        }
        return result;
    }

    /*
     **删除推送控制策略规则
     */
    @PostMapping("delPushCtrlRul")
    @CrossOrigin
    public Map<String, Object> delPushCtrlRul(@RequestBody PushCtrlRul pushCtrlRul) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            PushCtrlRul pushCtrlRules = pushCtrlRulMapper.selectByPrimaryKey(pushCtrlRul.getPushCtrlRulId());
            if(pushCtrlRules == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            pushCtrlRulMapper.deleteByPrimaryKey(pushCtrlRul.getPushCtrlRulId());
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg","删除成功");
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to delPushCtrlRul",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to delPushCtrlRul");
            return result;
        }
        return result;
    }

    /*
     **获取推送控制策略规则详情
     */
    @PostMapping("getPushCtrlRulDetail")
    @CrossOrigin
    public Map<String, Object> getPushCtrlRulDetail(@RequestBody HashMap<String,Long> param) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            Long pushCtrlRulId = param.get("pushCtrlRulId");
            PushCtrlRul pushCtrlRul = pushCtrlRulMapper.selectByPrimaryKey(pushCtrlRulId);
            if(pushCtrlRul == null) {
                result.put("resultCode",CODE_FAIL);
                result.put("resultMsg","事件源接口配置参数表不存在");
                return result;
            }
            result.put("resultCode",CODE_SUCCESS);
            result.put("resultMsg",pushCtrlRul);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getPushCtrlRulDetail",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getPushCtrlRulDetail");
            return result;
        }
        return result;
    }
}
