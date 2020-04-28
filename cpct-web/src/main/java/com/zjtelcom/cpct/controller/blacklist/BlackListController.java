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
    public void downloadExcel(HttpServletResponse response, HttpServletRequest request) throws IOException {
        String encode = "utf-8";
        response.setContentType("text/html;charset=" + encode);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String downLoadPath = "G:\\cpct-pst\\cpct\\cpct-web\\src\\main\\resources\\file\\offerTemplate.xlsx";
        try {
            File file = new File(downLoadPath);
            long fileLength = file.length();
            String fileName = file.getName();
            response.setContentType("application/vnd.ms-excel;");
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes(encode), "ISO8859-1"));
            response.setHeader("Content-Length", String.valueOf(fileLength));
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int len;
            while (-1 != (len = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, len);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            if (bis != null)
                try {
                    bis.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
            if (bos != null)
                try {
                    bos.close();
                } catch (IOException e) {
                    logger.error(e.getMessage());
                }
        }

    }





}

