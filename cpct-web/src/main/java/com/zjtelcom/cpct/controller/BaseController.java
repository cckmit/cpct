package com.zjtelcom.cpct.controller;

import com.zjtelcom.cpct.bean.RespInfo;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.util.FastJsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: baseController
 * author: pengy
 * date: 2018/3/26 18:53
 */
public class BaseController {

    /**
     * log object
     */
    protected Logger logger = LoggerFactory.getLogger(BaseController.class);

    /**
     * failure return
     * @param msg
     * @return
     */
    public String initFailRespInfo(String msg, String errorCode) {
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
