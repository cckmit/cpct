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
	
    private TcpReqCont TcpCont;

    
    private SvcReqCont<T> SvcCont;


	public TcpReqCont getTcpCont() {
		return TcpCont;
	}

	public void setTcpCont(TcpReqCont tcpCont) {
		TcpCont = tcpCont;
	}

	public SvcReqCont<T> getSvcCont() {
		return SvcCont;
	}

	public void setSvcCont(SvcReqCont<T> svcCont) {
		SvcCont = svcCont;
	}
}
