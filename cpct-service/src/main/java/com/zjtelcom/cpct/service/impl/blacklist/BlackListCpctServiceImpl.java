package com.zjtelcom.cpct.service.impl.blacklist;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jcraft.jsch.ChannelSftp;
import com.zjtelcom.cpct.bean.ResponseVO;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.blacklist.BlackListLogMapper;
import com.zjtelcom.cpct.dao.blacklist.BlackListMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import com.zjtelcom.cpct.domain.blacklist.BlackListLogDO;
import com.zjtelcom.cpct.service.blacklist.BlackListCpctService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.SftpUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;

@Service
@Transactional
public class BlackListCpctServiceImpl implements BlackListCpctService {

    private final static Logger log = LoggerFactory.getLogger(BlackListCpctServiceImpl.class);
    private final static String splitMark = "\u0007";
    private final static String superfield = "createStaff,updateStaff,createDate,updateDate";

    private String ftpAddress = "134.108.0.92";
    private int ftpPort = 22;
    private String ftpName = "ftp";
    private String ftpPassword = "V1p9*2_9%3#";
    private String exportPath = "/app/cpcp_cxzx/black_list_export/";
    private String importPath = "/app/cpcp_cxzx/black_list_import/";
    private String localHeadFilePath = "/app/";

    private final static int NUM = 5000;

    @Autowired
    private BlackListMapper blackListMapper;
    @Autowired
    ResponseVO responseVO;
    @Autowired
    BlackListLogMapper blackListLogMapper;
    private static final String SUCCESS_CODE = "0";
    private static final String FAIL_CODE = "1";

    /**
     * 从数据库导出黑名单上传ftp服务器
     *
     * @return
     */
    @Override
    public Map<String, Object> exportBlackListFile() {
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
        String Date = dateFormat.format(new Date());
        //String headFileName = "BLACK_LIST_HEAD_" + Date + ".dat";     //文件路径+名称+文件类型
        String dataFileName = "BLACK_LIST_" + Date + ".dat";     //文件路径+名称+文件类型
        //创建头文件
        //File headFile = new File(headFileName);
        //创建数据文件
        File dataFile = new File(dataFileName);
        SftpUtils sftpUtils = new SftpUtils();
        final ChannelSftp sftp = sftpUtils.connect(ftpAddress, ftpPort, ftpName, ftpPassword);
        boolean uploadResult = false;
        if (!dataFile.exists()) {
            // 如果文件不存在，则创建新的文件
            try {
                dataFile.createNewFile();
                //创建策略定义文件
                // 从数据库查询获取黑名单数据
//                List<BlackListDO> allBlackList = blackListMapper.getAllBlackList();

                int total = blackListMapper.getCountAll();
                int count = total / NUM;
                if (total % NUM > 1) {
                    count++;
                }
                for (int i = 0; i < count; i++) {
                    List<BlackListDO> allBlackList = new ArrayList<>();
                    if (i == count - 1) {
                        allBlackList = blackListMapper.getBlackListLimit(i * NUM, total - (i * NUM));
                    } else {
                        allBlackList = blackListMapper.getBlackListLimit(i * NUM,  NUM);
                    }
                    System.out.println("allBlackList.size() = " + allBlackList.size());
                    StringBuilder sline = new StringBuilder();
                    for (int j = 0; j < allBlackList.size(); j++) {
                        BlackListDO blackListDO = allBlackList.get(j);
                        if (blackListDO.getBlackId() != 0) {
                            sline.append(blackListDO.getBlackId());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getAssetPhone() != null) {
                            sline.append(blackListDO.getAssetPhone());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getServiceCate() != null) {
                            sline.append(blackListDO.getServiceCate());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getMaketingCate() != null) {
                            sline.append(blackListDO.getMaketingCate());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getPublicBenefitCate() != null) {
                            sline.append(blackListDO.getPublicBenefitCate());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getChannel() != null) {
                            sline.append(blackListDO.getChannel());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getStaffId() != null) {
                            sline.append(blackListDO.getStaffId());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getCreateStaff() != null) {
                            sline.append(blackListDO.getCreateStaff());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getCreateDate() != null) {

                            sline.append(DateUtil.date2StringDate(blackListDO.getCreateDate()));
                        }
                        sline.append(splitMark);
                        if (blackListDO.getUpdateStaff() != null) {
                            sline.append(blackListDO.getUpdateStaff());
                        }
                        sline.append(splitMark);
                        if (blackListDO.getUpdateDate() != null) {
                            sline.append(DateUtil.date2StringDate(blackListDO.getUpdateDate()));
                        }
                        sline.append(splitMark);
                        if (blackListDO.getOperType() != null) {
                            sline.append(blackListDO.getOperType());
                        }
                        if (j < allBlackList.size() - 1) {
                            sline.append("\r\n");
                        }
                    }
                    sftpUtils.writeFileContent(dataFile.getName(), sline.toString());
                }


                log.info("sftp已获得连接");
                sftpUtils.cd(exportPath, sftp);
                uploadResult = sftpUtils.uploadFile(exportPath, dataFile.getName(), new FileInputStream(dataFile), sftp);

                sftp.disconnect();
                resultMap.put("resultMsg", "success");
            } catch (Exception e) {
                log.error("黑名单数据文件dataFile失败！Expection = ", e);
                resultMap.put("resultMsg", "faile");
            } finally {
                if (uploadResult) {
                    log.info("上传成功，开始删除本地文件！");
                }
                boolean b1 = dataFile.delete();
                if (b1) {
                    log.info("删除本地文件成功！");
                }
                sftp.disconnect();
            }
        } else {
            log.info(dataFileName + "文件已存在！");
        }
        return resultMap;
    }

