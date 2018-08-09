/*
 * 文件名：ResultUtil.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月30日
 * 修改内容：
 */

package com.zjtelcom.cpct.util;


import com.zjtelcom.cpct.constants.ResponseCode;
import com.zjtelcom.cpct.dto.pojo.Result;
import com.zjtelcom.cpct.exception.ServicesException;
import com.zjtelcom.cpct.exception.ValidateException;
import org.apache.commons.lang.StringUtils;

import java.sql.SQLException;


/**
 * 服务结果处理工具类
 * @author taowenwu
 * @version 1.0
 * @see ResultUtil
 * @since JDK1.7
 */

public final class ResultUtil {

    private ResultUtil() {}

    public static Result buildSuccessResult() {
        return new Result(ResponseCode.SUCCESS, ResponseCode.SUCCESS_MSG);
    }

    public static Result buildSuccessResult(Object resultObject) {
        return new Result(ResponseCode.SUCCESS, ResponseCode.SUCCESS_MSG, resultObject);
    }

    public static Result buildErrorResult(Exception exception, Object resultObject) {

        if (exception instanceof SQLException) {
            return new Result(ResponseCode.DATABASE_ERROR, ResponseCode.DATABASE_ERROR_MSG,
                resultObject);
        }

        String msg = null;
        if (exception instanceof ValidateException) {

            if (StringUtils.isEmpty(exception.getMessage())) {
                msg = ResponseCode.VALIDATE_ERROR_MSG;
            }
            return new Result(ResponseCode.VALIDATE_ERROR, msg, resultObject);
        }

        if (exception instanceof ServicesException) {
            if (StringUtils.isEmpty(exception.getMessage())) {
                msg = ResponseCode.VALIDATE_ERROR_MSG;
            }
            return new Result(ResponseCode.SERVICE_ERROR, msg, resultObject);
        }
        return new Result(ResponseCode.INTERNAL_ERROR, ResponseCode.INTERNAL_ERROR_MSG,
            resultObject);
    }
}
