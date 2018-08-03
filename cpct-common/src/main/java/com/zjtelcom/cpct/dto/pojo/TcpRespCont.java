/*
 * 文件名：TcpRespCont.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;


/**
 * 控制对象</br>
 * 每个服务通用的控制信息，当服务端反馈应答时
 * @author taowenwu
 * @version 1.0
 * @see TcpRespCont
 * @since
 */

public class TcpRespCont {

    private String transactionId;

    @JsonFormat(pattern = "yyyyMMddHHmmssSSS")
    private Date rspTime;

    private String sign;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Date getRspTime() {
        return rspTime;
    }

    public void setRspTime(Date rspTime) {
        this.rspTime = rspTime;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

}
