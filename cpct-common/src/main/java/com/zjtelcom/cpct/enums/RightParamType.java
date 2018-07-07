package com.zjtelcom.cpct.enums;

/**
 * @Description 左参类型
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
public enum RightParamType {

    LABEL("注智标签", "1000"),
    EXPRESS("表达式", "2000"),
    FIX_VALUE("固定值","3000");

    private String statusMsg;
    private String statusCode;

    private RightParamType(final String statusMsg, final String statusCode) {
        this.statusMsg = statusMsg;
        this.statusCode = statusCode;
    }

    public String getErrorCode() {
        return statusCode;
    }

    public String getErrorMsg() {
        return statusMsg;
    }

    public void setErrorCode(String errorCode) {
        this.statusCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.statusMsg = errorMsg;
    }

}
