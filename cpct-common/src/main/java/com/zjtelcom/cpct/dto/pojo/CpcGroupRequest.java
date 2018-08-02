/*
 * 文件名：CpcGroupRequest.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;


/**
 * 集团cpc公共请求对象
 * @author taowenwu
 * @version 1.0
 * @see CpcGroupRequest
 * @since JDK1.7
 */

public class CpcGroupRequest<T> {

    private ContractReqRoot<T> contractRoot;

	public ContractReqRoot<T> getContractRoot() {
		return contractRoot;
	}

	public void setContractRoot(ContractReqRoot<T> contractRoot) {
		this.contractRoot = contractRoot;
	}


}
