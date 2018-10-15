package com.zjtelcom.cpct.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * Description: response object
 * author: pengy
 * date: 2018/3/26 19:11
 */
@Data
public class RespInfo implements Serializable {

    private String code;
    private String msg; //失败描述
    private Object data;
    private String errorCode;
    private String remarks;//备注

    public static RespInfo build(String code, String msg, String errorCode) {
        RespInfo respInfo = new RespInfo();
        respInfo.code = code;
        respInfo.msg = msg;
        respInfo.errorCode = errorCode;
        return respInfo;
    }

    public static RespInfo build(String code, Object data) {
        RespInfo respInfo = new RespInfo();
        respInfo.code = code;
        respInfo.data = data;
        return respInfo;
    }

}
