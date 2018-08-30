package com.zjtelcom.cpct.enums;

/**
 * @Auther: anson
 * @Date: 2018/8/28
 * @Description:同步操作类型
 */
public enum SynchronizeType {
    add(0,"新增"),
    update(1,"修改"),
    delete(2,"删除");

    private Integer type;
    private String  typeName;

    SynchronizeType(){

    }

    SynchronizeType(Integer type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }


}
