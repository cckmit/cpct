/*
 * 文件名：Result.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月30日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

/**
 * 服务层处理结果
 * @author taowenwu
 * @version 1.0
 * @see Result
 * @since JDK1.7
 */

public class Result {
    private String resultCode;

    private String resultMessage;

    private Object resultObject;

    public Result() {}

    public Result(String resultCode, String resultMessage, Object resultObject) {
        super();
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
        this.resultObject = resultObject;
    }

    public Result(String resultCode, String resultMessage) {
        super();
        this.resultCode = resultCode;
        this.resultMessage = resultMessage;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }

}
