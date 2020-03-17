package com.zjtelcom.cpct.service.impl.blacklist;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import com.zjtelcom.cpct.dao.blacklist.BlackListMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import com.zjtelcom.cpct.service.blacklist.BlackListService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.SftpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class BlackListServiceImpl implements BlackListService {

    private final static Logger log = LoggerFactory.getLogger(BlackListServiceImpl.class);
    private final static String splitMark = "\u0007";
    private final static String superfield = "createStaff,updateStaff,createDate,updateDate";

    private String ftpAddress = "134.108.0.92";
    private int ftpPort = 22;
    private String ftpName = "ftp";
    private String ftpPassword = "V1p9*2_9%3#";
    private String exportPath = "/app/cpcp_cxzx/black_list_export/";
    private String importPath = "/app/cpcp_cxzx/black_list_import/";

    @Autowired
    private BlackListMapper blackListMapper;

    /**
     *
     * 从数据库导出黑名单上传ftp服务器
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
                    SftpUtils sftpUtils = new SftpUtils();
                    sftpUtils.writeFileContent(dataFile.getName(), sline.toString());
                }

                SftpUtils sftpUtils = new SftpUtils();
                final ChannelSftp sftp = sftpUtils.connect(ftpAddress, ftpPort, ftpName, ftpPassword);
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
                resultMap.put("resultMsg", "success");
            } catch (Exception e) {
                log.error("黑名单数据文件dataFile失败！Expection = ", e);
                resultMap.put("resultMsg", "faile");
            }
        }
        return resultMap;
    }

    /**
     *
     * 从ftp服务器上下载文件，解析并导入到数据库中
     * @return
     */
    @Override
    public Map<String, Object> importBlackListFile() {
        Map<String, Object> resultMap = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            resultMap = new HashMap<>();
            SftpUtils sftpUtils = new SftpUtils();
            final ChannelSftp sftp = sftpUtils.connect(ftpAddress, ftpPort, ftpName, ftpPassword);
            // 下载文件
            String path = sftpUtils.cd(importPath, sftp);
            List<String> files = sftpUtils.listFiles(path, sftp);
            for (String fileName : files) {
                if (!"..".equals(fileName) && !".".equals(fileName)  && fileName.toUpperCase().contains("HEAD") && !fileName.toUpperCase().endsWith(".FLG")) {
                        // 下载文件到本地
                        File file = new File(fileName);
                        if (!file.exists()) {
                            log.info("开始下载head文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                            sftpUtils.download(sftp, path + "/", fileName, this.getClass().getResource("/").getPath());
                            log.info("结束下载head文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                        }
                    }

                if (!"..".equals(fileName) && !".".equals(fileName) && !fileName.toUpperCase().contains("HEAD") && !fileName.toUpperCase().endsWith(".FLG")) {
                        // 下载文件到本地
                        File file = new File(fileName);
                        if (!file.exists()) {
                            log.info("开始下载文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                            sftpUtils.download(sftp, path + "/", fileName, this.getClass().getResource("/").getPath());
                            log.info("结束下载文件--->>>" + fileName + "****** 时间：" + simpleDateFormat.format(new Date()));
                        }
                    }
            }

            // 解析头文件

        } catch (Exception e) {
            log.error("下载文件失败！", e);
        }
        return resultMap;
    }

}
