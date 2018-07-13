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
    STATUS_CODE_UNDO("撤消","1301"),


    STATUS_CODE_DRAFT("草稿", "2001"),
    STATUS_CODE_PUBLISHED("已发布", "2002"),
    STATUS_CODE_UNPUBLISH("未发布", "2003"),
    STATUS_CODE_CHECKED("已审核", "2004"),
    STATUS_CODE_CHECKING("审核中", "2005"),
    STATUS_CODE_UNCHECK("待审核", "2006"),
    STATUS_CODE_UNPASS("未通过", "2007"),
    STATUS_CODE_STOP("已暂停", "2008"),
    STATUS_CODE_ROLL("已下线", "2009");



    private String statusMsg;
    private String statusCode;

    private StatusCode(final String statusMsg, final String statusCode) {
        this.statusMsg = statusMsg;
        this.statusCode = statusCode;
    }

    public String getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(String statusMsg) {
        this.statusMsg = statusMsg;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
