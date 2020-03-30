package com.zjtelcom.cpct.service.impl.blacklist;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.ChannelSftp;
import com.zjtelcom.cpct.dao.blacklist.BlackListMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import com.zjtelcom.cpct.service.blacklist.BlackListCpctService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.SftpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class BlackListServiceCpctImpl implements BlackListCpctService {

    private final static Logger log = LoggerFactory.getLogger(BlackListServiceCpctImpl.class);
    private final static String splitMark = "\u0007";
    private final static String superfield = "createStaff,updateStaff,createDate,updateDate";

    private String ftpAddress = "134.108.0.92";
    private int ftpPort = 22;
    private String ftpName = "ftp";
    private String ftpPassword = "V1p9*2_9%3#";
    private String exportPath = "/app/cpcp_cxzx/black_list_export/";
    private String importPath = "/app/cpcp_cxzx/black_list_import/";
    private String localHeadFilePath = this.getClass().getResource("/").getPath();

    @Autowired
    private BlackListMapper blackListMapper;

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
        if (!dataFile.exists()) {
            // 如果文件不存在，则创建新的文件
            try {
                dataFile.createNewFile();
                //创建策略定义文件
                // 从数据库查询获取黑名单数据
                List<BlackListDO> allBlackList = blackListMapper.getAllBlackList();
                for (BlackListDO blackListDO : allBlackList) {
                    StringBuilder sline = new StringBuilder();
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
                    sftpUtils.writeFileContent(dataFile.getName(), sline.toString());
                }
                log.info("sftp已获得连接");
                sftpUtils.cd(exportPath, sftp);
                boolean uploadResult = sftpUtils.uploadFile(exportPath, dataFile.getName(), new FileInputStream(dataFile), sftp);
                if (uploadResult) {
                    log.info("上传成功，开始删除本地文件！");
                    boolean b1 = dataFile.delete();
                    if (b1) {
                        log.info("删除本地文件成功！");
                    }
                }
                sftp.disconnect();
                resultMap.put("resultMsg", "success");
            } catch (Exception e) {
                log.error("黑名单数据文件dataFile失败！Expection = ", e);
                resultMap.put("resultMsg", "faile");
            } finally {
                sftp.disconnect();
            }
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
            File headFolders = new File(this.getClass().getResource("/").getPath());
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

}
