package com.zjtelcom.cpct.count.base.enums;

/**
 * @Auther: anson
 * @Date: 2018/12/28
 * @Description:分群id请求返回状态
 */
public enum ResultEnum {

    SUCCESS("1"),         //成功
    FAILED("1000");       //失败


    private String status;


    ResultEnum(String status){
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }






}
