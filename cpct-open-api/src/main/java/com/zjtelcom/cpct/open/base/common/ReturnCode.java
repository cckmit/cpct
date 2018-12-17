package com.zjtelcom.cpct.open.base.common;

/**
 * @Auther: anson
 * @Date: 2018/10/26
 * @Description: 集团openapi 统一返回状态值
 */
public enum ReturnCode {

    select(200,"查询"),
    add(201,"新增"),
    update(200,"修改"),
    delete(204,"删除"),
    selectForPage(200,"查询列表");



    private Integer status;
    private String  statusName;

    ReturnCode(Integer status,String  statusName){
          this.status=status;
          this.statusName=statusName;
    }


    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
