package com.zjtelcom.cpct.controller.event;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.service.event.ContactEvtMatchRulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description EventMatchRulController
 * @Author pengy
 * @Date 2018/6/22 9:44
 */

@RestController
@RequestMapping("${adminPath}/eventMatchRul")
public class ContactEvtMatchRulController extends BaseController {

    @Autowired
    private ContactEvtMatchRulService contactEvtMatchRulService;

    /**
     * 查询事件匹配规则列表
     */
    @RequestMapping("/listEventMatchRuls")
    @CrossOrigin
    public String listEventMatchRul(@RequestBody ContactEvtMatchRul contactEvtMatchRul) {
        Map<String,Object> maps = new HashMap<>();
        try {
            maps = contactEvtMatchRulService.listEventMatchRuls(contactEvtMatchRul);
        } catch (Exception e) {
            logger.error("[op:EventMatchRulController] fail to listEventMatchRuls for contactEvtMatchRul = {}! Exception: ", contactEvtMatchRul, e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }



}
