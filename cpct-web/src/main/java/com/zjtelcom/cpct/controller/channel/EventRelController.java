package com.zjtelcom.cpct.controller.channel;

import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.channel.EventRel;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.service.channel.EventRelService;
import com.zjtelcom.cpct.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/eventRel")
public class EventRelController extends BaseController {

    @Autowired
    EventRelService eventRelService;

    /*
     **事件未被关联列表
     */
    @PostMapping("getEventNoRelList")
    @CrossOrigin
    public Map<String, Object> getEventNoRelList(@RequestBody ContactEvt contactEvt) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = eventRelService.getEventNoRelation(userId, contactEvt);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to getEventRelList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to getEventRelList");
            return result;
        }
        return result;
    }

    /*
     *创建事件关联
     */
    @PostMapping("createEventRel")
    @CrossOrigin
    public Map<String, Object> createEventRel(@RequestBody EventRel eventRel) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = eventRelService.createEventRelation(userId, eventRel);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createEventRelList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createEventRelList");
            return result;
        }
        return result;
    }

    /*
     *删除事件关联关系
     */
    @PostMapping("delEventRelation")
    @CrossOrigin
    public Map<String, Object> delEventRelation(@RequestBody EventRel eventRel) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = eventRelService.delEventRelation(userId, eventRel);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createEventRelList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createEventRelList");
            return result;
        }
        return result;
    }

    /*
     *获取被关联的事件列表
     */
    @PostMapping("getEventRelList")
    @CrossOrigin
    public Map<String, Object> getEventRelList(@RequestBody ContactEvt contactEvt) {
        Map<String ,Object> result = new HashMap<>();
        Long userId = UserUtil.loginId();
        try {
            result = eventRelService.getEventRelList(userId, contactEvt);
        }catch (Exception e){
            logger.error("[op:ServiceController] fail to createEventRelList",e);
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg"," fail to createEventRelList");
            return result;
        }
        return result;
    }
}
