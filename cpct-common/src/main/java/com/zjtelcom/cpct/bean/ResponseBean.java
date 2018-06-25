package com.zjtelcom.cpct.bean;

import lombok.Data;

/**
 * @Author:HuangHua
 * @Descirption: 服务接口返回bean
 * @Date: Created by huanghua on 2018/6/5.
 * @Modified By:
 */
@Data
public class ResponseBean {

    /**
     * 状态码:0为成功,-1为失败
     **/
    private int resultCode;

    /**
     * 返回信息
     **/
    private String resultMsg;

    /**
     * 返回的数据
     **/
    private Object resultObject;

    /**
     * 默认构造函数
     **/
    public ResponseBean(int resultCode, String resultMsg, Object resultObject) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.resultObject = resultObject;
    }

}
