/*
 * 文件名：Result.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月30日
 * 修改内容：
 */

package com.zjtelcom.cpct.controller.test;

import org.springframework.stereotype.Component;

@Component
public class ResponseResult {
    private String resultCode;

    private String resultMsg;

    private Object resultObject;

    public ResponseResult() {}

    public ResponseResult(String resultCode, String resultMessage, Object resultObject) {
        super();
        this.resultCode = resultCode;
        this.resultMsg = resultMessage;
        this.resultObject = resultObject;
    }

    public ResponseResult(String resultCode, String resultMessage) {
        super();
        this.resultCode = resultCode;
        this.resultMsg = resultMessage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMsg;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMsg = resultMessage;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }

}
