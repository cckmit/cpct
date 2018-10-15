/*
 * 文件名：Company.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月22日
 * 修改内容：
 */

package com.zjtelcom.cpct.pojo;

import java.io.Serializable;

/**
 * 落地分局
 * @author taowenwu
 * @version 1.0
 * @see Company
 * @since JDK1.7
 */

public class Company implements Serializable {
    private String companyId;

    private String level;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

}
