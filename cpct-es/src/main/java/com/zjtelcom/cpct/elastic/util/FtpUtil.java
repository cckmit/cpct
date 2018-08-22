package com.zjtelcom.cpct.elastic.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

public class FtpUtil {

    /**
     * FTP上传文件
     *
     * @param ftpAddress  ftp地址
     * @param ftpPort     ftp端口号
     * @param ftpName     ftp用户名
     * @param ftpPassWord ftp密码
     * @param ftpBasePath ftp主目录
     * @param filename    在ftp保存的文件名
     * @param input       待上传文件输入流
     * @return 操作结果
     */
    public boolean uploadFile(
            String ftpAddress,
            int ftpPort,
            String ftpName,
            String ftpPassWord,
            String ftpBasePath, //上传到FTP服务器上的文件夹
            String filename, //上传到FTP服务器上的文件名
            InputStream input // 输入流
    ) {
        boolean flag = false;
        FTPClient ftp = null;
        try {
            ftp = connect(ftpAddress, ftpPort, ftpName, ftpPassWord);
            if (ftp != null) {
                ftp.setControlEncoding("UTF-8"); // 中文支持
                // 改为被动模式
                ftp.enterLocalPassiveMode();
                // 改为主动模式
//                ftp.enterLocalActiveMode();
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftp.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
                ftp.setBufferSize(1024);
                System.out.println("ftpBasePath: " + ftpBasePath);
                System.out.println("ftp.pwd();: " + ftp.pwd());
                boolean change = ftp.changeWorkingDirectory(ftpBasePath);
                System.out.println("change: " + change);
                flag = ftp.storeFile(new String(filename.getBytes("GBK"), "ISO-8859-1"), input);
                input.close();
                ftp.logout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return flag;
    }

    /**
     * FTP下载文件
     *
     * @param ftpAddress  ftp地址
     * @param ftpPort     ftp端口号
     * @param ftpName     ftp用户名
     * @param ftpPassWord ftp密码
     * @param ftpBasePath ftp主目录
     * @param fileName    文件名
     * @param localPath   本地文件夹
     * @return 操作结果
     */
    public boolean downloadFile(
            String ftpAddress,
            int ftpPort,
            String ftpName,
            String ftpPassWord,
            String ftpBasePath,
            String fileName,
            String localPath
    ) {

        boolean flag = false;
        FTPClient ftp = null;
        try {
            ftp = connect(ftpAddress, ftpPort, ftpName, ftpPassWord);
            if (ftp != null) {

//                ftp.setControlEncoding("UTF-8"); // 中文支持
                ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
                // 改为被动模式
                ftp.enterLocalPassiveMode();
                // 改为主动模式
//                ftp.enterLocalActiveMode();
                ftp.changeWorkingDirectory(ftpBasePath);

                FTPFile[] list = ftp.listFiles(); // 得到文件列表 list
                OutputStream out = null;
                for (FTPFile ff : list) {
                    System.out.println("file: " + ff.getName());
                    if (ff.getName().equals(fileName)) {
                        File localFile = new File(localPath + fileName);
                        out = new FileOutputStream(localFile);
                        flag = ftp.retrieveFile(fileName, out);
                    }
                }
                out.close();
                ftp.logout();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return flag;
    }

    /**
     * 建立ftp连接
     *
     * @param ftpAddress  ftp地址
     * @param ftpPort     ftp端口号
     * @param ftpName     ftp用户名
     * @param ftpPassWord ftp密码
     * @return ftp连接对象
     * @throws IOException 失败异常
     */
    public FTPClient connect(
            String ftpAddress,
            int ftpPort,
            String ftpName,
            String ftpPassWord
    ) throws IOException {

        FTPClient ftp = new FTPClient();
        boolean flag = true;
        int reply;
        ftp.connect(ftpAddress, ftpPort);
        // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
        ftp.login(ftpName, ftpPassWord);// 登录
        reply = ftp.getReplyCode();
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            System.out.println("FTP服务器 拒绝连接");
            flag = false;
        }
        return ftp;
    }

    /**
     * 在服务器上创建一个文件夹
     *
     * @param dir 文件夹名称，不能含有特殊字符，如 \ 、/ 、: 、* 、?、 "、 <、>...
     */
    public static boolean makeDirectory(FTPClient ftp, String dir) {
        boolean flag = true;
        try {
            // System.out.println("dir=======" dir);
            flag = ftp.makeDirectory(dir);
            if (flag) {
                System.out.println("make Directory " + dir + " succeed");
            } else {
                System.out.println("make Directory " + dir + " false");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }
}
