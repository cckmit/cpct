package com.zjtelcom.cpct.util;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/11/13 22:40
 * @version: V1.0
 */
public class SftpUtils {
    private Logger logger = LoggerFactory.getLogger(SftpUtils.class);

    /**
     * 连接sftp服务器
     *
     * @param host     主机
     * @param port     端口
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static ChannelSftp connect(String host, int port, String username, String password) {
        ChannelSftp sftp = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            Session sshSession = jsch.getSession(username, host, port);
            System.out.println("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.setTimeout(3600000);
            sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            System.out.println("Connected to " + host + ".");
        } catch (Exception e) {
            e.printStackTrace();
            sftp = null;
        }
        return sftp;
    }

    public ChannelSftp connectSftp(Session sshSession) {
        ChannelSftp sftp = null;
        try {
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            e.printStackTrace();
            sftp = null;
        }
        return sftp;
    }

    public void exec(Session sshSession, String command) {
        ChannelExec channelExec = null;
        try {
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
            Channel channel = sshSession.openChannel("exec");
            channelExec = (ChannelExec) channel;
            channelExec.setCommand(command);
            channelExec.connect();
        } catch (Exception e) {
            e.printStackTrace();
            channelExec = null;
        } finally {
            channelExec.disconnect();
        }
    }

    public Session getSession(String host, int port, String username, String password) {
        Session sshSession = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            System.out.println("Session created.");
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            System.out.println("Session connected.");
            System.out.println("Opening Channel.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sshSession;
    }

    /**
     * 创建目录
     *
     * @param directory 要创建的目录 位置
     * @param dir       要创建的目录
     */
    public boolean createDir(String directory, String dir, ChannelSftp sftp) throws Exception {
        try {
            if (isDirExist(directory, sftp)) {
                System.out.println("进入目录：" + directory);
                sftp.cd(directory);
                return true;
            }
            String pathArry[] = directory.split("/");
            StringBuffer filePath = new StringBuffer("/");
            for (String path : pathArry) {
                if (path.equals("")) {
                    continue;
                }
                filePath.append(path + "/");
                if (isDirExist(filePath.toString(), sftp)) {
                    sftp.cd(filePath.toString());
                } else {
                    // 建立目录
                    sftp.mkdir(filePath.toString());
                    // 进入并设置为当前目录
                    sftp.cd(filePath.toString());
                }
            }
            sftp.cd(directory);
            return true;
        } catch (SftpException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断目录是否存在
     *
     * @param directory
     * @return
     */
    public boolean isDirExist(String directory, ChannelSftp sftp) {
        boolean isDirExistFlag = false;
        try {
            SftpATTRS sftpATTRS = sftp.lstat(directory);
            List<String> fileList = listFiles(directory, sftp);
            isDirExistFlag = true;
            return sftpATTRS.isDir();
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().equals("no such dataFile")) {
                isDirExistFlag = false;
            }
        }
        return isDirExistFlag;
    }


    /**
     * 上传文件
     *
     * @param directory  上传的目录
     * @param uploadFile 要上传的文件
     * @param sftp
     */
    public boolean upload(String directory, String uploadFile, ChannelSftp sftp) {
        boolean result = false;
        try {
            if (!directory.equals("")) {
                sftp.cd(directory);
            }
            File file = new File(uploadFile);
            sftp.put(new FileInputStream(file), file.getName());
            System.out.println("上传完成");
            sftp.disconnect();
            sftp.getSession().disconnect();
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
            sftp.disconnect();
            logger.error("[op:SftpUtils] upload 失败！Exception = ", e);
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e1) {
                logger.error("[op:SftpUtils] upload disconnect 失败！Exception = ", e);
            }
            result = false;
        }
        return result;
    }

    /**
     * 下载文件
     *
     * @param directory    下载目录
     * @param downloadFile 下载的文件
     * @param saveFile     存在本地的路径
     * @param sftp
     */
/*
    public void download(String directory, String downloadFile, String saveFile, ChannelSftp sftp) {
        try {
            sftp.cd(directory);
            File dataFile = new File(saveFile);
            sftp.get(downloadFile, new FileOutputStream(dataFile));
            sftp.disconnect();
            sftp.getSession().disconnect();
            System.out.println("download ok,session closed");
        } catch (Exception e) {
            sftp.disconnect();
            logger.error("[op:SftpUtils] download 失败！Exception = ", e);
            try {
                sftp.getSession().disconnect();
            } catch (JSchException e1) {
                logger.error("[op:SftpUtils] download disconnect 失败！Exception = ", e);
            }
            e.printStackTrace();
        }
    }
*/


