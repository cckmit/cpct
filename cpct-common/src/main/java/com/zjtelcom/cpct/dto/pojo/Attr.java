/*
 * 文件名：Attr.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

import java.io.Serializable;

/**
 * 扩展属性取值列表
 * @author taowenwu
 * @version 1.0
 * @see Attr
 * @since JDK1.8
 */

public class Attr implements Serializable {
    private String cd;

    private String name;

    private String val;

    private String actType;

    public String getCd() {
        return cd;
    }

    public void setCd(String cd) {
        this.cd = cd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getActType() {
        return actType;
    }

    public void setActType(String actType) {
        this.actType = actType;
    }

}
