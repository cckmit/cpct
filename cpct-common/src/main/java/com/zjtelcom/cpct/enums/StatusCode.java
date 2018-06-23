package com.zjtelcom.cpct.enums;

/**
 * @Description StatusCode
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
public enum StatusCode {

    STATUS_CODE_EFFECTIVE("有效", "1000"),
    STATUS_CODE_FAILURE("无效", "1100"),
    STATUS_CODE_NOTACTIVE("未生效","1200"),
    STATUS_CODE_ARCHIVED("已归档","1300"),
    STATUS_CODE_WILLEFFECTIVE("将生效","1001"),
    STATUS_CODE_WAIT_RESTORED("待恢复","00006"),
    STATUS_CODE_WILLEXPIRE("将失效","1101"),
    STATUS_CODE_TOBEINVALIDATED("待失效","1102"),
    STATUS_CODE_UNDO("撤消","1301");

    private String statusMsg;
    private String statusCode;

    private StatusCode(final String statusMsg, final String statusCode) {
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
