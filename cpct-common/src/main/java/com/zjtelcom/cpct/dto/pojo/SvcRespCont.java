/*
 * 文件名：SvcRespCont.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

/**
 * 集团公共服务应答对象
 * @author taowenwu
 * @version 1.0
 * @see SvcRespCont
 * @since JDK1.7
 */

public class SvcRespCont {
    private String resultCode;

    private String resultMsg;

    private Object resultObject;

    public SvcRespCont() {}

    public SvcRespCont(String resultCode, String resultMsg, Object resultObject) {
        super();
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.resultObject = resultObject;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }

}
