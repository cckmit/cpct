package com.zjtelcom.cpct.controller.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.user.UserListMapper;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.enums.ErrorCode;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import com.zjtelcom.cpct.service.filter.FilterRuleService;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 规律规则controller
 * @Author pengy
 * @Date 2018/7/3 10:27
 */
@RestController
@RequestMapping("${adminPath}/filter")
public class FilterRuleController extends BaseController {

    @Autowired
    private FilterRuleService filterRuleService;
    @Autowired
    private UserListMapper userListMapper;


    /**
     * 查询过滤规则列表(含分页)
     */
    @RequestMapping("/qryFilterRule")
    @CrossOrigin
    public String qryFilterRule(@RequestBody FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.qryFilterRule(filterRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询过滤规则列表(不含分页)
     */
    @RequestMapping("/qryFilterRules")
    @CrossOrigin
    public String qryFilterRules(@RequestBody FilterRuleReq filterRuleReq) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.qryFilterRules(filterRuleReq);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRuleReq), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 删除过滤规则
     */
    @RequestMapping("/delFilterRule")
    @CrossOrigin
    public String delFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.delFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 查询单个过滤规则
     */
    @RequestMapping("/getFilterRule")
    @CrossOrigin
    public String getFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.getFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 新增过滤规则
     */
    @RequestMapping("/createFilterRule")
    @CrossOrigin
    public String createFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.createFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 修改过滤规则
     */
    @RequestMapping("/modFilterRule")
    @CrossOrigin
    public String modFilterRule(@RequestBody FilterRule filterRule) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.modFilterRule(filterRule);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for filterRule = {}! Exception: ", JSONArray.toJSON(filterRule), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 下载模板
     */
    @RequestMapping("downloadTemplate")
    @CrossOrigin
    public String downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
        OutputStream ouputStream = null;
        try {
            String fileName = "用户名单.xlsx";
            File file = new File("cpct-web/src/main/resources/file/用户名单.xlsx");
            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            //处理导出问题
            response.reset();
            response.setContentType(CommonConstant.CONTENTTYPE);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ouputStream = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                ouputStream.write(buffer);
                i = bis.read(buffer);
            }
            ouputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ouputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return initSuccRespInfo(null);
    }

    /**
     * 导入名单
     */
    @RequestMapping("/importUserList")
    @CrossOrigin
    public String importUserList(MultipartFile file, @Param("ruleId") Long ruleId) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.importUserList(file, ruleId);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for multipartFile = {}! Exception: ", JSONArray.toJSON(file), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

    /**
     * 缓存取出用户信息
     */
    @RequestMapping("/listUserList")
    @CrossOrigin
    public String listUserList(@RequestBody UserList userList) {
        Map<String, Object> maps = new HashMap<>();
        try {
            maps = filterRuleService.listUserList(userList);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to listEvents for userList = {}! Exception: ", JSONArray.toJSON(userList), e);
            return JSON.toJSONString(maps);
        }
        return JSON.toJSONString(maps);
    }

}
