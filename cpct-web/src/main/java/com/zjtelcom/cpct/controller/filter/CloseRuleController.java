package com.zjtelcom.cpct.controller.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dto.filter.CloseRule;
import com.zjtelcom.cpct.dto.filter.CloseRuleAddVO;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.request.filter.CloseRuleReq;
import com.zjtelcom.cpct.service.MqService;
import com.zjtelcom.cpct.service.filter.CloseRuleService;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@RestController
@RequestMapping("${adminPath}/filter")
public class CloseRuleController extends BaseController {

    @Autowired
    private CloseRuleService closeRuleService;


    /**
     * 通过过滤标签集合获取标签列表
     *
     * @param params
     * @return
     */
    @RequestMapping("/getQryFilterRuleByIdList")
    @CrossOrigin
    public String qryFilterRuleByIdList(@RequestBody Map<String, Object> params) {
        Map<String, Object> closeRuleListMap = new HashMap<>();
        List<Integer> closeRuleIdList = (List<Integer>) params.get("filterRuleIdList");
        try {
            closeRuleListMap = closeRuleService.getFilterRule(closeRuleIdList);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to get filterRuleIdList by filterRuleIdList = {}! Exception: ", JSONArray.toJSON(closeRuleIdList), e);
            return JSON.toJSONString(closeRuleListMap);
        }
        return JSON.toJSONString(closeRuleListMap);
    }

    /**;
     * 查询过滤规则列表(含分页)
     */
    @RequestMapping("/getQryFilterRule")
    @CrossOrigin
    public String qryFilterRule(@RequestBody CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.qryFilterRule(closeRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询过滤规则列表(不含分页)
     */
    @RequestMapping("/getQryFilterRules")
    @CrossOrigin
    public String qryFilterRules(@RequestBody CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.qryFilterRules(closeRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除过滤规则
     */
    @RequestMapping("/delByFilterRule")
    @CrossOrigin
    public String delFilterRule(@RequestBody CloseRule closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.delFilterRule(closeRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询单个过滤规则
     */
    @RequestMapping("/getByFilterRule")
    @CrossOrigin
    public String getFilterRule(@RequestBody CloseRule closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.getFilterRule(closeRule.getRuleId());
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增过滤规则
     */
    @RequestMapping("/insertFilterRule")
    @CrossOrigin
    public String createFilterRule(@RequestBody CloseRuleAddVO closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.createFilterRule(closeRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改过滤规则
     */
    @RequestMapping("/modByFilterRule")
    @CrossOrigin
    public String modFilterRule(@RequestBody CloseRuleAddVO closeRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.modFilterRule(closeRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(closeRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 导入销售品
     */
    @RequestMapping("/importProductList")
    @CrossOrigin
    public String importProductList(MultipartFile file, Long ruleId, String closeName, String closeType, String offerInfo, String productType, String closeCode, Long[] rightListId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            InputStream inputStream = file.getInputStream();
            byte[] bytes = new byte[3];
            inputStream.read(bytes,0,bytes.length);
            String head = ChannelUtil.bytesToHexString(bytes);
            head = head.toUpperCase();
            if (!head.equals("D0CF11") && !head.equals("504B03")){
                maps.put("resultCode", CODE_FAIL);
                maps.put("resultMsg", "文件格式不正确");
                return JSON.toJSONString(maps);
            }
            maps = closeRuleService.importProductList(file, ruleId, closeName, closeType, offerInfo, productType, closeCode, rightListId);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for multipartFile = {}! Exception: ", JSONArray.toJSON(file), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**;
     * 查询过滤规则列表(含分页)
     * 关单类型过滤 和 用户登入匹配查看（自己看自己）
     */
    @PostMapping("/qryCloseRuleForUser")
    @CrossOrigin
    public String qryCloseRuleForUser(@RequestBody CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.qryCloseRuleForUser(closeRuleReq);
        } catch (Exception e) {
            logger.error("[op:qryCloseRuleForUser] fail to listEvents for CloseRule = {}! Exception: ", JSONArray.toJSON(closeRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }


    /**
     * 受理关单规则	2000
     欠费关单规则	3000 排除
     拆机关单规则	1000 排除
     按账期欠费关单	4000
     标签关单规则	5000
     去掉排除 展示其余
     * @param closeRuleReq
     * @return
     */
    @PostMapping("/getCloseRuleOut")
    @CrossOrigin
    public String getCloseRuleOut(@RequestBody CloseRuleReq closeRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = closeRuleService.getCloseRuleOut(closeRuleReq);
        } catch (Exception e) {
            logger.error("[op:qryCloseRuleForUser] fail to listEvents for CloseRule = {}! Exception: ", JSONArray.toJSON(closeRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }



   /* @Autowired
    private EsHitsService esHitService;
    @PostMapping("addLogToEsTest")
    public String addLogToEsTest(){
        //初始化es log
        JSONObject esJson = new JSONObject();
        esJson.put("reqId", "1");
        esJson.put("activityId", "1");
        esJson.put("activityName", "测试");
        esJson.put("activityCode", "999");
        esJson.put("hitEntity", "测试场"); //命中对象
        esJson.put("hit", false);
        esJson.put("msg", "红黑名单过滤规则验证被拦截");
        esHitService.save(esJson,"activity_1234");
        return null;
    }*/
}
