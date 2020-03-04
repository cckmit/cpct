package com.zjtelcom.cpct.open.base.controller;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * @Auther: anson
 * @Date: 2018/10/26
 * @Description:
 */
public class BaseController {

    /**
     * log object
     */
    protected Logger logger = LoggerFactory.getLogger(com.zjtelcom.cpct.open.base.controller.BaseController.class);

    /**
     * failure return
     * @param msg
     * @return
     */
    public static String initFailRespInfo(String msg, String errorCode) {
        return FastJsonUtils.objToJson(RespInfo.build(CommonConstant.CODE_FAIL,msg,errorCode));
    }

    /**
     * success return(hava data)
     * @param data
     * @return
     */
    public String initSuccRespInfo(Object data) {
        return FastJsonUtils.objToJson(RespInfo.build(CommonConstant.CODE_SUCCESS,data));
    }


    @Test
    public void test1111() {
        Object newObj = null;
        String s17 = "%C2%AC%C3%AD%00%05t%00%C2%9F%5B%7B%22labelCode%22%3A%22PROM_AGREE_EXP_TYPE%22%2C%22labelDataType%22%3A%221300%22%2C%22labelName%22%3A%22%C3%A5%C2%8D%C2%8F%C3%A8%C2%AE%C2%AE%C3%A5%C2%88%C2%B0%C3%A6%C2%9C%C2%9F%C3%A6%C2%97%C2%B6%C3%A9%C2%95%C2%BF%C3%AF%C2%BC%C2%88%C3%A6%C2%9C%C2%88%C3%AF%C2%BC%C2%89%22%2C%22operType%22%3A%225000%22%2C%22rightOperand%22%3A%222000%22%2C%22rightParam%22%3A%226%22%7D%5D";
        try {
            if(s17 != null) {
                String redStr = java.net.URLDecoder.decode(s17, "UTF-8");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                newObj = objectInputStream.readObject();
                System.out.println(newObj.toString());
               /* String[] split = newObj.toString().split(",");
                List<String> list = Arrays.asList(split);
                System.out.println(!list.contains(String.valueOf(33823L)));*/
                objectInputStream.close();
                byteArrayInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2222() {
        Long aaaaa = 1024L;
        System.out.println(1024L == aaaaa);
    }
}
