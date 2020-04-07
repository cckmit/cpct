package com.zjtelcom.cpct.open.base.controller;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
}
