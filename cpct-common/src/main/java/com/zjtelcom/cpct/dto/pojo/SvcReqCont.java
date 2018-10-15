/*
 * 文件名：SvcReqCont.java
 * 版权：Copyright by 南京星邺汇捷网络科技有限公司
 * 描述：
 * 修改人：taowenwu
 * 修改时间：2017年10月27日
 * 修改内容：
 */

package com.zjtelcom.cpct.dto.pojo;

import java.io.Serializable;

/**
 * 业务内容对象</br>
 * 每个服务通用的业务信息内容，当客户端发起请求时
 * @author taowenwu
 * @version 1.0
 * @see SvcReqCont
 * @since JDK1.7
 */

public class SvcReqCont<T>  implements Serializable {
    private AuthenticationInfo authenticationInfo;

    private T requestObject;



    public AuthenticationInfo getAuthenticationInfo() {
        return authenticationInfo;
    }

    public void setAuthenticationInfo(AuthenticationInfo authenticationInfo) {
        this.authenticationInfo = authenticationInfo;
    }

    public T getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(T requestObject) {
        this.requestObject = requestObject;
    }

}