    /**
     * sftp 下载
     */
    public boolean download(ChannelSftp sftp, String filePath, String fileName, String targetPath) {
        try {
            String dst = targetPath + fileName;
            String src = filePath + fileName;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss");
            logger.info("开始下载，sftp服务器路径：[" + src + "]目标服务器路径：[" + dst + "]");
            //logger.info("开始时间：" + simpleDateFormat.format(new Date()));
            sftp.get(src, dst);
            logger.info("下载成功");
            //logger.info("结束时间：" + simpleDateFormat.format(new Date()));
            return true;
        } catch (Exception e) {
            logger.error("下载失败", e);
            return false;
        }
    }


    public List<String> read(String directory, String readFile, ChannelSftp sftp, String charSetName) {
        List<String> stringlist = new ArrayList<>();
        InputStream inputStream = null;
        try {
            sftp.cd(directory);
            inputStream = sftp.get(readFile);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charSetName));
            String line = null;
            line = br.readLine();
            while (line != null) {
                stringlist.add(line);
                line = br.readLine(); // 一次读入一行数据
            }
            br.close();
        } catch (SftpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringlist;
    }

    /**
     * 删除文件
     *
     * @param directory  要删除文件所在目录
     * @param deleteFile 要删除的文件
     * @param sftp
     */
    public void delete(String directory, String deleteFile, ChannelSftp sftp) {
        try {
            if (directory != null) {
                sftp.cd(directory);
            }
            sftp.rm(deleteFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String cd(String directory, ChannelSftp sftp) {
        String path = directory;
        try {
            sftp.cd(directory);
            path = sftp.pwd();
            return path;
        } catch (Exception e) {
            logger.error("[op:sftpUtils] cd 失败！Exception = ", e);
        }
        return path;
    }


    /**
     * 列出目录下的文件
     *
     * @param directory 要列出的目录
     * @param sftp
     * @return
     * @throws SftpException
     */
    public List<String> listFiles(String directory, ChannelSftp sftp) throws SftpException {
        Vector<ChannelSftp.LsEntry> vector = sftp.ls(directory);
        List<String> fileList = new ArrayList<>();
        for (ChannelSftp.LsEntry entry : vector) {
            fileList.add(entry.getFilename());
        }
        return fileList;
    }

    /**
     * 将输入流的数据上传到sftp作为文件
     *
     * @param directory    上传到该目录
     * @param sftpFileName sftp端文件名
     * @param input        输入流
     * @throws SftpException
     * @throws Exception
     */
    public boolean upload(String directory, String sftpFileName, InputStream input, ChannelSftp sftp) throws SftpException, JSchException {
        boolean result = false;
     /*   try {
            sftp.cd(directory);
        } catch (SftpException e) {
            logger.warn("directory is not exist");
            sftp.mkdir(directory);
            sftp.cd(directory);
            result = false;
        }*/
        try {
            sftp.put(input, sftpFileName);
            result = true;
        } catch (SftpException e) {

            logger.error("[op:upload] 上传文件sftpFileName = {}失败！SftpException = ", sftpFileName , e);
        }finally {
            try {
                input.close();
            }catch (IOException e ){
                e.printStackTrace();
            }
        }
        if (sftp.isConnected()) {
            sftp.disconnect();
        }
        if (sftp.getSession().isConnected()) {
            sftp.getSession().disconnect();
        }
        sftp.quit();
        logger.info("dataFile:{" + sftpFileName + "} is upload successful");
        return result;
    }

    /**
     * 将输入流的数据上传到sftp作为文件
     *
     * @param directory    上传到该目录
     * @param sftpFileName sftp端文件名
     * @param input        输入流
     * @throws SftpException
     * @throws Exception
     */
    public boolean uploadFile(String directory, String sftpFileName, InputStream input, ChannelSftp sftp) throws SftpException, JSchException {
        boolean result = false;
        try {
            sftp.put(input, sftpFileName);
            result = true;
        } catch (SftpException e) {

            logger.error("[op:upload] 上传文件sftpFileName = {}失败！SftpException = ", sftpFileName , e);
        }finally {
            try {
                input.close();
            }catch (IOException e ){
                e.printStackTrace();
            }
        }
        logger.info("dataFile:{" + sftpFileName + "} is upload successful");
        return result;
    }

    /**
     * 读取文件为流的形式
     *
     * @param fileName
     * @param sftp
     * @return
     */
    public InputStream getFileStream(String fileName, ChannelSftp sftp) {
        InputStream inputStream = null;
        try {
            String path = sftp.pwd();
            inputStream = sftp.get(fileName);
        } catch (SftpException e) {
            logger.error("[op:getFileStream] 获取文件fileName = {}失败！SftpException = ", fileName, e);
        }
        return inputStream;

    }

    /**
     * 当前工作目录
     *
     * @return String
     */
    public String currentDir(ChannelSftp sftp) {
        try {
            return sftp.pwd();
        } catch (Exception e) {
            logger.error("failed to get current dir", e);
            return homeDir(sftp);
        }
    }

    /**
     * @param sftp
     * @param sourcePath 源路径+文件名
     * @param targetPath 目标路径+文件名
     * @return
     */
    public boolean move(ChannelSftp sftp, String sourcePath, String targetPath) {
        boolean result = false;
        try {
            sftp.rename(sourcePath, targetPath);
            result = true;
        } catch (SftpException e) {
            logger.error("[op:SftpUtils move] move sourcePath ={}, targetPath = {} 失败！SftpException = ", sourcePath, targetPath, e);
        }
        return result;
    }


    /**
     * @param sftp
     * @param filePath 路径/文件名
     * @return
     */
    public boolean remove(ChannelSftp sftp, String filePath) {
        boolean result = false;
        try {
            sftp.rm(filePath);
            result = true;
        } catch (SftpException e) {
            logger.error("[op:SftpUtils remove] remmove filePath = {}不存在，删除失败！SftpException = ", filePath, e);
        }
        return result;
    }

    /**
     * 根目录
     *
     * @return String
     */
    private String homeDir(ChannelSftp sftp) {
        try {
            return sftp.getHome();
        } catch (Exception e) {
            return "/";
        }
    }

    /**
     * 切换到上一级目录
     *
     * @return boolean
     */
    public boolean changeToParentDir(ChannelSftp sftp) {
        boolean result = false;
        try {
            sftp.cd("..");
            result = true;
        } catch (SftpException e) {
            result = false;
            logger.error("[op:changeToParentDir] changeToParentDir 失败！SftpException = ", e);
        }
        return result;
    }

    /**
     * 切换工作目录
     *
     * @param pathName 路径
     * @return boolean
     */
    public boolean changeDir(String pathName, ChannelSftp sftp) {
        if (pathName == null || pathName.trim().equals("")) {
            logger.debug("invalid pathName");
            return false;
        }
        try {
            sftp.cd(pathName);
            logger.debug("directory successfully changed,current dir=" + sftp.pwd());
            return true;
        } catch (SftpException e) {
            logger.error("failed to change directory", e);
            return false;
        }
    }


    /**
     * @param file 需要下载的文件(文件过大会内存溢出)
     * @param sftp
     * @return 直接返回文件内容
     * @throws SftpException
     * @throws IOException
     */
    public String downLoadByte(String file, ChannelSftp sftp) throws SftpException, IOException {
        InputStream inputStream = sftp.get(file);
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        System.out.println("result.size() = " + result.size() / 1048576 / 150 + 1);
        String str = result.toString(StandardCharsets.UTF_8.name());
        return str;
    }


    /**
     * 测试
     */
    public static void main(String[] args) {
        try {
            ChannelSftp sftp = connect("134.108.0.93", 22, "ftp", "ftp_123");
    /*        String fileName = "ASSET_INC_A_20181114234502.flg";
            String tarPath = "/app/cpcp_cxzx/es/increment/hangzhou_bak/";
            sftp.rm(tarPath + "/" + fileName);*/

            String localTarPath = "F:\\workspace\\elasticsearch\\elasticsearch_services\\target\\classes\\tempfile\\hangzhou\\ASSET_MAIN_INC_A_20181119033141.dat";
            delFile(localTarPath);
            System.out.println("end...");
        } catch (Exception e) {
            System.out.println("Exception = " + e);
        }
    }

    public static boolean delFile(String path) {
        Boolean bool = false;
        File file = new File(path);
        try {
            if (file.exists()) {
                file.delete();
                bool = true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return bool;
    }



    /**
     * 向文件中写入内容
     *
     * @param filepath 文件路径与名称
     * @param newstr   写入的内容
     * @return
     * @throws IOException
     */
    public boolean writeFileContent(String filepath, String newstr) throws IOException {
        Boolean bool = false;
        String filein = newstr + "\r\n";//新写入的行，换行
        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            File file = new File(filepath);//文件路径(包括文件名称)
            //将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buffer = new StringBuffer();

            //文件原有内容
            for (int i = 0; (temp = br.readLine()) != null; i++) {
                buffer.append(temp);
                // 行与行之间的分隔符 相当于“\n”
                buffer = buffer.append(System.getProperty("line.separator"));
            }
            buffer.append(filein);
            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buffer.toString().toCharArray());
            pw.flush();
            bool = true;
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("文件输入异常！， Expection = ", e);
            e.printStackTrace();
        } finally {
            //不要忘记关闭
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
        return bool;
    }


}