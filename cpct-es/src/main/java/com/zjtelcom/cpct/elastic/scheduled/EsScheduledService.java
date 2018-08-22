package com.zjtelcom.cpct.elastic.scheduled;

import com.zjtelcom.cpct.elastic.util.FtpUtil;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EsScheduledService {

    @Value("${ftp.address}")
    private String ftpAddress;
    @Value("${ftp.port}")
    private int ftpPort;
    @Value("${ftp.name}")
    private String ftpName;
    @Value("${ftp.password}")
    private String ftpPassword;
    @Value("${ftp.basepath}")
    private String ftpBathPath;

    /**
     * 每两个小时执行一次扫面FTP服务器并更新到Es
     */
    @Scheduled(cron = "0 0 /2 * * *")
    public void scanFtpFile() throws IOException {
        getFilenames();
    }

    //获取指定文件夹内的文件名称
    public String getFilenames() throws IOException {
        String names="";
        FtpUtil ftpUtil = new FtpUtil();
        FTPClient ftpClient = ftpUtil.connect(ftpAddress, ftpPort, ftpName, ftpPassword);
        List<String> fileList = new ArrayList<>();
        try {
            //获取具体文件路径
            if (ftpBathPath.startsWith("/") && ftpBathPath.endsWith("/")) {
                String directory = ftpBathPath;
                //更换目录到当前目录
                ftpClient.changeWorkingDirectory(directory);
                ftpClient.enterLocalPassiveMode();
                FTPFile[] files = ftpClient.listFiles();
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()){
                            String dirName = new String(files[i].getName().getBytes("gbk"),"utf-8");
                            if (dirName.indexOf("_") >-1 ) {
                                ftpClient.changeWorkingDirectory(dirName);
                                ftpClient.enterLocalPassiveMode();
                                FTPFile[] files2 = ftpClient.listFiles();
                                if (files2 != null) {
                                    for (int j = 0; j < files2.length; j++) {
                                        if (files2[j].isFile()){
                                            String fileName = new String(files2[j].getName().getBytes("gbk"),"utf-8");
                                            String filePath = directory+dirName+"/"+fileName;
                                            fileList.add(filePath);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            for (String filePath : fileList) {
                //todo 调用ES更新数据接口
                boolean res = true;
                if (res) {
                    int index = filePath.lastIndexOf("/");
                    String fileName = filePath.substring(index+1,filePath.length());
                    String pathName = filePath.substring(0,index);
                    ftpClient.changeWorkingDirectory(pathName);
                    ftpClient.deleteFile(fileName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            ftpClient.disconnect();
        }
        return names;
    }
}
