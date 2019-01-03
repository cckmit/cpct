package com.zjtelcom.cpct.util;

import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;


/**
 * MD5工具类.
 * 
 * @CreateDate: 2016-8-31 下午11:51:29
 * @version: V 1.0
 */
public class MD5Util {
    /**
     * 对字符串进行MD5进行加密
     * @param str
     * @return
     * @CreateDate: 2016-6-3 下午2:43:42
     */
    public static String md5Encode(String str){
        return md5Encode(str, defaultSalt);
    }
    
    /**
     * 对字符串进行MD5进行加密,并设置混淆码。防止破解。
     * @param str
     * @param salt
     * @return
     * @CreateDate: 2016-8-31 下午11:56:50
     */
    public static String md5Encode(String str, String salt) {
        String saltedPass = mergeStrAndSalt(str, salt, false);
        byte[] digest;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            digest = md5.digest(saltedPass.getBytes("UTF-8"));
        } 
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported!");
        }
        catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm [MD5]");
        }
        return new String(Hex.encodeHex(digest));
    }

    /**
     * 对密码进行MD5进行加密,并设置混淆码。防止破解
     * @param rawPass
     * @param salt
     * @return
     * @CreateDate: 2016-9-1 上午12:14:27
     */
    public static String encodePassword(String rawPass, String salt) {
        return md5Encode(rawPass, salt);
    }
    
    /**
     * 对密码进行MD5进行加密
     * @param rawPass
     * @return
     * @CreateDate: 2016-8-31 下午11:53:03
     */
    public static String encodePassword(String rawPass) {
        return md5Encode(rawPass, defaultSalt);
    }
    
    /**
     * 随机生成自定义长度位数字字母组合
     * @param length
     * @return
     * @CreateDate: 2016-9-19 下午8:56:53
     */
    public static String getRandomString(int length){
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for(int i = 0; i<length ;i++){
            // 设置生成字母还是数字规则
            int choice = random.nextInt(2) % 2;
            if(choice == 0){
                // 设置大小写字母生成规则
                choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                sb.append((char)(choice+random.nextInt(26)));
            }
            else{
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }
    
    /**
     * 用混淆码混淆字符串
     * @param str 原字符串
     * @param salt 混淆码
     * @param strict 是否严格
     * @return
     * @CreateDate: 2016-9-1 上午12:02:11
     */
    protected static String mergeStrAndSalt(String str, Object salt, boolean strict) {
        if (str == null) {
            str = "";
        }
        //如果 strict 设置为 true 严格模式，则混淆码不能以  { 或 } 结尾
        if (strict && (salt != null)) {
            if ((salt.toString().lastIndexOf("{") != -1)
                    || (salt.toString().lastIndexOf("}") != -1)) {
                throw new IllegalArgumentException(
                        "Cannot use { or } in salt.toString()");
            }
        }
        if ((salt == null) || "".equals(salt)) {
            return str;
        } 
        else {
            return str + "{" + salt.toString() + "}";
        }
    }

    /**
     * 混淆码。防止破解。
     */
    private static String defaultSalt;

    /**
     * 获得混淆码
     * 
     * @return
     */
    public static String getDefaultSalt() {
        return defaultSalt;
    }

    /**
     * 设置混淆码
     * 
     * @param defaultSalt
     */
    public static void setDefaultSalt(String defaultSalt) {
        MD5Util.defaultSalt = defaultSalt;
    }
    

    
    
    
   
    
}