    /**
     * 从ftp服务器上下载文件，解析并导入到数据库中
     *
     * @return
     */
    @Override
    public Map<String, Object> importBlackListFile() {
        Map<String, Object> resultMap = new HashMap<>();
        List<BlackListDO> blackListDOS = new ArrayList<>();
        SftpUtils sftpUtils = new SftpUtils();
        final ChannelSftp sftp = sftpUtils.connect(ftpAddress, ftpPort, ftpName, ftpPassword);
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resultMap = new HashMap<>();
            // 下载文件
            String path = sftpUtils.cd(importPath, sftp);
            List<String> files = sftpUtils.listFiles(path, sftp);
            for (String fileName : files) {
                if (!"..".equals(fileName) && !".".equals(fileName) && fileName.toUpperCase().contains("HEAD") && !fileName.toUpperCase().endsWith(".FLG")) {
                    // 下载文件到本地
                    File file = new File(fileName);
                    if (!file.exists()) {
                        log.info("开始下载head文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                        sftpUtils.download(sftp, path + "/", fileName, localHeadFilePath);
                        log.info("结束下载head文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                    }
                }

                if (!"..".equals(fileName) && !".".equals(fileName) && !fileName.toUpperCase().contains("HEAD") && !fileName.toUpperCase().endsWith(".FLG")) {
                    // 下载文件到本地
                    File file = new File(fileName);
                    if (!file.exists()) {
                        log.info("开始下载文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                        sftpUtils.download(sftp, path + "/", fileName, localHeadFilePath);
                        log.info("结束下载文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                    }
                    boolean remove = sftpUtils.remove(sftp, importPath + '/' + fileName);
                    if(remove){
                        log.info("删除sftp服务器上的数据文件成功！");
                    }
                }

            }

            // 解析头文件
            File headFolders = new File(localHeadFilePath);
            File[] fileArray = headFolders.listFiles();
            StringBuilder head = new StringBuilder();
            String[] headArr = null;
            String[] dataArr = null;
            for (File headFile : fileArray) {
                if (headFile.isFile() && headFile.getName().toUpperCase().contains("HEAD")) {
                    File file = new File(localHeadFilePath + headFile.getName());
                    BufferedReader bufferedReader = null;
                    try {
                        FileInputStream reader = new FileInputStream(file);
                        bufferedReader = new BufferedReader(new InputStreamReader(reader, "UTF-8"));
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            headArr = line.split("\u0007");
                        }
                    } catch (Exception e) {
                        log.error("[op:updateDifferentNetEsData] failed to read head files = {} 失败！ SftpException=", file.getName(), e);
                    } finally {
                        bufferedReader.close();
                    }
                    //删除头文件
                    boolean delete = headFile.delete();
                    if (delete) {
                        log.info("删除头文件成功！");
                    }
                }
            }

            // 解析数据文件
            for (File dataFile : fileArray) {
                if (dataFile.isFile() && !dataFile.getName().toUpperCase().contains("HEAD") && dataFile.getName().toUpperCase().contains("BLACK_LIST")) {
                    File file = new File(localHeadFilePath + dataFile.getName());
                    BufferedReader bufferedReader = null;
                    try {
                        FileInputStream reader = new FileInputStream(file);
                        bufferedReader = new BufferedReader(new InputStreamReader(reader, "UTF-8"));
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            dataArr = line.split("\u0007");
                            try {
                                Class<?> c = Class.forName("com.zjtelcom.cpct.domain.blacklist.BlackListDO");
                                Class<?> superClass = c.getSuperclass();
                                BlackListDO BlackListDO = (BlackListDO) c.newInstance();
                                // Field[] declaredFields = c.getDeclaredFields();
                                for (int i = 0; i < headArr.length; i++) {
                                    String propName = fieldToProperty(headArr[i]);
                                    Field field;
                                    // 判断是否为父类的属性
                                    if (superfield.indexOf(propName) >= 0) {
                                        field = superClass.getDeclaredField(propName);
                                    } else {
                                        field = c.getDeclaredField(propName);
                                    }
                                    field.setAccessible(true);
                                    if (dataArr[i] != null && !"".equals(dataArr[i])) {
                                        if (field != null && field.getType() != null && "int".equals(field.getType().getName())) {
                                            field.set(BlackListDO, Integer.valueOf(dataArr[i]));
                                        } else if (field != null && field.getType() != null && "java.lang.Long".equals(field.getType().getName())) {
                                            field.set(BlackListDO, Long.valueOf(dataArr[i]));
                                        } else if (field != null && field.getType() != null && "java.util.Date".equals(field.getType().getName())) {
                                            field.set(BlackListDO, DateUtil.string2DateTime(dataArr[i]));
                                        } else if (field != null && field.getType() != null && "java.lang.String".equals(field.getType().getName())) {
                                            field.getType().cast(dataArr[i]);
                                            field.set(BlackListDO, dataArr[i]);
                                        }
                                    }
                                }
                                blackListDOS.add(BlackListDO);
                                log.info("BlackListDO--->>>" + JSON.toJSONString(BlackListDO));
                                blackListMapper.addBlackList(BlackListDO);
                            } catch (Exception e) {
                                log.error("解析文件异常：", e);
                                e.printStackTrace();
                            }
                        }
                        // 批量导入数据库
                        //blackListMapper.insertBatch(blackListDOS);
                        resultMap.put("result", "success");
                    } catch (Exception e) {
                        log.error("[op:updateDifferentNetEsData] failed to read head files = {} 失败！ SftpException=", file.getName(), e);
                    } finally {
                        bufferedReader.close();
                    }
                    //删除数据文件
                    boolean delete = dataFile.delete();
                    if (delete) {
                        log.info("删除数据文件成功！");
                    }
                }
            }
        } catch (Exception e) {
            log.error("导入黑名单数据失败", e);
            e.printStackTrace();
        } finally {
            sftp.disconnect();
        }
        return resultMap;
    }


    /**
     * 将数据库字段转换为java属性，如user_name-->userName
     *
     * @param field 字段名
     * @return
     */
    private static String fieldToProperty(String field) {
        if (null == field) {
            return "";
        }
        char[] chars = field.toCharArray();
        StringBuffer property = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') {
                int j = i + 1;
                if (j < chars.length) {
                    property.append(String.valueOf(chars[j]).toUpperCase());
                    i++;
                }
            } else {
                property.append(String.valueOf(c).toLowerCase());
            }
        }
        return property.toString();
    }


    /*黑名单管理接口*/

    /*导出黑名单*/
    @Override
    public void exportBlackListFileManage(HttpServletResponse response) throws IOException {
        try {
            //excel文件名
            String fileName = "blacklist.xls";
            //设置响应的编码格式
            response.setCharacterEncoding("UTF-8");
            //设置响应类型
            response.setContentType("application/msexcel;charset=UTF-8");
            //设置响应头
            response.setHeader("Content-Disposition",
                    "attachment;filename="+
                            new String(fileName.getBytes(),"iso8859-1"));

            Map<String, Object> maps = new HashMap<>();
            OutputStream outputStream = response.getOutputStream();
//            FileOutputStream outputStream=new FileOutputStream("d:\\blacklist.xls");
            List<BlackListDO> blackListDOList = blackListMapper.getAllBlackList();
            List<List> list = new ArrayList<>();
            for(BlackListDO blackListDO:blackListDOList){
                ArrayList<String> sublist = new ArrayList();
                sublist.add(blackListDO.getAssetPhone());
                sublist.add(blackListDO.getServiceCate());
                sublist.add(blackListDO.getMaketingCate());
                sublist.add(blackListDO.getPublicBenefitCate());
                sublist.add(blackListDO.getChannel());
                sublist.add(blackListDO.getStaffId());
                list.add(sublist);
            }
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            int  groupSize= 60000;
            List<List<List>> newlist = splitList(list,groupSize);
            for(List<List> ele: newlist){
                hssfWorkbook = writeExcel(hssfWorkbook,ele);
            }

            //用输出流写到excel
            try {
                hssfWorkbook.write(outputStream);
                outputStream.flush();
                outputStream.close();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /*导入黑名单*/
    @Override
    public Map<String, Object> importBlackListFileManage(MultipartFile multipartFile) throws IOException {
        Map<String, Object> maps = new HashMap<>();
        InputStream inputStream = multipartFile.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = wb.getSheetAt(0);
        Integer rowNums = sheet.getLastRowNum() + 1;
        List<String> resultList = new ArrayList<>();
        for (int i = 1; i < rowNums; i++) {
            Row row = sheet.getRow(i);
            if (row.getLastCellNum() < 6) {
                maps.put("resultCode", CODE_FAIL);
                maps.put("resultMsg", "请返回检查模板格式");
                return maps;
            }
            Cell assetPhoneCell = row.getCell(0);
            assetPhoneCell.setCellType(CellType.STRING);
            String assetPhone = assetPhoneCell.getStringCellValue();

            Cell serviceCateCell = row.getCell(1);
            serviceCateCell.setCellType(CellType.STRING);
            String serviceCate = serviceCateCell.getStringCellValue();

            Cell maketingCateCell = row.getCell(2);
            maketingCateCell.setCellType(CellType.STRING);
            String maketingCate = maketingCateCell.getStringCellValue();

            Cell publicBenefitCateCell = row.getCell(3);
            publicBenefitCateCell.setCellType(CellType.STRING);
            String publicBenefitCate = publicBenefitCateCell.getStringCellValue();

            Cell channelCell = row.getCell(4);
            channelCell.setCellType(CellType.STRING);
            String channel = channelCell.getStringCellValue();

            Cell staffIdCell = row.getCell(5);
            staffIdCell.setCellType(CellType.STRING);
            String staffId = staffIdCell.getStringCellValue();

            BlackListDO blackListDO = new BlackListDO();
            blackListDO.setAssetPhone(assetPhone);
            blackListDO.setServiceCate(serviceCate);
            blackListDO.setMaketingCate(maketingCate);
            blackListDO.setPublicBenefitCate(publicBenefitCate);
            blackListDO.setChannel(channel);
            blackListDO.setStaffId(staffId);
            //添加黑名单
            blackListDO.setCreateDate(new Date());
            blackListMapper.addBlackList(blackListDO);

        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", "导入文件成功");
        return maps;

    }

    /*分页获取黑名单列表*/
    @Override
    public Map<String, Object> getBlackListPageByKey(Map<String,Object> pageParams) {
        Map<String,Object> result = new HashMap<>();
        List<BlackListDO> blackListDOS = new ArrayList<>();
        String pageSize = "10";
        if(pageParams.get("pageSize").toString() != ""){
            pageSize = pageParams.get("pageSize").toString();
        }
        try {
            String orderBy = "black_id desc";
            PageHelper.startPage(Integer.parseInt(pageParams.get("page").toString()),Integer.parseInt(pageSize.toString()),orderBy);
            blackListDOS= blackListMapper.getBlackListPageByKey((String)pageParams.get("assetPhone"),(String)pageParams.get("serviceCate"),(String)pageParams.get("maketingCate"),
                    (String)pageParams.get("publicBenefitCate"),(String)pageParams.get("channel"),(String)pageParams.get("staffId"));
            result.put("resultCode",CommonConstant.CODE_SUCCESS);
            result.put("resultMsg","请求成功");
            result.put("blackList",blackListDOS);
            result.put("pageInfo",new Page(new PageInfo<>(blackListDOS)));

        }catch (Exception e){
            result.put("resultCode", CommonConstant.CODE_FAIL);
            result.put("resultMsg","请求失败");
            e.printStackTrace();
        }finally {
            return result;
        }
    }

    /**
     * 把内容写入Excel
     * @param list 传入要写的内容，此处以一个List内容为例，先把要写的内容放到一个list中
     * @param outputStream 把输出流怼到要写入的Excel上，准备往里面写数据
     */
    public static void writeExcel(List<List> list, OutputStream outputStream) {
        //创建工作簿
        HSSFWorkbook hssfWorkbook = null;
        hssfWorkbook = new HSSFWorkbook();
        //创建工作表
        HSSFSheet hssfSheet;
        hssfSheet = hssfWorkbook.createSheet();
        //创建行
        HSSFRow hssfRow;
        hssfRow = hssfSheet.createRow(0);
        hssfRow.createCell(0).setCellValue("assetPhone");
        hssfRow.createCell(1).setCellValue("serviceCate");
        hssfRow.createCell(2).setCellValue("maketingCate");
        hssfRow.createCell(3).setCellValue("publicBenefitCate");
        hssfRow.createCell(4).setCellValue("channel");
        hssfRow.createCell(5).setCellValue("staffId");

        //创建列，即单元格Cell
        HSSFCell hssfCell;

// 设置单元格编码格式

        //把List里面的数据写到excel中
        for (int i=0;i<list.size();i++) {
            //从第一行开始写入
            hssfRow = hssfSheet.createRow(i+1);
//            创建每个单元格Cell，即列的数据
            List sub_list =list.get(i);
            for (int j=0;j<sub_list.size();j++) {

                hssfCell = hssfRow.createCell(j); //创建单元格
                hssfCell.setCellValue((String)sub_list.get(j)); //设置单元格内容
            }
        }
        //用输出流写到excel
        try {
            hssfWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Map<String, Object> deleteBlackList(List<String> phoneNumsDeleted) {
        try {
            blackListMapper.deleteBlackListById(phoneNumsDeleted);

            //添加操作日志
            for(String phone: phoneNumsDeleted){
                BlackListLogDO blackListLogDO = new BlackListLogDO();
                blackListLogDO.setMethod("delete");
                blackListLogDO.setAssetPhone(phone);
                blackListLogMapper.addBlacklistlog(blackListLogDO);
            }
            return responseVO.response(SUCCESS_CODE,"删除黑名单成功");
        }catch (Exception e){
            e.printStackTrace();
            return responseVO.response(FAIL_CODE,"删除黑名单失败");
        }
    }
    private List<List<List>> splitList(List<List> list , int groupSize){
        int length = list.size();
        // 计算可以分成多少组
        int num = ( length + groupSize - 1 )/groupSize ; // TODO
        List<List<List>> newList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            // 开始位置
            int fromIndex = i * groupSize;
            // 结束位置
            int toIndex = (i+1) * groupSize < length ? ( i+1 ) * groupSize : length ;
            newList.add(list.subList(fromIndex,toIndex)) ;
        }
        return  newList ;
    }


    private HSSFWorkbook writeExcel(HSSFWorkbook hssfWorkbook, List<List> list) {
        //创建工作表
        HSSFSheet hssfSheet;
        hssfSheet = hssfWorkbook.createSheet();
        //创建行
        HSSFRow hssfRow = hssfSheet.createRow(0);
        hssfRow.createCell(0).setCellValue("资产号码（assetPhone）");
        hssfRow.createCell(1).setCellValue("服务类(serviceCate)");
        hssfRow.createCell(2).setCellValue("营销类（maketingCate）");
        hssfRow.createCell(3).setCellValue("公益类（publicBenefitCate）");
        hssfRow.createCell(4).setCellValue("渠道（channel）");
        hssfRow.createCell(5).setCellValue("员工id（staffId）");

        // 设置单元格编码格式
        //把List里面的数据写到excel中
        for (int i = 0; i < list.size(); i++) {
            //从第一行开始写入
            hssfRow = hssfSheet.createRow(i + 1);
            //创建每个单元格Cell，即列的数据
            List sub_list = list.get(i);
            for (int j = 0; j < sub_list.size(); j++) {
                HSSFCell hssfCell = hssfRow.createCell(j); //创建单元格
                hssfCell.setCellValue((String) sub_list.get(j)); //设置单元格内容
            }
        }
        return hssfWorkbook;

    }




}
