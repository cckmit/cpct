package com.zjtelcom.cpct.controller.synchronize;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.service.event.ContactEvtService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtTypeService;
import com.zjtelcom.cpct.service.synchronize.SynEventSorceService;
import com.zjtelcom.cpct.service.synchronize.SynInterfaceCfgService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    @Autowired
    private SynContactEvtTypeService synContactEvtTypeService;
    @Autowired
    private SynEventSorceService synEventSorceService;
    @Autowired
    private SynInterfaceCfgService synInterfaceCfgService;


    /**
     * 单个事件同步
     * @param eventId  事件主键id
     * @return
     */
    @PostMapping("singleEvent")
    @CrossOrigin
    public String singleEvent(@RequestParam(value = "eventId", required = true) Long eventId){
        logger.info("同步事件");
        //   权限控制
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
       try{
        map = synContactEvtService.synchronizeSingleEvent(eventId,roleName);
    } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtServiceImpl] 通过主键同步单个事件失败！Exception: ",eventId,e);
    }
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
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synContactEvtService.synchronizeBatchEvent(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtServiceImpl] 批量同步事件失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件目录同步
     * @param eventTypeId  事件目录主键id
     * @return
     */
    @PostMapping("singleEventType")
    @CrossOrigin
    public String singleEventType(@RequestParam(value = "eventTypeId", required = true) Long eventTypeId){
        logger.info("同步事件目录");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synContactEvtTypeService.synchronizeSingleEventType(eventTypeId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtTypeServiceImpl] 通过主键同步单个事件目录失败！Exception: ",eventTypeId,e);
        }
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
        logger.info("批量同步事件目录");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map =synContactEvtTypeService.synchronizeBatchEventType(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtTypeServiceImpl] 批量同步事件目录失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件源同步
     * @param eventSourceId  事件源主键id
     * @return
     */
    @PostMapping("singleEventSource")
    @CrossOrigin
    public String singleEventSource(@RequestParam(value = "eventSourceId", required = true) Long eventSourceId){
        logger.info("同步事件源");
        String roleName=getRole();   //  操作角色
        //得到事件对象
        Map<String, Object> map=new HashMap<>();
        try{
            map =synEventSorceService.synchronizeSingleEventSorce(eventSourceId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynEventSorceServiceImpl] 通过主键同步单个事件源失败！Exception: ",eventSourceId,e);
        }
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
        logger.info("批量同步事件源");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSorceService.synchronizeBatchEventSorce(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynEventSorceServiceImpl] 批量同步事件源失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件源接口同步
     * @param eventInterfaceId  事件源接口主键id
     * @return
     */
    @PostMapping("singleEventInterface")
    @CrossOrigin
    public String singleEventInterface(@RequestParam(value = "eventInterfaceId", required = true) Long eventInterfaceId){
        logger.info("同步事件源接口");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synInterfaceCfgService.synchronizeSingleEventInterface(eventInterfaceId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynInterfaceCfgServiceImpl] 通过主键同步单个事件源接口失败！Exception: ",eventInterfaceId,e);
        }
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
        logger.info("批量同步事件源接口");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synInterfaceCfgService.synchronizeBatchEventInterface(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtServiceImpl] 批量同步事件源接口失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 权限控制 获取角色身份
     * @return
     */
    public String getRole(){
        String role="admin";

        return role;
    }











}
