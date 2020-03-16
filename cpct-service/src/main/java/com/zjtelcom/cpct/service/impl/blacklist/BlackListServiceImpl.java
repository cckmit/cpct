package com.zjtelcom.cpct.service.impl.blacklist;

import com.zjtelcom.cpct.dao.blacklist.BlackListMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import com.zjtelcom.cpct.service.blacklist.BlackListService;
import com.zjtelcom.cpct.util.SftpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BlackListServiceImpl implements BlackListService {

    private final static Logger log = LoggerFactory.getLogger(BlackListServiceImpl.class);
    private final static String splitMark = "\u0007";

    private String ftpAddress = "134.108.0.93";
    private int ftpPort = 22;
    private String ftpName= "ftp";
    private String ftpPassword="V1p9*2_9%3#";
    private String excelIssurepath="/app/ftp/msc/userlist/fees";
    private String uploadExcelPath="/app/ftp/msc/userlist/fees/";



    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    public boolean exportBlackListFile() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMDD");
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
                    sline.append(blackListDO.getBlackId() + splitMark);
                    sline.append(blackListDO.getAssetPhone() + splitMark);
                    sline.append(blackListDO.getServiceCate() + splitMark);
                    sline.append(blackListDO.getMaketingCate() + splitMark);
                    sline.append(blackListDO.getPublicBenefitCate() + splitMark);
                    sline.append(blackListDO.getChannel() + splitMark);
                    sline.append(blackListDO.getStaffId() + splitMark);
                    sline.append(blackListDO.getCreateStaff() + splitMark);
                    sline.append(blackListDO.getCreateDate() + splitMark);
                    sline.append(blackListDO.getUpdateStaff() + splitMark);
                    sline.append(blackListDO.getUpdateDate() + splitMark);
                    sline.append(blackListDO.getOperType());
                    SftpUtils sftpUtils = new SftpUtils();
                    sftpUtils.writeFileContent(dataFile.getName(), sline.toString());
                }
            } catch (Exception e) {
                log.error("黑名单数据文件dataFile失败！Expection = ", e);
            }
        }
        return true;
    }


}
