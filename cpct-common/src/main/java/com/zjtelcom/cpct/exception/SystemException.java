package com.zjtelcom.cpct.exception;

/**
 * @Auther: anson
 * @Date: 2018/8/28
 * @Description:自定义业务异常
 */
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 5099373481522038895L;

    //对应跳转地址
    private String backUrl=null;

    public SystemException(String msg){
        super(msg);
    }

    public SystemException(String msg,String backUrl){
        super(msg);
        this.backUrl = backUrl;
    }


    public String getBackUrl() {
        return backUrl;
    }

    public void setBackUrl(String backUrl) {
        this.backUrl = backUrl;
    }
}
