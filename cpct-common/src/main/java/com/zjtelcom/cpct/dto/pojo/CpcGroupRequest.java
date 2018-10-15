/*
 * 文件名：CpcGroupRequest.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;


import java.io.Serializable;

/**
 * 集团cpc公共请求对象
 * @author taowenwu
 * @version 1.0
 * @see CpcGroupRequest
 * @since JDK1.7
 */

public class CpcGroupRequest<T> implements Serializable {

    private ContractReqRoot<T> ContractRoot;

	public ContractReqRoot<T> getContractRoot() {
		return ContractRoot;
	}

	public void setContractRoot(ContractReqRoot<T> contractRoot) {
		ContractRoot = contractRoot;
	}
}
