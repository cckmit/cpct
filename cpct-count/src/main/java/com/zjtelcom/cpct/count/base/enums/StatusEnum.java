package com.zjtelcom.cpct.count.base.enums;

/**
 * 活动状态修改
 * 动作            传值         操作后                    满足操作的前提条件
  审核            1000        状态变为已审核                草稿和不通过
  发布            2000        状态变为已发布                已通过
  暂停            3000       状态变为已暂停                已发布
  取消暂停        4000       状态变为已发布                已暂停
  下线           5000       状态变为已下线                已发布
 */
public enum StatusEnum {

    CHECK("审核", "1000"),
    PUBLISH("发布", "2000"),
    PAUSE("暂停","3000"),
    CANCEL_PAUSE("恢复","4000"),
    TAPE_OUT("下线","5000");




    private String statusMsg;
    private String statusCode;

    private StatusEnum(final String statusMsg, final String statusCode) {
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

    public static String getNameByCode(String code){
        for (StatusEnum statusEnum:StatusEnum.values()){
            if(code.equals(statusEnum.getStatusCode())){
                return statusEnum.getStatusMsg();
            }
        }
        return null;
    }

}
