package com.zjtelcom.cpct.controller.synchronize;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.synchronize.*;
import com.zjtelcom.cpct.service.synchronize.campaign.SynMktCampaignRelService;
import com.zjtelcom.cpct.service.synchronize.channel.SynChannelService;
import com.zjtelcom.cpct.service.synchronize.filter.SynFilterRuleService;
import com.zjtelcom.cpct.service.synchronize.label.SynLabelService;
import com.zjtelcom.cpct.service.synchronize.label.SynMessageLabelService;
import com.zjtelcom.cpct.service.synchronize.script.SynScriptService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysMenuService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysParamsService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysRoleService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysStaffService;
import com.zjtelcom.cpct.service.synchronize.template.SynTarGrpTemplateService;
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
            logger.error("批量同步事件源接口失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件场景同步
     * @param eventSceneId
     * @return
     */
    @PostMapping("singleEventScene")
    @CrossOrigin
    public String singleEventScene(@RequestParam(value = "eventSceneId", required = true) Long eventSceneId){
        logger.info("同步事件场景");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSceneService.synchronizeSingleEventScene(eventSceneId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过主键同步单个事件目录失败！Exception: ",eventSceneId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 批量事件场景同步
     * @return
     */
    @PostMapping("batchEventScene")
    @CrossOrigin
    public String batchEventScene(){
        //角色权限控制
        logger.info("批量同步事件场景");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSceneService.synchronizeBatchEventScene(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步事件目录失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个事件场景目录同步
     * @param eventSceneTypeId  事件场景目录主键
     * @return
     */
    @PostMapping("singleEventSceneType")
    @CrossOrigin
    public String singleEventSceneType(@RequestParam(value = "eventSceneTypeId", required = true) Long eventSceneTypeId){
        logger.info("同步事件场景目录");
        String roleName=getRole();   //  操作角色
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
     * 批量同步事件场景目录
     * @return
     */
    @PostMapping("batchEventSceneType")
    @CrossOrigin
    public String batchEventSceneType(){
        //角色权限控制
        logger.info("批量同步事件场景目录");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synEventSceneTypeService.synchronizeBatchEventSceneType(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步事件场景目录失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个渠道同步
     * @param channelId  渠道id
     * @return
     */
    @PostMapping("singleContactChannel")
    @CrossOrigin
    public String singleContactChannel(@RequestParam(value = "channelId", required = true) Long channelId){
        logger.info("同步渠道");
        String roleName=getRole();   //  操作角色
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
     * 批量渠道同步
     * @return
     */
    @PostMapping("batchContactChannel")
    @CrossOrigin
    public String batchContactChannel(){
        //角色权限控制
        logger.info("批量同步渠道");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synChannelService.synchronizeBatchChannel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步渠道失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 单个接触脚本同步
     * @param scriptId
     * @return
     */
    @PostMapping("singleScript")
    @CrossOrigin
    public String singleScript(@RequestParam(value = "scriptId", required = true) Long scriptId){
        logger.info("同步接触脚本");
        String roleName=getRole();   //  操作角色
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
     * 批量同步接触脚本
     * @return
     */
    @PostMapping("batchContactScript")
    @CrossOrigin
    public String batchContactScript(){
        //角色权限控制
        logger.info("批量同步接触脚本");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synScriptService.synchronizeBatchScript(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步接触脚本失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }


    /**
     * 同步单个用户
     * @param staffId
     * @return
     */
    @PostMapping("singleStaff")
    @CrossOrigin
    public String singleStaff(@RequestParam(value = "staffId", required = true) Long staffId){
        logger.info("同步单个用户");
        String roleName=getRole();   //  操作角色
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
     * 批量同步用户信息
     * @return
     */
    @PostMapping("batchStaff")
    @CrossOrigin
    public String batchStaff(){
        //角色权限控制
        logger.info("批量同步用户信息");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysStaffService.synchronizeBatchStaff(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步用户信息失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个角色信息
     * @param roleId
     * @return
     */
    @PostMapping("singleRole")
    @CrossOrigin
    public String singleRole(@RequestParam(value = "roleId", required = true) Long roleId){
        logger.info("同步单个角色信息");
        String roleName=getRole();   //  操作角色
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
     * 批量同步角色信息
     * @return
     */
    @PostMapping("batchRole")
    @CrossOrigin
    public String batchRole(){
        //角色权限控制
        logger.info("批量同步角色信息");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysRoleService.synchronizeBatchRole(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步角色信息失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个菜单信息
     * @param menuId
     * @return
     */
    @PostMapping("singleMenu")
    @CrossOrigin
    public String singleMenu(@RequestParam(value = "menuId", required = true) Long menuId){
        logger.info("同步单个菜单信息");
        String roleName=getRole();   //  操作角色
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
     * 批量同步菜单信息
     * @return
     */
    @PostMapping("batchMenu")
    @CrossOrigin
    public String batchMenu(){
        //角色权限控制
        logger.info("批量同步菜单信息");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysMenuService.synchronizeBatchMenu(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步菜单信息失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个静态参数
     * @param paramId
     * @return
     */
    @PostMapping("singleParams")
    @CrossOrigin
    public String singleParams(@RequestParam(value = "paramId", required = true) Long paramId){
        logger.info("同步单个静态参数");
        String roleName=getRole();   //  操作角色
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
     * 批量同步静态参数
     * @return
     */
    @PostMapping("batchParams")
    @CrossOrigin
    public String batchParams(){
        //角色权限控制
        logger.info("批量同步静态参数");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synSysParamsService.synchronizeBatchParam(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步静态参数失败！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个过滤规则
     * @param ruleId
     * @return
     */
    @PostMapping("singleFilterRule")
    @CrossOrigin
    public String singleFilterRule(@RequestParam(value = "ruleId", required = true) Long ruleId){
        logger.info("同步单个过滤规则");
        String roleName=getRole();   //  操作角色
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
     * 批量同步标签
     * @return
     */
    @PostMapping("batchFilterRule")
    @CrossOrigin
    public String batchFilterRule(){
        //角色权限控制
        logger.info("批量同步过滤规则");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synFilterRuleService.synchronizeBatchFilterRule(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步过滤规则！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个试运算标签展示列
     * @param labelId
     * @return
     */
    @PostMapping("singleMessageLabel")
    @CrossOrigin
    public String singleMessageLabel(@RequestParam(value = "labelId", required = true) Long labelId){
        logger.info("同步单个试运算标签展示列");
        String roleName=getRole();   //  操作角色
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
     * 批量同步试运算标签展示列
     * @return
     */
    @PostMapping("batchMessageLabel")
    @CrossOrigin
    public String batchMessageLabel(){
        //角色权限控制
        logger.info("批量同步试运算标签展示列");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synMessageLabelService.synchronizeBatchMessageLabel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步试运算标签展示列！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }




    /**
     * 同步单个分群模板
     * @param templateId
     * @return
     */
    @PostMapping("singleTemplate")
    @CrossOrigin
    public String singleTemplate(@RequestParam(value = "templateId", required = true) Long templateId){
        logger.info("同步单个分群模板");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synTarGrpTemplateService.synchronizeSingleTemplate(templateId,roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("通过id同步单个分群模板失败！Exception: ",templateId,e);
        }
        return JSON.toJSONString(map);
    }


    /**
     * 批量同步标签
     * @return
     */
    @PostMapping("batchTemplate")
    @CrossOrigin
    public String batchTemplate(){
        //角色权限控制
        logger.info("批量同步分群模板");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synTarGrpTemplateService.synchronizeBatchTemplate(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步分群模板！Exception: ", e);
        }
        return  JSON.toJSONString(map);
    }



    /**
     * 同步单个营销维挽活动
     * @param campaignRelId
     * @return
     */
    @PostMapping("singleCampaignRel")
    @CrossOrigin
    public String singleCampaignRel(@RequestParam(value = "campaignRelId", required = true) Long campaignRelId){
        logger.info("同步单个营销维挽活动");
        String roleName=getRole();   //  操作角色
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
     * 批量同步营销维挽活动
     * @return
     */
    @PostMapping("batchCampaignRel")
    @CrossOrigin
    public String batchCampaignRel(){
        //角色权限控制
        logger.info("批量同步营销维挽活动");
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synMktCampaignRelService.synchronizeBatchCampaignRel(roleName);
        } catch (Exception e) {
            map.put("resultCode", CommonConstant.CODE_FAIL);
            map.put("resultMsg", e.getMessage());
            logger.error("批量同步营销维挽活动！Exception: ", e);
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
