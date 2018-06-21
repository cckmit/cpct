package com.zjtelcom.cpct.enums;

/**
 * @Description ErrorCode
 * @Author pengy
 * @Date 2018/6/20 17:55
 */
public enum ErrorCode {

    SEARCH_EVENT_LIST_FAILURE("查询事件列表失败！", "00001"),
    SEARCH_EVENTSORCE_LIST_FAILURE("查询事件源列表失败！", "00002"),
    DELETE_EVENTSORCE_FAILURE("删除事件源失败！","00003");

    private String errorMsg;
    private String errorCode;

    private ErrorCode(final String errorMsg, final String errorCode) {
        this.errorMsg = errorMsg;
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
