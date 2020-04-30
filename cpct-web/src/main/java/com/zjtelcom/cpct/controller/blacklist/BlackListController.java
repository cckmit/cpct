package com.zjtelcom.cpct.controller.blacklist;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.controller.BaseController;
import com.zjtelcom.cpct.service.blacklist.BlackListCpctService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${adminPath}/blacklist")
public class BlackListController extends BaseController {

    @Autowired
    private BlackListCpctService blackListCpctService;

    /*导出黑名单*/
    @RequestMapping("/exportBlackListFileManage")
    @CrossOrigin
    public void exportBlackListFileManage(HttpServletResponse response){
        Map<String,Object> result = new HashMap<>();
        try{
            blackListCpctService.exportBlackListFileManage(response);
        }catch (Exception e){
            logger.error("导出黑名单失败",e);
            e.printStackTrace();
        }
    }

    /*导入黑名单*/
    @RequestMapping("/importBlackListFileManage")
    @CrossOrigin
    public  Map<String,Object> importBlackListFileManage(@RequestParam(value = "file") MultipartFile multipartFile){
        Map<String,Object> result = new HashMap<>();
        try{
            result = blackListCpctService.importBlackListFileManage(multipartFile);
        }catch (Exception e){
            logger.error("导入黑名单失败",e);
            e.printStackTrace();
        }
        return result;
    }

    /*模糊查询*/
    @PostMapping("/getBlackListByKey")
    @CrossOrigin
    public  Map<String,Object> getBlackListPageByKey(@RequestBody Map<String,Object> pageParams){
        Map<String,Object> result = new HashMap<>();
        try{
            result = blackListCpctService.getBlackListPageByKey(pageParams);
        }catch (Exception e){
            logger.error("模糊查询失败",e);
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 导出黑名单
     *
     * @return
     */
    @PostMapping("/export")
    @CrossOrigin
    public Map<String, Object> export(){
        Map<String, Object> result = new HashMap<>();
        try {
            result = blackListCpctService.exportBlackListFile();
        } catch (Exception e) {
            logger.error("[op:BlackListController] fail to exportBlackListFile",e);
        }
        return result;
    }


    /**
     * 导入黑名单
     *
     * @return
     */
    @PostMapping("/import")
    @CrossOrigin
    public Map<String, Object> importBlackList(){
        Map<String, Object> result = new HashMap<>();
        try {
            result = blackListCpctService.importBlackListFile();
        } catch (Exception e) {
            logger.error("[op:BlackListController] fail to importBlackListFile",e);
        }
        return result;
    }


    /*导出模板*/
    @RequestMapping("/exportTemplate")
    @CrossOrigin
    public String downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        OutputStream ouputStream = null;
        try {
            String fileName = "全局黑名单模板.xlsx";

            byte[] buffer = new byte[1024];
            FileInputStream fis = null; //文件输入流
            BufferedInputStream bis = null;
            fis = new FileInputStream("D:/blacklist_template.xlsx");
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





}

