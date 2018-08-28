package com.zjtelcom.cpct.controller.synchronize;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:同步数据  准生产数据同步到生产环境
 */
@RestController
@RequestMapping("${adminPath}/synchronize")
public class SynchronizeController extends BaseController {


    @Autowired
    private SynContactEvtService synContactEvtService;


    /**
     * 单个事件同步
     * @param params
     * @return
     */
    @PostMapping("singleEvent")
    @CrossOrigin
    public String singleEvent(@RequestBody Map<String, String> params){
        logger.info("同步事件");
        String eventId = params.get("eventId");  // 事件id
        String roleName="admin";   //  操作角色
        Map<String, Object> map = synContactEvtService.synchronizeSingleEvent(Long.valueOf(eventId),roleName);
        //得到事件对象
        ContactEvt c=(ContactEvt) map.get("ContactEvt");
        return JSON.toJSONString(map);
    }


    /**
     * 批量事件同步
     * @return
     */
    @PostMapping("batchEvent")
    @CrossOrigin
    public String batchEvent(){
        //角色权限控制
        logger.info("批量事件同步");
        String roleName="admin";   //  操作角色
        Map<String, Object> map = synContactEvtService.synchronizeBatchEvent(roleName);
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件目录同步
     * @param params
     * @return
     */
    @PostMapping("singleEventType")
    @CrossOrigin
    public String singleEventType(@RequestBody Map<String, String> params){
        logger.info("同步事件");
        String eventId = params.get("eventId");  // 事件id
        String roleName="admin";   //  操作角色
        Map<String, Object> map = null;
        //得到事件对象
        ContactEvt c=(ContactEvt) map.get("ContactEvt");
        return JSON.toJSONString(map);
    }


    /**
     * 批量事件目录同步
     * @return
     */
    @PostMapping("batchEventType")
    @CrossOrigin
    public String batchEventType(){
        //角色权限控制
        logger.info("批量事件同步");
        String roleName="admin";   //  操作角色
        Map<String, Object> map = synContactEvtService.synchronizeBatchEvent(roleName);
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件源同步
     * @param params
     * @return
     */
    @PostMapping("singleEventSource")
    @CrossOrigin
    public String singleEventSource(@RequestBody Map<String, String> params){
        String eventId = params.get("eventId");  // 事件id
        String roleName="admin";   //  操作角色
        Map<String, Object> map = null;
        //得到事件对象
        ContactEvt c=(ContactEvt) map.get("ContactEvt");
        return JSON.toJSONString(map);

    }


    /**
     * 批量事件源同步
     * @return
     */
    @PostMapping("batchEventSource")
    @CrossOrigin
    public String batchEventSource(){
        //角色权限控制
        logger.info("批量事件同步");
        String roleName="admin";   //  操作角色
        Map<String, Object> map = synContactEvtService.synchronizeBatchEvent(roleName);
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件源接口同步
     * @param params
     * @return
     */
    @PostMapping("singleEventInterface")
    @CrossOrigin
    public String singleEventInterface(@RequestBody Map<String, String> params){

        String roleName="admin";   //  操作角色
        Map<String, Object> map = null;
        ContactEvt c=(ContactEvt) map.get("ContactEvt");
        return JSON.toJSONString(map);
    }



    /**
     * 批量事件源接口同步
     * @return
     */
    @PostMapping("batchEventInterface")
    @CrossOrigin
    public String batchEventInterface(){
        //角色权限控制
        logger.info("批量事件同步");
        String roleName="admin";   //  操作角色
        Map<String, Object> map = synContactEvtService.synchronizeBatchEvent(roleName);
        return  JSON.toJSONString(map);
    }














}
