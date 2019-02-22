package com.zjtelcom.cpct.controller.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.filter.FilterRuleAddVO;
import com.zjtelcom.cpct.dto.filter.FilterRuleVO;
import com.zjtelcom.cpct.dto.user.UserList;
import com.zjtelcom.cpct.request.filter.FilterRuleReq;
import com.zjtelcom.cpct.service.filter.FilterRuleService;
import com.zjtelcom.cpct.util.ChannelUtil;
import org.apache.ibatis.annotations.Param;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

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


    /**
     * 通过过滤标签集合获取标签列表
     *
     * @param params
     * @return
     */
    @RequestMapping("/qryFilterRuleByIdList")
    @CrossOrigin
    public String qryFilterRuleByIdList(@RequestBody Map<String, Object> params) {
        Map<String, Object> filterRuleListMap = new HashMap<>();
        List<Integer> filterRuleIdList = (List<Integer>) params.get("filterRuleIdList");
        try {
            filterRuleListMap = filterRuleService.getFilterRule(filterRuleIdList);
        } catch (Exception e) {
            logger.error("[op:FilterRuleController] fail to get filterRuleIdList by filterRuleIdList = {}! Exception: ", JSONArray.toJSON(filterRuleIdList), e);
            return JSON.toJSONString(filterRuleListMap);
        }
        return JSON.toJSONString(filterRuleListMap);
    }

    /**;
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
            maps = filterRuleService.getFilterRule(filterRule.getRuleId());
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
    public String createFilterRule(@RequestBody FilterRuleAddVO filterRule) {
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
    public String modFilterRule(@RequestBody FilterRuleAddVO filterRule) {
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
            String fileName = "用户名单.xls";

            String filePath = getClass().getResource("/file/" + "templete.xlsx").getPath();
            FileInputStream input = new FileInputStream(filePath);

            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //
            BufferedInputStream bis = null;
            fis = new FileInputStream("cpct-web/src/main/resources/file/templete.xlsx");
            bis = new BufferedInputStream(fis);

            //处理导出问题
            response.reset();
            response.setContentType(CommonConstant.CONTENTTYPE);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ouputStream = response.getOutputStream();

            int len = 0;
            while ((len = bis.read(buffer)) > 0) {
                ouputStream.write(buffer, 0, len);
            }
            int i = bis.read(buffer);
//            while (i != -1) {
//                ouputStream.write(buffer);
//                i = bis.read(buffer);
//            }
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
        return initSuccRespInfo("导出成功");
    }



    /**
     * 下发文件导入模板下载
     */
    @RequestMapping("downloadTrialOperationTemplate")
    @CrossOrigin
    public String downloadTrialOperationTemplate(HttpServletRequest request, HttpServletResponse response) {
        OutputStream ouputStream = null;
        try {
            String fileName = "下发导入模板.xls";

            String filePath = getClass().getResource("/file/" + "trialOperationTemplate.xlsx").getPath();
            FileInputStream input = new FileInputStream(filePath);

            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            fis = new FileInputStream("cpct-web/src/main/resources/file/trialOperationTemplate.xlsx");
            bis = new BufferedInputStream(fis);

            //处理导出问题
            response.reset();
            response.setContentType(CommonConstant.CONTENTTYPE);
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ouputStream = response.getOutputStream();

            int len = 0;
            while ((len = bis.read(buffer)) > 0) {
                ouputStream.write(buffer, 0, len);
            }
            int i = bis.read(buffer);
//            while (i != -1) {
//                ouputStream.write(buffer);
//                i = bis.read(buffer);
//            }
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
        return initSuccRespInfo("导出成功");
    }


    /**
     * 导出名单
     */
    @RequestMapping("/outPutUserList")
    public void buildExcelDocument(HSSFWorkbook workBook, HttpServletRequest request, HttpServletResponse response,Long filterRuleId) {

        try {
            Map<String,Object> filterRuleMap = filterRuleService.getFilterRule(filterRuleId);
            if (!filterRuleMap.get("resultCode").equals("200")){
                logger.info("过滤规则不存在");
            }
            FilterRuleVO filterRule = (FilterRuleVO)filterRuleMap.get("filterRule");
            List<String> phoneList = ChannelUtil.StringToList(filterRule.getUserList());
            //excel文件名
            String fileName = filterRule.getRuleName()+"名单.xls";
            //设置响应的编码格式
            response.setCharacterEncoding("UTF-8");
            //设置响应类型
            response.setContentType("application/ms-excel");
            //设置响应头
            response.setHeader("Content-Disposition",
                    "inline;filename="+
                            new String(fileName.getBytes(),"iso8859-1"));
            //创建一个sheet标签
            HSSFSheet sheet = workBook.createSheet(filterRule.getRuleName()+"名单");
            //创建第一行（头）
            for (int i = 0; i< phoneList.size() ;i++){
                HSSFRow row = sheet.createRow(i);
                row.createCell(0).setCellValue(phoneList.get(i));
            }
            //通过repsonse获取输出流
            OutputStream outputStream = response.getOutputStream();
            workBook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    /**
     * 导入名单
     */
    @RequestMapping("/importUserList")
    @CrossOrigin
    public String importUserList(MultipartFile file, @Param("ruleId") Long ruleId) {
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
