package com.zjtelcom.cpct.controller.synchronize;

import com.alibaba.fastjson.JSON;
import com.zjhcsoft.eagle.main.dubbo.model.policy.ResponseHeaderModel;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.service.synchronize.*;
import com.zjtelcom.cpct.service.synchronize.campaign.SynMktCampaignRelService;
import com.zjtelcom.cpct.service.synchronize.campaign.SyncActivityService;
import com.zjtelcom.cpct.service.synchronize.campaign.SynchronizeCampaignService;
import com.zjtelcom.cpct.service.synchronize.channel.SynChannelService;
import com.zjtelcom.cpct.service.synchronize.filter.SynFilterRuleService;
import com.zjtelcom.cpct.service.synchronize.label.SynLabelGrpService;
import com.zjtelcom.cpct.service.synchronize.label.SynLabelService;
import com.zjtelcom.cpct.service.synchronize.label.SynMessageLabelService;
import com.zjtelcom.cpct.service.synchronize.script.SynScriptService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysMenuService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysParamsService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysRoleService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysStaffService;
import com.zjtelcom.cpct.service.synchronize.template.SynTarGrpTemplateService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:同步数据 准生产数据同步到生产环境  所有的主键数据统一用id接收
 *              单个同步操作存在（新增，修改），（删除）区别  ---全量同步直接对比
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
    @Autowired
    private SynEventSceneService synEventSceneService;
    @Autowired
    private SynEventSceneTypeService synEventSceneTypeService;
    @Autowired
    private SynScriptService synScriptService;
    @Autowired
    private SynLabelService synLabelService;
    @Autowired
    private SynChannelService synChannelService;
    @Autowired
    private SynSysStaffService synSysStaffService;
    @Autowired
    private SynSysRoleService synSysRoleService;
    @Autowired
    private SynSysMenuService synSysMenuService;
    @Autowired
    private SynSysParamsService synSysParamsService;
    @Autowired
    private SynFilterRuleService synFilterRuleService;
    @Autowired
    private SynMessageLabelService synMessageLabelService;
    @Autowired
    private SynTarGrpTemplateService synTarGrpTemplateService;
    @Autowired
    private SynMktCampaignRelService synMktCampaignRelService;
    @Autowired(required = false)
    private SynchronizeCampaignService synchronizeCampaignService;
    @Autowired
    private SynLabelGrpService synLabelGrpService;
    @Autowired
    private SynQuestionService synQuestionService;
    @Autowired
    private SyncActivityService syncActivityService;


    /**
     * 单个事件同步新增，修改
     * @return
     */
    @PostMapping("singleEvent")
    @CrossOrigin
    public String singleEvent(@RequestBody Map<String, Object> params) {
        String roleName = getRole();   //  操作角色
        Long eventId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map = new HashMap<>();
        try {
            map = synContactEvtService.synchronizeSingleEvent(eventId, roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtServiceImpl] 通过主键同步单个事件失败！Exception: ", eventId, e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 单个事件同步删除
     * @return
     */
    @DeleteMapping("singleEvent")
    @CrossOrigin
    public String deleteSingleEvent(@RequestBody Map<String, Object> params) {
        String roleName = getRole();   //  操作角色
        Long eventId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map = new HashMap<>();
        try {
            map = synContactEvtService.deleteSingleEvent(eventId, roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtServiceImpl] 通过主键同步单个事件失败！Exception: ", eventId, e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 批量事件同步
     * @return
     */
    @RequestMapping("batchEvent")
    @CrossOrigin
    public String batchEvent(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synContactEvtService.synchronizeBatchEvent(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtServiceImpl] 全量同步事件失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件目录同步
     * @return
     */
    @PostMapping("singleEventType")
    @CrossOrigin
    public String singleEventType(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventTypeId = Long.valueOf((Integer) params.get("id"));
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
     * 单个事件目录同步 删除
     * @return
     */
    @DeleteMapping("singleEventType")
    @CrossOrigin
    public String deleteSingleEventType(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventTypeId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synContactEvtTypeService.deleteSingleEventType(eventTypeId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtTypeServiceImpl] 通过主键同步单个事件目录失败！Exception: ",eventTypeId,e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 全量事件目录同步
     * @return
     */
    @RequestMapping("batchEventType")
    @CrossOrigin
    public String batchEventType(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map =synContactEvtTypeService.synchronizeBatchEventType(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtTypeServiceImpl] 全量同步事件目录失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件源同步新增，修改
     * @return
     */
    @PostMapping("singleEventSource")
    @CrossOrigin
    public String singleEventSource(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventSourceId = Long.valueOf((Integer) params.get("id"));
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
     * 单个事件源同步删除
     * @param params
     * @return
     */
    @DeleteMapping("singleEventSource")
    @CrossOrigin
    public String deleteSingleEventSource(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventSourceId = Long.valueOf((Integer) params.get("id"));
        //得到事件对象
        Map<String, Object> map=new HashMap<>();
        try{
            map =synEventSorceService.deleteSingleEventSorce(eventSourceId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynEventSorceServiceImpl] 通过主键同步单个事件源失败！Exception: ",eventSourceId,e);
        }
        return JSON.toJSONString(map);

    }


    /**
     * 全量事件源同步
     * @return
     */
    @RequestMapping("batchEventSource")
    @CrossOrigin
    public String batchEventSource(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSorceService.synchronizeBatchEventSorce(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynEventSorceServiceImpl] 全量同步事件源失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件源接口同步
     * @return
     */
    @PostMapping("singleEventInterface")
    @CrossOrigin
    public String singleEventInterface(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventInterfaceId = Long.valueOf((Integer) params.get("id"));
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
     * 单个事件源接口同步 删除
     * @return
     */
    @DeleteMapping("singleEventInterface")
    @CrossOrigin
    public String deleteEventInterface(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventInterfaceId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synInterfaceCfgService.deleteSingleEventInterface(eventInterfaceId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynInterfaceCfgServiceImpl] 通过主键同步单个事件源接口失败！Exception: ",eventInterfaceId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量事件源接口同步
     * @return
     */
    @RequestMapping("batchEventInterface")
    @CrossOrigin
    public String batchEventInterface(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synInterfaceCfgService.synchronizeBatchEventInterface(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步事件源接口失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件场景同步
     * @return
     */
    @PostMapping("singleEventScene")
    @CrossOrigin
    public String singleEventScene(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventSceneId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSceneService.synchronizeSingleEventScene(eventSceneId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过主键同步单个事件场景失败！Exception: ",eventSceneId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量事件场景同步
     * @return
     */
    @RequestMapping("batchEventScene")
    @CrossOrigin
    public String batchEventScene(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSceneService.synchronizeBatchEventScene(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步事件场景失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件场景目录同步
     * @return
     */
    @PostMapping("singleEventSceneType")
    @CrossOrigin
    public String singleEventSceneType(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventSceneTypeId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSceneTypeService.synchronizeSingleEventSceneType(eventSceneTypeId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过主键同步单个事件场景目录失败！Exception: ",eventSceneTypeId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步事件场景目录
     * @return
     */
    @RequestMapping("batchEventSceneType")
    @CrossOrigin
    public String batchEventSceneType(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSceneTypeService.synchronizeBatchEventSceneType(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步事件场景目录失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个渠道同步
     * @return
     */
    @PostMapping("singleContactChannel")
    @CrossOrigin
    public String singleContactChannel(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long channelId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synChannelService.synchronizeSingleChannel(channelId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过渠道id同步单个渠道失败！Exception: ",channelId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 单个渠道同步删除
     * @return
     */
    @DeleteMapping("singleContactChannel")
    @CrossOrigin
    public String delelteSingleContactChannel(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long channelId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synChannelService.deleteSingleChannel(channelId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过渠道id同步单个渠道失败！Exception: ",channelId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量渠道同步
     * @return
     */
    @RequestMapping("batchContactChannel")
    @CrossOrigin
    public String batchContactChannel(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synChannelService.synchronizeBatchChannel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步渠道失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个接触脚本同步
     * @return
     */
    @PostMapping("singleContactScript")
    @CrossOrigin
    public String singleScript(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long scriptId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synScriptService.synchronizeScript(scriptId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过接触脚本id同步单个接触脚本失败！Exception: ",scriptId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 单个接触脚本同步删除操作
     * @return
     */
    @DeleteMapping("singleContactScript")
    @CrossOrigin
    public String deleteSingleScript(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long scriptId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synScriptService.delelteSynchronizeScript(scriptId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过接触脚本id同步单个接触脚本失败！Exception: ",scriptId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步接触脚本
     * @return
     */
    @RequestMapping("batchContactScript")
    @CrossOrigin
    public String batchContactScript(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synScriptService.synchronizeBatchScript(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步接触脚本失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 同步单个用户
     * @return
     */
    @PostMapping("singleStaff")
    @CrossOrigin
    public String singleStaff(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long staffId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysStaffService.synchronizeSingleStaff(staffId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个用户信息失败！Exception: ",staffId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 同步单个用户 删除
     * @return
     */
    @DeleteMapping("singleStaff")
    @CrossOrigin
    public String deleteSingleStaff(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long staffId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysStaffService.deleteSingleStaff(staffId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个用户信息失败！Exception: ",staffId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步用户信息
     * @return
     */
    @RequestMapping("batchStaff")
    @CrossOrigin
    public String batchStaff(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysStaffService.synchronizeBatchStaff(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步用户信息失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个角色信息
     * @return
     */
    @RequestMapping("singleRole")
    @CrossOrigin
    public String singleRole(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long roleId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysRoleService.synchronizeSingleRole(roleId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个角色信息失败！Exception: ",roleId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 同步单个角色信息  删除
     * @return
     */
    @DeleteMapping("singleRole")
    @CrossOrigin
    public String deleteSingleRole(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long roleId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysRoleService.deleteSingleRole(roleId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个角色信息失败！Exception: ",roleId,e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 全量同步角色信息
     * @return
     */
    @RequestMapping("batchRole")
    @CrossOrigin
    public String batchRole(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysRoleService.synchronizeBatchRole(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步角色信息失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个菜单信息
     * @return
     */
    @PostMapping("singleMenu")
    @CrossOrigin
    public String singleMenu(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long menuId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysMenuService.synchronizeSingleMenu(menuId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个菜单信息失败！Exception: ",menuId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 同步单个菜单信息 删除
     * @return
     */
    @DeleteMapping("singleMenu")
    @CrossOrigin
    public String deleteSingleMenu(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long menuId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysMenuService.deleteSingleMenu(menuId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个菜单信息失败！Exception: ",menuId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步菜单信息
     * @return
     */
    @RequestMapping("batchMenu")
    @CrossOrigin
    public String batchMenu(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysMenuService.synchronizeBatchMenu(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步菜单信息失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个静态参数
     * @return
     */
    @PostMapping("singleParams")
    @CrossOrigin
    public String singleParams(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long paramId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysParamsService.synchronizeSingleParam(paramId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过标签id同步单个静态参数失败！Exception: ",paramId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 同步单个静态参数 删除
     * @return
     */
    @DeleteMapping("singleParams")
    @CrossOrigin
    public String deleteSingleParams(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long paramId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysParamsService.deleteSingleParam(paramId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过标签id同步单个静态参数失败！Exception: ",paramId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步静态参数
     * @return
     */
    @RequestMapping("batchParams")
    @CrossOrigin
    public String batchParams(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysParamsService.synchronizeBatchParam(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步静态参数失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个过滤规则
     * @return
     */
    @PostMapping("singleFilterRule")
    @CrossOrigin
    public String singleFilterRule(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long ruleId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synFilterRuleService.synchronizeSingleFilterRule(ruleId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error(" 通过id同步单个过滤规则失败！Exception: ",ruleId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 同步单个过滤规则  删除
     * @return
     */
    @DeleteMapping("singleFilterRule")
    @CrossOrigin
    public String deleteSingleFilterRule(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long ruleId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synFilterRuleService.deleteSingleFilterRule(ruleId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error(" 通过id同步单个过滤规则失败！Exception: ",ruleId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步过滤规则
     * @return
     */
    @RequestMapping("batchFilterRule")
    @CrossOrigin
    public String batchFilterRule(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synFilterRuleService.synchronizeBatchFilterRule(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步过滤规则失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个试运算标签展示列
     * @return
     */
    @PostMapping("singleMessageLabel")
    @CrossOrigin
    public String singleMessageLabel(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long labelId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synMessageLabelService.synchronizeSingleMessageLabel(labelId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个试运算标签展示列失败！Exception: ",labelId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 同步单个试运算标签展示列  删除操作
     * @return
     */
    @DeleteMapping("singleMessageLabel")
    @CrossOrigin
    public String deleteSingleMessageLabel(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long labelId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synMessageLabelService.deleteSingleMessageLabel(labelId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个试运算标签展示列失败！Exception: ",labelId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步试运算标签展示列
     * @return
     */
    @RequestMapping("batchMessageLabel")
    @CrossOrigin
    public String batchMessageLabel(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synMessageLabelService.synchronizeBatchMessageLabel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步试运算标签展示列失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个客户分群
     * @return
     */
    @PostMapping("singleTarGrp")
    @CrossOrigin
    public String singleTemplate(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long templateId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synTarGrpTemplateService.synchronizeSingleTarGrp(templateId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个分群模板失败！Exception: ",templateId,e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 同步单个分客户分群删除
     * @return
     */
    @DeleteMapping("singleTarGrp")
    @CrossOrigin
    public String deleteSingleTemplate(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long templateId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synTarGrpTemplateService.deleteSingleTarGrp(templateId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个分群模板失败！Exception: ",templateId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步客户分群
     * @return
     */
    @RequestMapping("batchTarGrp")
    @CrossOrigin
    public String batchTemplate(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synTarGrpTemplateService.synchronizeBatchTarGrp(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步分群模板失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }



    /**
     * 同步单个营销维挽活动
     * @return
     */
    @PostMapping("singleCampaignRel")
    @CrossOrigin
    public String singleCampaignRel(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long campaignRelId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synMktCampaignRelService.synchronizeSingleCampaignRel(campaignRelId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个营销维挽活动失败！Exception: ",campaignRelId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 全量同步营销维挽活动
     * @return
     */
    @RequestMapping("batchCampaignRel")
    @CrossOrigin
    public String batchCampaignRel(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synMktCampaignRelService.synchronizeBatchCampaignRel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步营销维挽活动失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 同步活动到大数据
     *
     * @param params
     * @return
     */
    @PostMapping("/syncActivity")
    @CrossOrigin
    public String SyncActivity(@RequestBody Map<String, Object> params) {
        logger.info("同步单个营销维挽活动");
        Long mktCampaignId = Long.valueOf((Integer) params.get("mktCampaignId"));
        String roleName = getRole();   //  操作角色
        ResponseHeaderModel responseHeaderModel = new ResponseHeaderModel();
        try {
            responseHeaderModel = syncActivityService.syncActivity(mktCampaignId);
        } catch (Exception e) {
            responseHeaderModel.setResultCode("1");
            responseHeaderModel.setResultMessage("同步失败！");
            logger.error("同步活动到大数据失败！Exception: ", mktCampaignId, e);
        }
        return JSON.toJSONString(responseHeaderModel);
    }


    /**
     * 选中的活动同步到大数据
     *
     * @param params
     * @return
     */
    @PostMapping("/syncPartActivity")
    @CrossOrigin
    public String syncPartActivity(@RequestBody Map<String, Object> params) {
        Map<String,Object> result = new HashMap<>();
        List<Integer> mktCampaignIdList = (List<Integer>)params.get("mktCampaignIdList");
        try {
            for(int i=0;i<mktCampaignIdList.size();i++) {
                Long mktCampaignId = Long.valueOf(mktCampaignIdList.get(i));
                syncActivityService.syncActivity(mktCampaignId);
            }
            result.put("resultCode", CommonConstant.CODE_SUCCESS);
            result.put("resultMsg", "同步成功");
            result.put("mktCampaignIdList", mktCampaignIdList);
        } catch (Exception e) {
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", "同步失败！");
            logger.error("同步活动到大数据失败！Exception: ", e);
        }
        return JSON.toJSONString(result);
    }


    /**
     * 所有活动同步到大数据
     */
    @PostMapping("/syncTotalActivity")
    @CrossOrigin
    public String syncTotalActivity() {
        Map<String,Object> result = new HashMap<>();
        try {
            result = syncActivityService.syncTotalActivity();
        } catch (Exception e) {
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg", "同步失败！");
            logger.error("同步活动到大数据失败！Exception: ", e);
        }
        return JSON.toJSONString(result);
    }



    /**
     * 权限控制 获取角色身份
     * @return
     */
    public String getRole(){
        SysStaff sysStaff = (SysStaff) SecurityUtils.getSubject().getPrincipal();
        if(sysStaff==null){
            return "admin";
        }
        return sysStaff.getStaffCode();
    }


    @PostMapping("/synchronizeCampaign")
    @CrossOrigin
    public String synchronizeCampaignServiceImpl(@RequestBody Map<String, Object> params) throws Exception {
        Long mktCampaignId = Long.valueOf((Integer) params.get("mktCampaignId"));
        String roleName = getRole();   //  操作角色
        Map<String, Object> synchronizeCampaignMap = synchronizeCampaignService.synchronizeCampaign(mktCampaignId, roleName);
        return JSON.toJSONString(synchronizeCampaignMap);
    }


    /**
     *
     * 删除活动下的redis缓存 -- 生产
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/deleteCampaignRedisProd")
    @CrossOrigin
    public String deleteCampaignRedisProd(@RequestBody Map<String, Object> params) throws Exception {
        Long mktCampaignId = Long.valueOf((Integer) params.get("mktCampaignId"));
        Map<String, Object> deleteCampaignRedisMap = synchronizeCampaignService.deleteCampaignRedisProd(mktCampaignId);
        return JSON.toJSONString(deleteCampaignRedisMap);
    }

    /**
     *
     * 删除活动下的redis缓存 -- 准生产
     * @param params
     * @return
     * @throws Exception
     */
    @PostMapping("/deleteCampaignRedisPre")
    @CrossOrigin
    public String deleteCampaignRedisPre(@RequestBody Map<String, Object> params) throws Exception {
        Long mktCampaignId = Long.valueOf((Integer) params.get("mktCampaignId"));
        Map<String, Object> deleteCampaignRedisMap = synchronizeCampaignService.deleteCampaignRedisPre(mktCampaignId);
        return JSON.toJSONString(deleteCampaignRedisMap);
    }


    /**
     * 单个标签同步
     * @return
     */
    @PostMapping("singleInjectionLabel")
    @CrossOrigin
    public String singleInjectionLabel(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long id = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synLabelService.synchronizeSingleLabel(id,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("同步单个标签失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个标签同步删除
     * @return
     */
    @DeleteMapping("singleInjectionLabel")
    @CrossOrigin
    public String deleteInjectionLabel(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        Long id = Long.valueOf((Integer) params.get("id"));
        try{
            map = synLabelService.deleteSingleLabel(id,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("同步单个标签失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }



    /**
     * 全量同步标签
     * @return
     */
    @RequestMapping("batchInjectionLabel")
    @CrossOrigin
    public String batchInjectionLabel(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synLabelService.synchronizeBatchLabel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步标签失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }



    /**
     * 单个标签组同步
     * @return
     */
    @PostMapping("singleInjectionLabelGrp")
    @CrossOrigin
    public String singleInjectionLabelGrp(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventTypeId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synLabelGrpService.synchronizeSingleLabel(eventTypeId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtTypeServiceImpl] 通过主键同步单个标签组失败！Exception: ",eventTypeId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 单个标签组同步 删除
     * @return
     */
    @DeleteMapping("singleInjectionLabelGrp")
    @CrossOrigin
    public String deleteInjectionLabelGrp(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventTypeId = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synLabelGrpService.deleteSingleLabel(eventTypeId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("[op:SynContactEvtTypeServiceImpl] 通过主键同步单个标签组失败！Exception: ",eventTypeId,e);
        }
        return JSON.toJSONString(map);
    }

    /**
     * 全量同步标签组
     * @return
     */
    @RequestMapping("batchInjectionLabelGrp")
    @CrossOrigin
    public String batchInjectionLabelGrp(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synLabelGrpService.synchronizeBatchLabel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步标签组失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个调查问卷 新增 修改
     * @return
     */
    @RequestMapping("singleQuestion")
    @CrossOrigin
    public String singleQuestion(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long id = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synQuestionService.synQuestion(roleName,id);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("同步单个调查问卷失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 同步单个调查问卷 删除
     * @return
     */
    @DeleteMapping("singleQuestion")
    @CrossOrigin
    public String deleteSingleQuestionn(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long id = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synQuestionService.deleteQuestion(roleName,id);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("同步单个调查问卷失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 全量同步调查问卷
     * @return
     */
    @RequestMapping("batchQuestion")
    @CrossOrigin
    public String batchQuestionn(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synQuestionService.synchronizeBatchQuestion(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步问卷题库失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }



    /**
     * 同步单个问卷题库 新增 修改
     * @return
     */
    @RequestMapping("singleQuestionBank")
    @CrossOrigin
    public String singleQuestionnBank(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long id = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synQuestionService.synQuestionBank(roleName,id);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("同步单个问卷题库失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 同步单个问卷题库 删除
     * @return
     */
    @DeleteMapping("singleQuestionBank")
    @CrossOrigin
    public String deleteQuestionnBank(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long id = Long.valueOf((Integer) params.get("id"));
        Map<String, Object> map=new HashMap<>();
        try{
            map = synQuestionService.deleteQuestionBank(roleName,id);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("同步单个问卷题库失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }

    /**
     * 全量同步问卷题库
     * @return
     */
    @RequestMapping("batchQuestionBank")
    @CrossOrigin
    public String batchQuestionnBank(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synQuestionService.synchronizeBatchQuestionBank(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("全量同步问卷题库失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }
}
