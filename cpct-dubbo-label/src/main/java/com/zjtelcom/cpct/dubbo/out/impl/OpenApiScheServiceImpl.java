package com.zjtelcom.cpct.dubbo.out.impl;

import com.alibaba.fastjson.JSON;
import com.jcraft.jsch.ChannelSftp;
import com.zjtelcom.cpct.dao.campaign.MktCampaignMapper;
import com.zjtelcom.cpct.domain.blacklist.BlackListDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.campaign.OpenCampaignScheEntity;
import com.zjtelcom.cpct.dubbo.out.OpenApiScheService;
import com.zjtelcom.cpct.service.campaign.OpenCampaignScheService;
import com.zjtelcom.cpct.service.impl.blacklist.BlackListCpctServiceImpl;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.SftpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OpenApiScheServiceImpl implements OpenApiScheService {

    private final static Logger log = LoggerFactory.getLogger(OpenApiScheServiceImpl.class);

    //600104 |user m600104 |passwd  Ftp_600104@2020 |path /jtppm/zc_mkt_campaign/600104
    private String ftpAddress = "10.128.28.3";
    private int ftpPort = 21;
    private String ftpName = "m600104";
    private String ftpPassword = "Ftp_600104@2020";
    private String exportPath = "/jtppm/zc_mkt_campaign/600104";
    @Autowired
     private  OpenCampaignScheService openCampaignScheService;
    @Autowired
    private MktCampaignMapper campaignMapper;


    @Override
    public Map<String, Object> openCampaignScheForDay() {
        Map<String ,Object> result = new HashMap<>();
        Date start = DateUtil.getStartTime();
        Date end = DateUtil.getnowEndTime();
        System.out.println("start"+DateUtil.date2StringDate(start));
        System.out.println("end"+DateUtil.date2StringDate(end));
        List<MktCampaignDO> campaignDOS = campaignMapper.listByCreateDate(start, end);
        for (int i = 0 ; i< campaignDOS.size();i++){
            try {
                Map<String ,Object> dataMap = openCampaignScheService.openCampaignScheForDay(campaignDOS.get(i).getMktCampaignId());
                OpenCampaignScheEntity campaign = new OpenCampaignScheEntity();
                if (dataMap.get("code").toString().equals("200")){
                    campaign = (OpenCampaignScheEntity) dataMap.get("data");
                    String num = getNum(i);
                    exportFile(campaign,"A",num);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> openCampaignScheForMonth() {
        Integer lastMonth = DateUtil.getLastMonth();
        Date firstDayOfMonth = DateUtil.getFirstDayOfMonth(lastMonth);
        Date lastDayOfMonth = DateUtil.getLastDayOfMonth(lastMonth);
        System.out.println("start"+DateUtil.date2StringDate(firstDayOfMonth));
        System.out.println("end"+DateUtil.date2StringDate(lastDayOfMonth));

        List<MktCampaignDO> campaignDOS = campaignMapper.listByCreateDate(firstDayOfMonth, lastDayOfMonth);
        Map<String ,Object> result = new HashMap<>();
        for (int i = 0 ; i< campaignDOS.size();i++){
            try {
                Map<String ,Object> dataMap = openCampaignScheService.openCampaignScheForDay(campaignDOS.get(i).getMktCampaignId());
                OpenCampaignScheEntity campaign = new OpenCampaignScheEntity();
                if (dataMap.get("code").toString().equals("200")){
                    campaign = (OpenCampaignScheEntity) dataMap.get("data");
                    String num = getNum(i);
                    exportFile(campaign,"F",num);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String  getNum(int num) {
        num = num+1;
        String i = "";
        if (num < 10) {
            i = "00" + num;
        } else if (num >= 100) {
            i = String.valueOf(num);
        } else {
            i = "0" + num;
        }
        return i;
    }

    /**
     * 文件名定义：
     * 文件的前10位是发起方系统编码:6001040005
     * 第11到20位是落地方系统编码:1000000038
     * 第21到28位，填写当前业务功能编码:BUS63001
     * @param campaign
     * @return
     */
    private Map<String, Object> exportFile(OpenCampaignScheEntity campaign,String flg,String intNum) {
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYYMMdd");
        String Date = dateFormat.format(new Date());
        String dataFileName = "6001040005"+"1000000038"+ "BUS63001"+ Date + flg + intNum + ".txt";     //文件路径+名称+文件类型
        File dataFile = new File(dataFileName);
        SftpUtils sftpUtils = new SftpUtils();
        final ChannelSftp sftp = sftpUtils.connect(ftpAddress, ftpPort, ftpName, ftpPassword);
        boolean uploadResult = false;
        if (!dataFile.exists()) {
            // 如果文件不存在，则创建新的文件
            try {
                dataFile.createNewFile();
                sftpUtils.writeFileContent(dataFile.getName(), JSON.toJSONString(campaign));
                log.info("sftp已获得连接");
                sftpUtils.cd(exportPath, sftp);
                uploadResult = sftpUtils.uploadFile(exportPath, dataFile.getName(), new FileInputStream(dataFile), sftp);
                sftp.disconnect();
                resultMap.put("resultMsg", "success");
            } catch (Exception e) {
                log.error("活动文件上传失败！Expection = ", e);
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
}
