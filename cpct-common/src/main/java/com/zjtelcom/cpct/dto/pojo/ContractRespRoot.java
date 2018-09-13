/*
 * 文件名：ContractRespRoot.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

/**
 * 每个服务的通用框架对象
 * 
 * @author taowenwu
 * @version 1.0
 * @see ContractRespRoot
 * @since JDK1.7
 */

public class ContractRespRoot {
	private TcpRespCont tcpCont;

	private SvcRespCont svcCont;

	public TcpRespCont getTcpCont() {
		return tcpCont;
	}

	public void setTcpCont(TcpRespCont tcpCont) {
		this.tcpCont = tcpCont;
	}

	public SvcRespCont getSvcCont() {
		return svcCont;
	}

	public void setSvcCont(SvcRespCont svcCont) {
		this.svcCont = svcCont;
	}

}
