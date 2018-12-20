package com.zjtelcom.cpct.openConfig;

import com.zjtelcom.cpct.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Auther: anson
 * @Date: 2018/11/5
 * @Description:全局异常处理,先不启用
 */
@RestControllerAdvice
public class GlobalExceptionConfig {

//    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionConfig.class);
//
//
//    @ExceptionHandler(SystemException.class)
//    public String handleSystemException(Exception e){
//        logger.error(e.getMessage(), e);
//        logger.info("业务异常");
//        return e.getMessage();
//    }
//
//
//    /**
//     * 处理 Exception 异常
//     */
//    @ExceptionHandler(Exception.class)
//    public String handleException(Exception e){
//        logger.error(e.getMessage(), e);
//        logger.info("系统异常");
//        return "";
//    }


}
