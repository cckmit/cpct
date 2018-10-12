package com.zjtelcom.cpct.controller.synchronize;

import com.alibaba.fastjson.JSON;
import com.zjhcsoft.eagle.main.dubbo.model.policy.ResponseHeaderModel;
import com.zjtelcom.cpct.config.RedisConfig;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.domain.question.Question;
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
import lombok.val;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:同步数据 准生产数据同步到生产环境
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
    @Autowired
    private SynchronizeCampaignService synchronizeCampaignService;
    @Autowired
    private SynLabelGrpService synLabelGrpService;
    @Autowired
    private SynQuestionService synQuestionService;
    @Autowired
    private SyncActivityService syncActivityService;



    /**
     * 单个事件同步
     * @return
     */
    @PostMapping("singleEvent")
    @CrossOrigin
    public String singleEvent(@RequestBody Map<String, Object> params) {
        String roleName = getRole();   //  操作角色
        Long eventId = Long.valueOf((Integer) params.get("eventId"));
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
        Long eventTypeId = Long.valueOf((Integer) params.get("eventTypeId"));
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
     * 单个事件源同步
     * @return
     */
    @PostMapping("singleEventSource")
    @CrossOrigin
    public String singleEventSource(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long eventSourceId = Long.valueOf((Integer) params.get("eventSourceId"));
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
        Long eventInterfaceId = Long.valueOf((Integer) params.get("eventInterfaceId"));
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
        Long eventSceneId = Long.valueOf((Integer) params.get("eventSceneId"));
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
            logger.error("全量同步事件目录失败！Exception: ", e);
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
        Long eventSceneTypeId = Long.valueOf((Integer) params.get("eventSceneTypeId"));
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
        Long channelId = Long.valueOf((Integer) params.get("channelId"));
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
        Long scriptId = Long.valueOf((Integer) params.get("scriptId"));
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
        Long staffId = Long.valueOf((Integer) params.get("staffId"));
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
        Long roleId = Long.valueOf((Integer) params.get("roleId"));
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
        Long menuId = Long.valueOf((Integer) params.get("menuId"));
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
        Long paramId = Long.valueOf((Integer) params.get("paramId"));
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
        Long ruleId = Long.valueOf((Integer) params.get("ruleId"));
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
        Long labelId = Long.valueOf((Integer) params.get("labelId"));
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
     * 同步单个分群模板
     * @return
     */
    @PostMapping("singleTemplate")
    @CrossOrigin
    public String singleTemplate(@RequestBody Map<String, Object> params){
        String roleName=getRole();   //  操作角色
        Long templateId = Long.valueOf((Integer) params.get("templateId"));
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
     * 全量同步分群模板
     * @return
     */
    @RequestMapping("batchTemplate")
    @CrossOrigin
    public String batchTemplate(){
        String roleName=getRole();   //  操作角色
        Map<String, Object> map=new HashMap<>();
        try{
            map = synTarGrpTemplateService.synchronizeBatchTemplate(roleName);
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
        Long campaignRelId = Long.valueOf((Integer) params.get("campaignRelId"));
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
            responseHeaderModel.setResultMessage("1");
            responseHeaderModel.setResultMessage("同步失败！");
            logger.error("同步活动到大数据失败！Exception: ", mktCampaignId, e);
        }
        return JSON.toJSONString(responseHeaderModel);
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
            logger.error("全量同步调查问卷失败！Exception: ", e);
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
