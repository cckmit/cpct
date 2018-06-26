package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @Description  接口配置参数表
 * @Author pengy
 * @Date 2018/6/26 14:17
 */
@Data
public class InterfaceCfgParam extends BaseEntity{

    private String actType;//  KIP=保持/ADD=新增/MOD=修改/DEL=删除
    private Long interfaceCfgParamId;//记录接口配置参数的主键标识
    private Long interfaceCfgId;//主键标识
    private String url;//描述接口的调用地址URL
    private String Port;//描述服务端的服务端口号
    private String User;//记录有权限访问服务端服务的用户名称
    private String password;//记录有权限访问服务端服务的用户密码
    private Long Timeout;//记录接口的超时时间，单位秒
    private String publicKey;//记录加密的公钥
    private Long regionId;//记录接口的本地网区域标识

}
