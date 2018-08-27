package com.zjtelcom.cpct.controller.synchronize;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:
 */
@RestController
@RequestMapping("${adminPath}/synchronize")
public class SynchronizeController extends BaseController {


    @Autowired
    private ContactEvtService contactEvtService;


    /**
     * 单个事件同步
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/event", method = RequestMethod.POST)
    @CrossOrigin
    public String event(@RequestBody Map<String, String> params)throws Exception {
        //logger.info("同步事件");
        String eventId = params.get("eventId");  // 事件id
        String roleName="admin";   //  操作角色
        Map<String, Object> map = contactEvtService.synchronizeEvent(Long.valueOf(eventId),roleName);
        //得到事件对象
        ContactEvt c=(ContactEvt) map.get("ContactEvt");

        return JSON.toJSONString(map);
    }




}
