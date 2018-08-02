/*
 * 文件名：ContractRoot.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

/**
 * 每个服务的通用框架对象
 * @author taowenwu
 * @version 1.0
 * @see ContractReqRoot
 * @since JDK1.7
 */

public class ContractReqRoot<T> {
	
    private TcpReqCont tcpCont;

    
    private SvcReqCont<T> svcCont;


	public TcpReqCont getTcpCont() {
		return tcpCont;
	}


	public void setTcpCont(TcpReqCont tcpCont) {
		this.tcpCont = tcpCont;
	}


	public SvcReqCont<T> getSvcCont() {
		return svcCont;
	}


	public void setSvcCont(SvcReqCont<T> svcCont) {
		this.svcCont = svcCont;
	}

    

}
