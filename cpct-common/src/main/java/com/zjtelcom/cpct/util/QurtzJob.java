package com.zjtelcom.cpct.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Auther: anson
 * @Date: 2018/10/10
 * @Description:定时任务
 */
@Component
public class QurtzJob {

//    @Scheduled(cron="0/30 * * * * ?")
//    public void job(){
//        System.out.println("每30秒执行一次");
//    }


    /**
     * 下载营销组织数dat文件  两分钟执行一次
     * @Scheduled(cron="0 0 2 * * ?")  每天凌晨2点执行
     */
//    @Scheduled(cron="0 */2 * * * ?")
//    public void downLoadOrgTree() throws IOException {
//        System.out.println("ftp下载文件");
//        FtpUtils ftp =new FtpUtils("127.0.0.1",21,"Administrator","18086518450");
//        ftp.downloadFile("D://ftp", "my.cnf", "D://tts9//");
//        System.out.println("下载完毕");
//    }



}
