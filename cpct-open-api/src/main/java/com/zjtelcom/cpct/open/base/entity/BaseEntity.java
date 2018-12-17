package com.zjtelcom.cpct.open.base.entity;

import java.io.Serializable;

/**
 * @Auther: anson
 * @Date: 2018/10/30
 * @Description:
 */
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = -1999342706988857516L;

    private String id;  //唯一id

    private String href; //引用地址


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
