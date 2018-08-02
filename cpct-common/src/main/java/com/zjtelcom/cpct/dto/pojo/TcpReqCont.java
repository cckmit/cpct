/*
 * 文件名：TcpCont.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;




/**
 * 控制对象</br>
 * 每个服务通用的控制信息，当客户端发起请求时
 * @author taowenwu
 * @version 1.0
 * @see TcpReqCont
 * @since
 */

public class TcpReqCont {
    private String svcCode;

    private String appKey;

    private String dstSysId;

    private String transactionId;

    private String reqTime;

    private String sign;

    private String version;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


    public String getReqTime() {
		return reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getSvcCode() {
        return svcCode;
    }

    public void setSvcCode(String svcCode) {
        this.svcCode = svcCode;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getDstSysId() {
        return dstSysId;
    }

    public void setDstSysId(String dstSysId) {
        this.dstSysId = dstSysId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
