/*
 * 文件名：ValidateResult.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年11月8日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

import java.io.Serializable;

/**
 * 校验结果
 * @author taowenwu
 * @version 1.0
 * @see ValidateResult
 * @since
 */

public class ValidateResult implements Serializable {
    private boolean flag;

    private String message;

    public ValidateResult() {
        super();
    }

    public ValidateResult(boolean flag, String message) {
        super();
        this.flag = flag;
        this.message = message;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
